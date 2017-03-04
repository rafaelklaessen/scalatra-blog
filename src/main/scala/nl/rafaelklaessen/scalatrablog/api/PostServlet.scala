package nl.rafaelklaessen.scalatrablog

import org.scalatra._
import org.json4s._
import org.scalatra.json._
import com.google.firebase
import com.google.firebase._
import com.google.firebase.auth._
import com.google.firebase.database._

class PostServlet extends ScalatraBlogStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }

  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def validEmail(email: String): Boolean = email match{
    case null                                           => false
    case e if e.trim.isEmpty                            => false
    case e if emailRegex.findFirstMatchIn(e).isDefined  => true
    case _                                              => false
  }

  case class Error(error_description: String) 
  case class Success(success_message: String)

  post("/put") {
    val title: String = params.getOrElse("title", halt(400, Error("Please provide a title")))
    val content: String = params.getOrElse("content", halt(400, Error("Please provide content")))
    
    val postReference = FirebaseDatabase.getInstance().getReference("posts")

    postReference.setValue("woopwoop")
    
    println(request.isAjax)
    println(params.get("henkisniethier"))
    List("json", "example")
  }

  post("/delete") {

  }
}