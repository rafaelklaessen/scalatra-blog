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

  case class Error(error_description: String) 
  case class Success(success_message: String)

  post("/create") {
    val title: String = params.getOrElse("title", halt(400, Error("Please provide a title")))
    val content: String = params.getOrElse("content", halt(400, Error("Please provide content")))
    
    if (!session.contains("username")) halt(401, Error("You have to log in before you can create posts"))

    val ref = FirebaseDatabase.getInstance()
    val currentUser = ref.getReference("users").child(session("username").toString)
    val currentPost = ref.getReference("posts").push()

    // Update the user's posts field
    currentUser.child("posts").child(currentPost.getKey()).setValue(true)

    // Create post
    currentPost.child("title").setValue(title)
    currentPost.child("content").setValue(content)
    currentPost.child("owner").setValue(session("username"))
    currentPost.child("timestamp").setValue((System.currentTimeMillis / 1000).toString)

    Success("Successfully created post")
  }

  post("/delete") {

  }
}