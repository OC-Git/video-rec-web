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
import util.Publisher
import util.S3
import models.Client

object API extends Controller {

  def record(client: String) = Action { implicit request =>
    Client.byId(client) match {
      case None => BadRequest("Unknown client")
      case Some(c) => Ok(views.html.api.record(client)).as("text/javascript")
    }
  }

  val form = Form(
    tuple(
      "title" -> text,
      "page" -> text,
      "key" -> text,
      "category" -> text,
      "description" -> text,
      "publishedId" -> optional(text)))

  def videos(client: String) = Action { implicit request =>
    val params = request.queryString.map(t => (t._1, t._2(0)))

    val videos = Video.findAll(client, params)

    val json = Json.generate(videos)

    Ok(json).as("application/json").withHeaders(
      "Access-Control-Allow-Origin" -> "*")
  }

  def upload(client: String) = Action(parse.multipartFormData) { implicit request =>
    request.body.file("file").map { file =>
      form.bindFromRequest.fold(
        errors => {
          Logger.error(errors.toString())
          BadRequest("Failed")
        },
        {
          case (title, page, key, category, description, publishedId) =>
            import java.io.File;
            val filename = file.filename
            Logger.info(filename)

            val contentType = file.contentType
            val tmpFile = new File("/tmp/" + filename)
            file.ref.moveTo(tmpFile, true)
            try {
              Client.byId(client) match {
                case None => BadRequest("Unknown client")
                case Some(c) => {
                  val publishedId = Publisher.publish(tmpFile, title, category, description, c.ytUser, c.ytPwd)
                  val video = Video(NotAssigned, client, new Date(), title, page, key, category, description, publishedId, filename)
                  val id = Video.create(video)
                  S3.upload(client + "/" + id + "." + filename, tmpFile)
                  tmpFile.delete()
                  Ok("created")
                }
              }
            } catch {
              case e: Exception => {
                Logger.error("on upload", e)
                BadRequest(e.getMessage())
              }
            }
        })
    }.getOrElse {
      BadRequest("File missing")
    }
  }
}