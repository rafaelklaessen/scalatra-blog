package nl.rafaelklaessen.scalatrablog

import org.scalatra._
import org.json4s._
import org.scalatra.json._
import com.google.firebase
import com.google.firebase._
import com.google.firebase.auth._
import com.google.firebase.database._

class CategoryServlet extends ScalatraBlogStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }

  case class Error(error_description: String)
  case class Success(success_message: String)

  post("/get") {
    val categoryName: String = "kees"

    Success("not set up")
  }
}