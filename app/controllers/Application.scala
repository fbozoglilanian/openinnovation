package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.User

object Application extends Controller {

  case class UserSignup(email: String, password: String, passwordConfirm: String)

  val userSignUpForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "passwordConfirm" -> nonEmptyText)(UserSignup.apply)(UserSignup.unapply)
      verifying ("Passwords must match", f => f.password == f.passwordConfirm)
      verifying ("Email already exist", f => User.getByEmail(f.email) match {
        case None => true
        case Some(u) => false
      }))

  def index = Action {
    implicit request =>
      Ok(views.html.index("Welcome to Open Innovation.",
        session.get("email").map { email =>
          User.getByEmail(email)
        }.getOrElse {
          None
        }))
  }

  def signup = Action {
    Ok(views.html.signup(userSignUpForm))
  }

  def newUser = Action { implicit request =>
    userSignUpForm.bindFromRequest.fold(
      errors => BadRequest(views.html.signup(errors)),
      userSubmit => {
        models.User.add(userSubmit.email, userSubmit.password) match {
          case None => Ok(views.html.index("Error: User not added", None))
          case Some(user) => Redirect(routes.Application.index).withSession("email" -> user.email)
        }
      })
  }

  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text) verifying ("Invalid email or password", result => result match {
        case (email, password) => User.authenticate(email, password).isDefined
      }))

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession("email" -> user._1))
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out")
  }

}