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
    val key: String = params.getOrElse("key", halt(400, Error("Please provide a post key")))

    // Make sure the post exists and the user is logged in
    if (!session.contains("username")) halt(401, Error("You have to log in before you can delete posts"))
    if (!Posts.postExists(key)) halt(400, Error("Post doesn't exist"))

    val post = Posts.get(key)

    // Make sure current logged in user actually is the post owner
    if (post.owner != session("username")) halt(400, Error("Post doesn't exist"))

    // Delete post from Firebase
    val ref = FirebaseDatabase.getInstance().getReference("posts")
    val currentPost = ref.child(key)

    currentPost.removeValue()

    Success("Post was successfully deleted")
  }

  post("/update") {
    val key: String = params.getOrElse("key", halt(400, Error("Please provide a post key")))
    val fieldsJson: String = params.getOrElse("fields", halt(400, Error("Please provide fields")))
    var fields: Map[String, String] = Map()
    
    try {
      fields = parse(fieldsJson).extract[Map[String, String]]
    } catch {
      case jpe: com.fasterxml.jackson.core.JsonParseException => halt(400, Error("Please provide valid fields"))
    }

    // Make sure the post exists and the user is logged in
    if (!session.contains("username")) halt(401, Error("You have to log in before you can update posts"))
    if (!Posts.postExists(key)) halt(400, Error("Post doesn't exist"))

    val post = Posts.get(key)

    // Make sure current logged in user actually is the post owner
    if (post.owner != session("username")) halt(400, Error("Post doesn't exist"))

    // Make sure we're getting only valid fields
    val filteredFields = fields.filterKeys(_ match {
      case "title" | "content" => true
      case _ => false
    })

    val postsRef = FirebaseDatabase.getInstance().getReference("posts")
    val currentPost = postsRef.child(key)
    
    for (field <- filteredFields.keys) {
      currentPost.child(field).setValue(filteredFields(field))
    }

    Success("Successfully updated post")
  }
}