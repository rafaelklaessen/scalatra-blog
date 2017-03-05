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
  case class CategorySuccess(success_message: String, key: String)
  
  post("/get") {
    val key: String = params.getOrElse("key", halt(400, Error("Please provide a category key")))

    if (!Categories.categoryExists(key)) halt(400, Error("Category doesn't exist"))

    val category = Categories.get(key)

    category
  }

  post("/create") {
    val name: String = params.getOrElse("name", halt(400, Error("Please provide a category name")))

    // Make sure user is logged in
    if (!session.contains("username")) halt(401, Error("You have to log in before you can create categories"))

    val ref = FirebaseDatabase.getInstance().getReference("categories")
    val currentCategory = ref.push()

    currentCategory.child("name").setValue(name)

    CategorySuccess("Successfully created category", currentCategory.getKey())
  }
  
  post("/delete") {
    val key: String = params.getOrElse("key", halt(400, Error("Please provide a key")))

    // Make sure a user is logged in
    if (!session.contains("username")) halt(401, Error("You have to log in before you can delete posts"))

    val category = Categories.get(key)

    // Delete category from posts
    for (post <- category.posts.keys) {
      Categories.deletePost(key, post)
    }

    // Delete category from Firebase 
    val ref = FirebaseDatabase.getInstance().getReference("categories")
    val currentCategory = ref.child(key)

    currentCategory.removeValue()
  }
}