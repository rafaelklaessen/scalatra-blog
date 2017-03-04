package nl.rafaelklaessen.scalatrablog

case class User(email: String, name: String, password: String, posts: Map[String, Boolean] = Map())