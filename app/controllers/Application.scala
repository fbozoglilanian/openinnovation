package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {

  case class UserSignup(email: String, password: String, passwordConfirm: String)

  val userSignUpForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "passwordConfirm" -> nonEmptyText)(UserSignup.apply)(UserSignup.unapply)
      verifying ("Passwords must match", f => f.password == f.passwordConfirm))

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def signup = Action {
    Ok(views.html.signup(userSignUpForm))
  }

  def newUser = Action { implicit request =>
    userSignUpForm.bindFromRequest.fold(
      errors => BadRequest(views.html.signup(errors)),
      userSubmit => {
        models.User.add(userSubmit.email, userSubmit.password) match {
          case None => Ok(views.html.index("Error: User not added"))
          case Some(user) => Ok(views.html.index("Welcome: " + user.email)).withSession("user" -> user.email)
        }
      })
  }

}