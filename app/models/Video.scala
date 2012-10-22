package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.util.Date

case class Video(id: Pk[Long], client: String, date: Date, title: String,
  page: String, key: String, category: String, description: String, publishedId: Option[String], filename: Option[String]) {
  val url = util.S3.s3Access().website + "/" + client + "/" + id + "." + filename
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
      get[Option[String]]("publishedId") ~
      get[Option[String]]("filename") map {
        case id ~ client ~ date ~ title ~ page ~ key ~ category ~ description ~ publishedId ~ filename =>
          Video(id, client, date, title, page, key, category, description, publishedId, filename)
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
        INSERT INTO Video(client, date, title, page, key, category, description, publishedId, filename) 
               VALUES ({client}, {date}, {title}, {page}, {key}, {category}, {description}, {publishedId}, {filename})
        """)
        .on("client" -> video.client.slice(0, 255),
          "date" -> video.date,
          "title" -> video.title.slice(0, 255),
          "page" -> video.page.slice(0, 255),
          "key" -> video.key.slice(0, 255),
          "category" -> video.category.slice(0, 255),
          "description" -> video.description.slice(0, 255),
          "publishedId" -> video.publishedId,
          "filename" -> video.filename).executeInsert()
    } match {
      case Some(long) => long
      case None => throw new IllegalStateException("No key generated")
    }

  }

  def update(id: Long, video: Video) = {
    DB.withConnection { implicit connection =>
      SQL("""
        UPDATE Video SET client={client}, date={date}, title={title}, page={page}, key={key}, category={category}, description={description}, publishedId={publishedId}, filename={filename} WHERE id={id}
        """)
        .on("client" -> video.client.slice(0, 255),
          "date" -> video.date,
          "title" -> video.title.slice(0, 255),
          "page" -> video.page.slice(0, 255),
          "key" -> video.key.slice(0, 255),
          "category" -> video.category.slice(0, 255),
          "description" -> video.description.slice(0, 255),
          "publishedId" -> video.publishedId,
          "filename" -> video.filename,
          "id" -> id).executeInsert()
    }
  }

  def published(video: Long, publishedId: String) = {
    DB.withConnection { implicit connection =>
      SQL("""
        UPDATE Video SET publishedId={publishedId} WHERE id={id}
        """)
        .on("publishedId" -> publishedId.slice(0, 255),
          "id" -> video).executeInsert()
    }
  }
}