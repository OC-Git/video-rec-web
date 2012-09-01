package controllers

import play.api.mvc._
import play.api._
import play.api.libs.ws.WS

object Clients extends Controller {
  /*
  val ClientId = "307418826933.apps.googleusercontent.com"
  val ClientSecret = "6O6yaRcx7cgomDkLf1pHLijU"

  def auth(client: String) = Action { implicit request =>
    val q = "response_type=code" +
      "client_id=" + ClientId +
      "redirect_uri=" + routes.Clients.callback().absoluteURL() +
      "scope=" +
      "state=" + client +
      "access_type=offline" +
      "approval_prompt=auto"

    Redirect("https://accounts.google.com/o/oauth2/auth?" + q)
  }
  def callback = Action { implicit request =>
    val code = request.queryString("code")
    val client = request.queryString("state")
    val params = Map("code" -> Seq(code),
      "client_id" -> Seq(ClientId),
      "client_secret" -> Seq(ClientSecret),
      "redirect_uri" -> Seq(routes.Clients.callback().absoluteURL()),
      "grant_type" -> Seq("authorization_code"))
    Async {
      WS.url("https://accounts.google.com/o/oauth2/token").post(params).map { response =>
        val accessToken = response.json \ "access_token"
        val refreshToken = response.json \ "refresh_token"
        val expiresIn = response.json \ "expires_in"
        val tokenType = response.json \ "token_type"
        Ok("")
      }
    }
  }
*/
}