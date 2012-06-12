package controllers

import play.api._
import play.api.db._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json._
import anorm._
import models.Video
import java.util.Date

object API extends Controller {

  def record(client: String) = Action {
    Ok(views.html.api.record.render(client)).as("text/javascript")
  }

  val form = Form(
    tuple(
      "title" -> text,
      "page" -> text,
      "category" -> text,
      "description" -> text,
      "publishedId" -> text))

  def published(client: String) = Action { implicit request =>
    form.bindFromRequest.fold(
      errors => BadRequest("Failed"),
      {
        case (title, page, category, description, publishedId) =>
          Video.create(Video(NotAssigned, client, new Date(), title, page, category, description, publishedId))
          Ok("created")
      })
  }

  def videos(client: String) = Action { implicit request =>
    val videos = Video.findAll().map { v => toJson(v.asMap) }

    Ok(toJson(Map("videos" -> videos)))
  }

}