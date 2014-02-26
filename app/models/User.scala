package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(email: String)

object User {

  val userParse = {
    get[String]("user.email") map {
        case email => User(email)
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
        """
      ).on(
        'email -> email,
        'password -> password
      ).executeUpdate()
      
      Some(new User(email))
    }
  }
  
  
  def getByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email
      ).as(User.userParse.singleOpt)
    }
  }
}
