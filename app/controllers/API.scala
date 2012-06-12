package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object API extends Controller {

  def record(client: String) = Action {
    Ok(views.html.api.record.render(client)).as("text/javascript")
  }

  def published(client: String) = Action { implicit request =>
    val form = Form(
      tuple(
        "title" -> text,
        "page" -> text,
        "category" -> text,
        "description" -> text,
        "id" -> text))
    val (title, page, category, description, id) = form.bindFromRequest.get
    println(client + " " + title + " " + page + " " + category + " " + description + " " + id)
    Ok("")
  }

}