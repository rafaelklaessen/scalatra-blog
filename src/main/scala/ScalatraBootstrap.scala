import nl.rafaelklaessen.scalatrablog._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new ScalatraBlogServlet, "/*")
    context.mount(new ScalatraBlogApiServlet, "/api/*")
  }
}
