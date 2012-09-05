package models

import play.api.db._
import play.api.Logger
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.util.Date

case class Client(id: Pk[String], created: Date, user: String, pwd: String, ytToken: Option[String])

object Client {

  val simple = {
    get[Pk[String]]("id") ~
      get[Date]("created") ~
      get[String]("usr") ~
      get[String]("pwd") ~
      get[Option[String]]("yt_token") map {
        case id ~ created ~ user ~ pwd ~ ytToken =>
          Client(id, created, user, pwd, ytToken)
      }
  }

  def byId(client: String): Option[Client] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM Client WHERE id={client}")
        .on("client" -> client)
        .as(Client.simple *).headOption
    }
  }

  def byUser(user: String): Option[Client] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM Client WHERE usr={user}")
        .on("user" -> user)
        .as(Client.simple *).headOption
    }
  }

  def refreshToken(client: String, token: String) = {
    Logger.info("refreshtoken(" + client + ")=" + token)
    DB.withConnection { implicit connection =>
      SQL("""
        UPDATE Client SET yt_token={ytToken} WHERE id={client}
        """)
        .on("ytToken" -> token.slice(0, 255),
          "client" -> client).executeInsert()
    }
  }

}