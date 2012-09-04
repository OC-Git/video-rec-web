package controllers

import play.api.mvc._
import play.api._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Promise

import models.Client

object Clients extends Controller {

  val ClientId = "307418826933.apps.googleusercontent.com"
  val ClientSecret = "6O6yaRcx7cgomDkLf1pHLijU"

  def auth(client: String) = Action { implicit request =>
    val q = "response_type=code" +
      "&client_id=" + ClientId +
      "&redirect_uri=" + routes.Clients.callback().absoluteURL() +
      "&scope=https://gdata.youtube.com" +
      "&state=" + client +
      "&access_type=offline" +
      "&approval_prompt=force"

    Redirect("https://accounts.google.com/o/oauth2/auth?" + q)
  }

  def callback = Action { implicit request =>
    val code = request.queryString("code")
    val client = request.queryString("state")
    val params = Map("code" -> code,
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

        Client.refreshToken(client.head, refreshToken.asOpt[String].get)
        Ok("You are authenticated, thank you!")
      }
    }
  }

  def exchange(refreshToken: String): Promise[String] = {
    val params = Map(
      "client_id" -> Seq(ClientId),
      "client_secret" -> Seq(ClientSecret),
      "refresh_token" -> Seq(refreshToken),
      "grant_type" -> Seq("refresh_token"))
    WS.url("https://accounts.google.com/o/oauth2/token").post(params).map { response =>
      val accessToken = response.json \ "access_token"
      Logger.info("Refresh from " + refreshToken + ": " + accessToken.asOpt[String].get)
      accessToken.asOpt[String].get
    }
  }
}