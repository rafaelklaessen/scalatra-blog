package nl.rafaelklaessen.scalatrablog

import scala.io.Source
import org.scalatra._
import org.json4s._
import org.scalatra.json._
import org.json4s.jackson.JsonMethods._
import com.google.firebase
import com.google.firebase._
import com.google.firebase.auth._
import com.google.firebase.database._

object Categories {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  def get(key: String): Category = {
    val credential = "EZvAO9apJ0O563x8njmdDnNhOx5ZSRaHvcos4Q8w"
    val jsonUrl = "https://scalatra-blog.firebaseio.com/categories/" + key + ".json?auth=" + credential
  
    // Get category JSON & parse it 
    val category = parse(Source.fromURL(jsonUrl).mkString).extract[Category]
  
    category
  }

  def categoryExists(key: String): Boolean = {
    val credential = "EZvAO9apJ0O563x8njmdDnNhOx5ZSRaHvcos4Q8w"
    val jsonUrl = "https://scalatra-blog.firebaseio.com/categories/" + key + ".json?auth=" + credential

    // Get category JSON 
    val category = Source.fromURL(jsonUrl).mkString

    // If category is "null", it doesn't categoryExists
    category != "null"
  }

  def addPost(categoryKey: String, postKey: String) = {
    val ref = FirebaseDatabase.getInstance()
    val currentPost = ref.getReference("posts").child(postKey)
    val currentCategory = ref.getReference("categories").child(categoryKey)

    currentPost.child("categories").child(categoryKey).setValue(true)
    currentCategory.child("posts").child(postKey).setValue(true)
  }

  def deletePost(categoryKey: String, postKey: String) = {
    val ref = FirebaseDatabase.getInstance()
    val currentPost = ref.getReference("posts").child(postKey)
    val currentCategory = ref.getReference("categories").child(categoryKey)

    currentPost.child("categories").child(categoryKey).removeValue()
    currentCategory.child("posts").child(postKey).removeValue()
  } 
}