package controllers

import play.api._
import play.api.mvc._

object API extends Controller {

  def record(client: String) = Action {
    Ok(views.html.api.record.render(client)).as("text/javascript")
  }

}