package nl.rafaelklaessen.scalatrablog

import scala.io.Source
import org.scalatra._
import org.json4s._
import org.scalatra.json._
import org.json4s.jackson.JsonMethods._

object Users {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  private val credential = "EZvAO9apJ0O563x8njmdDnNhOx5ZSRaHvcos4Q8w"
  private val firebaseUrl = "https://scalatra-blog.firebaseio.com"

  def get(username: String): User = {
    val jsonUrl = firebaseUrl + "/users/" + username + ".json?auth=" + credential

    // Get user JSON & parse it
    val user = parse(Source.fromURL(jsonUrl).mkString).extract[User]

    user
  }

  def getAll: Map[String, User] = {
    val jsonUrl = firebaseUrl + "/users.json?auth=" + credential
  
    // Get users JSON & parse it
    val users = parse(Source.fromURL(jsonUrl).mkString).extract[Map[String, User]]

    users
  }

  def userExists(username: String): Boolean = {
    val jsonUrl = firebaseUrl + "/users/" + username + ".json?auth=" + credential

    // Get user JSON
    val user = Source.fromURL(jsonUrl).mkString

    // If user is "null", he doesn't exist
    user != "null"
  }
}