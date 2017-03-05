import nl.rafaelklaessen.scalatrablog._
import org.scalatra._
import javax.servlet.ServletContext
import com.google.firebase
import com.google.firebase._
import com.google.firebase.auth._
import com.google.firebase.database._
import java.io.File
import java.io.FileInputStream

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new ScalatraBlogServlet, "/*")
    context.mount(new UserServlet, "/api/user/*")
    context.mount(new PostServlet, "/api/posts/*")
    context.mount(new CategoryServlet, "/api/categories/*")

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
}
