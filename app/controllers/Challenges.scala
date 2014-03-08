package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views.html.defaultpages.unauthorized

object Challenges extends Controller with Secured {

  val ChallengeForm = Form(
    tuple(
      "challenge" -> nonEmptyText(minLength = 10, maxLength = 256),
      "motivation" -> text(minLength = 0, maxLength = 1024)))

  def view(id: Long) =  Action {
    implicit request =>
      models.Challenge.getChallenge(id) match {
        case None => BadRequest(views.html.challenges.view(None, GetLogedUser(request)))
        case Some(challenge) =>  Ok(views.html.challenges.view(Some(challenge), GetLogedUser(request)))
      }
  }
  
  
  def add() = IsAuthenticated { email =>
    implicit request =>
      models.User.getByEmail(email) match {
        case None => Unauthorized("You need to be logged to see this section")
        case Some(user) =>  Ok(views.html.challenges.add(ChallengeForm, GetLogedUser(request)))
      }
  }

  def saveChallenge() = IsAuthenticated { email =>
    implicit request =>
      models.User.getByEmail(email) match {
        case None => Forbidden
        case Some(user) =>
          {
            ChallengeForm.bindFromRequest.fold(
              formWithErrors => BadRequest(views.html.challenges.add(formWithErrors, GetLogedUser(request))),
              challenge => {
                user.id match {
                  case None => BadRequest(views.html.challenges.add(ChallengeForm, GetLogedUser(request))).flashing("error" -> "Challenge not added")
                  case Some(userId) => {
                    models.Challenge.add(challenge._1, challenge._2, userId) match {
                      case None => BadRequest(views.html.challenges.add(ChallengeForm, GetLogedUser(request))).flashing("error" -> "Challenge not added")
                      case Some(challenge) =>
                        challenge.id match {
                          case None => BadRequest(views.html.challenges.add(ChallengeForm, GetLogedUser(request))).flashing("error" -> "Challenge not added")
                          case Some(challengeId) => Redirect(routes.Challenges.view(challengeId)).flashing("message" -> "New Challenge added")
                        }

                    }
                  }

                }

              })
          }
      }
  }

}