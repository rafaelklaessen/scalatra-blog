package nl.rafaelklaessen.scalatrablog

case class Post(title: String, content: String, timestamp: String, owner: String, categories: Map[String, Boolean] = Map())