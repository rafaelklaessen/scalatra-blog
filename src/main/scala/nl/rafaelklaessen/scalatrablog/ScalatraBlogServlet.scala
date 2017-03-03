package nl.rafaelklaessen.scalatrablog

import org.scalatra._

class ScalatraBlogServlet extends ScalatraBlogStack {
  get("/") {
    contentType="text/html"

    ssp("/index")
  }
}