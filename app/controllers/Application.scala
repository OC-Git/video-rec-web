package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def apps = Action { implicit request =>
    Ok(views.html.apps())
  }

  def api = Action { implicit request =>
    Ok(views.html.apidoc())
  }

  def plans = Action { implicit request =>
    Ok(views.html.plans())
  }

}