package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.util.Date

case class Video(id: Pk[Long], client: String, date: Date, title: String,
  page: String, category: String, description: String, publishedId: String) {
  def asMap = Map(
    "id" -> id.toString,
    "date" -> date.toString,
    "client" -> client,
    "title" -> title,
    "page" -> page,
    "category" -> category,
    "description" -> description,
    "publishId" -> publishedId)
}

object Video {

  val simple = {
    get[Pk[Long]]("id") ~
      get[String]("client") ~
      get[Date]("date") ~
      get[String]("title") ~
      get[String]("page") ~
      get[String]("category") ~
      get[String]("description") ~
      get[String]("publishedId") map {
        case id ~ client ~ date ~ title ~ page ~ category ~ description ~ publishedId =>
          Video(id, client, date, title, page, category, description, publishedId)
      }
  }

  def findAll(): Seq[Video] = {
    DB.withConnection { implicit connection =>
      SQL("select * from Video").as(Video.simple *)
    }
  }

  def create(video: Video): Unit = {
    println(video)
    DB.withConnection { implicit connection =>
      SQL("""
        INSERT INTO Video(client, date, title, page, category, description, publishedId) 
               VALUES ({client}, {date}, {title}, {page}, {category}, {description}, {publishedId})
        """)
        .on("client" -> video.client,
          "date" -> video.date,
          "title" -> video.title,
          "page" -> video.page,
          "category" -> video.category,
          "description" -> video.description,
          "publishedId" -> video.publishedId).executeInsert()
    }
  }

}