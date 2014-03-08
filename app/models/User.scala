package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(id: Option[Long], email: String)

object User {

  val userParse = {
    get[Long]("user.user_id") ~
    get[String]("user.email") map {
      case id ~ email => User(Some(id), email)
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
          'password -> password).executeInsert()

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
         email = {email} and password = {password}
        """).on(
          'email -> email,
          'password -> password).as(User.userParse.singleOpt)
    }
  }

}
