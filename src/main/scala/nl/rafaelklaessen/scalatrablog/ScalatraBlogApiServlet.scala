package nl.rafaelklaessen.scalatrablog

import org.scalatra._
import org.json4s._
import org.scalatra.json._
import com.google.firebase
import com.google.firebase._
import com.google.firebase.auth._
import com.google.firebase.database._
import java.io.File
import java.io.FileInputStream

class ScalatraBlogApiServlet extends ScalatraBlogStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  
    val apps = FirebaseApp.getApps()

    if (apps.isEmpty()) {
      val serviceAccount = new FileInputStream("firebase-auth.json")

      val options = new FirebaseOptions.Builder()
        .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
        .setDatabaseUrl("https://scalatra-blog.firebaseio.com")
        .build()
    
      FirebaseApp.initializeApp(options)
    }
  }

  post("user/login") {

  }

  case class Error(error_description: String) 

  post("/post/put") {
    val title: String = params.getOrElse("title", halt(400, Error("Please provide a title")))
    val content: String = params.getOrElse("content", halt(400, Error("Please provide content")))
    
    val postReference = FirebaseDatabase.getInstance().getReference("posts")

    postReference.setValue("woopwoop")
    
    println(request.isAjax)
    println(params.get("henkisniethier"))
    List("json", "example")
  }

  post("/post/delete") {

  }

}
