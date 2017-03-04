package nl.rafaelklaessen.scalatrablog

import scala.io.Source
import org.scalatra._
import org.json4s._
import org.scalatra.json._
import org.json4s.jackson.JsonMethods._

object Posts {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  def get(key: String): Post = {
    val credential = "EZvAO9apJ0O563x8njmdDnNhOx5ZSRaHvcos4Q8w"
    val jsonUrl = "https://scalatra-blog.firebaseio.com/posts/" + key + ".json?auth=" + credential
  
    // Get post JSON & parse it
    val post = parse(Source.fromURL(jsonUrl).mkString).extract[Post]
    
    post
  }

  def postExists(key: String): Boolean = {
    val credential = "EZvAO9apJ0O563x8njmdDnNhOx5ZSRaHvcos4Q8w"
    val jsonUrl = "https://scalatra-blog.firebaseio.com/posts/" + key + ".json?auth=" + credential

    // Get post JSON
    val post = Source.fromURL(jsonUrl).mkString

    // If post is "null", it doesn't exist 
    post != "null"
  }
}