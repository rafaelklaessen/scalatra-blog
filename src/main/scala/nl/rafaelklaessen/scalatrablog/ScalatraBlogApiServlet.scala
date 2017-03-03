package nl.rafaelklaessen.scalatrablog

import org.scalatra._
import org.json4s._
import org.scalatra.json._

class ScalatraBlogApiServlet extends ScalatraBlogStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }

  post("user/login") {

  }

  case class Error(error_description: String) 

  post("/post/put") {
    val title: String = params.getOrElse("title", halt(400, Error("Please provide a title")))
    val content: String = params.getOrElse("content", halt(400, Error("Please provide content")))
    
    
    println(request.isAjax)
    println(params.get("henkisniethier"))
    List("json", "example")
  }

  post("/post/delete") {

  }

}
