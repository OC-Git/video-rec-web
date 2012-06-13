package controllers

import play.api._
import play.api.db._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import anorm._
import models.Video
import java.util.Date
import com.codahale.jerkson.Json

object API extends Controller {

  def record(client: String) = Action { implicit request =>
    Ok(views.html.api.record(client)).as("text/javascript")
  }

  val form = Form(
    tuple(
      "title" -> text,
      "page" -> text,
      "key" -> text,
      "category" -> text,
      "description" -> text,
      "publishedId" -> text))

  def published(client: String) = Action { implicit request =>
    form.bindFromRequest.fold(
      errors => BadRequest("Failed"),
      {
        case (title, page, key, category, description, publishedId) =>
          Video.create(Video(NotAssigned, client, new Date(), title, page, key, category, description, publishedId))
          Ok("created")
      })
  }

  def videos(client: String) = Action { implicit request =>
    val params = request.queryString.map(t => (t._1, t._2(0)))

    val videos = Video.findAll(client, params)

    println("found videos: " + videos.size)

    val json = Json.generate(videos)

    Ok(json).as("application/json")
  }

}