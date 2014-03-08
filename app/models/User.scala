package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import org.mindrot.jbcrypt.BCrypt

case class User(id: Option[Long], email: String)
case class SecuredUser(id: Long, email: String, password: String)

object User {

  val userParse = {
    get[Long]("user.user_id") ~
    get[String]("user.email") map {
      case id ~ email => User(Some(id), email)
    }
  }
  
  val SecuredUserParse = {
    get[Long]("user.user_id") ~
    get[String]("user.email") ~
    get[String]("user.password") map {
      case id ~ email ~ password => SecuredUser(id, email, password)
    }
  }

  def add(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user (email, password)
          values (
            {email}, {password}
          )
        """).on(
          'email -> email,
          'password -> BCrypt.hashpw(password, BCrypt.gensalt(12))).executeInsert()

    }  match {
        case Some(id) => Some(new User(Some(id), email)) // The Primary Key
        case None     => None
    }
  }

  def getByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email).as(User.userParse.singleOpt)
    }
  }

  def getUserById(id: Long): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where user_id = {user_id}").on(
        'user_id -> id).as(User.userParse.singleOpt)
    }
  }

  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where 
         email = {email}
        """).on(
          'email -> email).as(User.SecuredUserParse.singleOpt) match {
            case Some(securedUser) => BCrypt.checkpw(password, securedUser.password) match {
              case true => getUserById(securedUser.id)
              case false => None
            }
            case None => None
          }
    }
  }

}
