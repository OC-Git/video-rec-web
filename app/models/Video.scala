package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.util.Date

case class Video(id: Pk[Long], client: String, date: Date, title: String,
  page: String, key: String, category: String, description: String, publishedId: String) {
}

object Video {

  val simple = {
    get[Pk[Long]]("id") ~
      get[String]("client") ~
      get[Date]("date") ~
      get[String]("title") ~
      get[String]("page") ~
      get[String]("key") ~
      get[String]("category") ~
      get[String]("description") ~
      get[String]("publishedId") map {
        case id ~ client ~ date ~ title ~ page ~ key ~ category ~ description ~ publishedId =>
          Video(id, client, date, title, page, key, category, description, publishedId)
      }
  }

  def findAll(client: String, p: Map[String, String]): Seq[Video] = {
    val where = "client={client}" +
      (if (p.contains("key")) { " AND key='" + p("key") + "'" } else "") +
      (if (p.contains("page")) { " AND page='" + p("page") + "'" } else "")
    val count = if (p.contains("count")) { Integer.parseInt(p("count")) } else { 10 }
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM Video WHERE " + where + " ORDER BY date DESC LIMIT {count}")
        .on("client" -> client, "count" -> count)
        .as(Video.simple *)
    }
  }

  def create(video: Video): Long = {
    println(video)
    DB.withConnection { implicit connection =>
      SQL("""
        INSERT INTO Video(client, date, title, page, key, category, description, publishedId) 
               VALUES ({client}, {date}, {title}, {page}, {key}, {category}, {description}, {publishedId})
        """)
        .on("client" -> video.client.slice(0, 255),
          "date" -> video.date,
          "title" -> video.title.slice(0, 255),
          "page" -> video.page.slice(0, 255),
          "key" -> video.key.slice(0, 255),
          "category" -> video.category.slice(0, 255),
          "description" -> video.description.slice(0, 255),
          "publishedId" -> video.publishedId.slice(0, 255)).executeInsert()
    } match {
      case Some(long) => long
      case None => throw new IllegalStateException("No key generated")
    }

  }
}