package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Challenge(id: Option[Long], challenge: String, motivation: String, creator: Option[User])

object Challenge {

  val challengeParse = {
    get[Long]("challenge.challenge_id") ~
    get[String]("challenge.challenge") ~
    get[String]("challenge.motivation") ~
    get[Long]("challenge.added_by_user_id") map {
      case id ~ challenge ~ motivation ~ addedByUserId => Challenge(Some(id), challenge, motivation, models.User.getUserById(addedByUserId))
    }
  }

  def add(challenge: String, motivation: String, addedByUserId: Long): Option[Challenge] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into challenge (challenge, motivation, added_by_user_id)
          values (
            {challenge}, {motivation}, {added_by_user_id}
          )
        """).on(
          'challenge -> challenge,
          'motivation -> motivation,
          'added_by_user_id -> addedByUserId).executeInsert()
    }  match {
        case Some(id) => Some(new Challenge(Some(id), challenge, motivation, models.User.getUserById(addedByUserId))) // The Primary Key
        case None     => None
    }
  }

  def getChallenge(challengeId: Long): Option[Challenge] = {
    DB.withConnection { implicit connection =>
      SQL("select * from challenge where challenge_id = {challengeId}").on(
        'challengeId -> Some(challengeId)).as(Challenge.challengeParse.singleOpt)
    }
  }

}
