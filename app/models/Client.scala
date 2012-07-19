package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.util.Date

case class Client(id: Pk[String], created: Date, ytUser: String, ytPwd: String)

object Client {

  val simple = {
    get[Pk[String]]("id") ~
      get[Date]("created") ~
      get[String]("ytUser") ~
      get[String]("ytPwd") map {
        case id ~ created ~ ytUser ~ ytPwd =>
          Client(id, created, ytUser, ytPwd)
      }
  }

  def byId(client: String): Option[Client] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM Client WHERE id={client}")
        .on("client" -> client)
        .as(Client.simple *).headOption
    }
  }
}