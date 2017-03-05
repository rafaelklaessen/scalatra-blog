package nl.rafaelklaessen.scalatrablog

import scala.io.Source
import org.scalatra._
import org.json4s._
import org.scalatra.json._
import org.json4s.jackson.JsonMethods._

object Posts {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  private val credential = "EZvAO9apJ0O563x8njmdDnNhOx5ZSRaHvcos4Q8w"
  private val firebaseUrl = "https://scalatra-blog.firebaseio.com"

  def get(key: String): Post = {
    val jsonUrl = firebaseUrl + "/posts/" + key + ".json?auth=" + credential
  
    // Get post JSON & parse it
    val post = parse(Source.fromURL(jsonUrl).mkString).extract[Post]
    
    post
  }

  def getByUsername(username: String): Map[String, Post] = {
    val user = Users.get(username)

    val postMap = scala.collection.mutable.Map[String, Post]()

    for (postKey <- user.posts.keys) {
      postMap(postKey) = Posts.get(postKey)
    }

    // Return immutable map
    postMap.toMap
  }

  def getAll: Map[String, Post] = {
    val jsonUrl = firebaseUrl + "/posts.json?auth=" + credential + "&orderByChild=timestamp"
  
    // Get posts JSON & parse it
    val posts = parse(Source.fromURL(jsonUrl).mkString).extract[Map[String, Post]]
    
    posts
  }

  def postExists(key: String): Boolean = {
    val jsonUrl = firebaseUrl + "/posts/" + key + ".json?auth=" + credential

    // Get post JSON
    val post = Source.fromURL(jsonUrl).mkString

    // If post is "null", it doesn't exist 
    post != "null"
  }
}