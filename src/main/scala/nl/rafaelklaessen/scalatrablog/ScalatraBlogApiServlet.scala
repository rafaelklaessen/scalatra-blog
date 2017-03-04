package nl.rafaelklaessen.scalatrablog

import org.scalatra._
import org.json4s._
import org.scalatra.json._
import com.google.firebase
import com.google.firebase._
import com.google.firebase.auth._
import com.google.firebase.database._
import org.mindrot.jbcrypt._

class ScalatraBlogApiServlet extends ScalatraBlogStack with JacksonJsonSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  before() {
    contentType = formats("json")
  }

  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def validEmail(email: String): Boolean = email match{
    case null                                           => false
    case e if e.trim.isEmpty                            => false
    case e if emailRegex.findFirstMatchIn(e).isDefined  => true
    case _                                              => false
  }

  case class Error(error_description: String) 
  case class Success(success_message: String)

  post("/user/login") {
    val username: String = params.getOrElse("username", halt(400, Error("Please provide a username")))
    val password: String = params.getOrElse("password", halt(400, Error("Please provide a password")))

    if (!Users.userExists(username)) halt(400, Error("That user doesn't exist"))

    val user = Users.get(username)

    if (BCrypt.checkpw(password, user.password)) {
      session("username") = username
      Success("Successfully logged in")
    } else {
      halt(400, Error("Wrong password"))
    }
  }

  post("/user/register") {
    val username: String = params.getOrElse("username", halt(400, Error("Please provide a username")))
    val email: String = params.getOrElse("email", halt(400, Error("Please provide an email")))
    val name: String = params.getOrElse("name", halt(400, Error("Please provide a name")))
    val password: String = params.getOrElse("password", halt(400, Error("Please provide a password")))

    if (username.trim().length < 4) {
      halt(400, Error("Please provide a longer username"))
    } else if (username.contains(".")) {
      halt(400, Error("Username may not contain dots"))
    }

    if (!validEmail(email)) halt(400, Error("Please provide a valid emaill adress"))
    if (name.trim().length < 4) halt(400, Error("Please provide a valid name"))
    if (password.trim().length < 4) halt(400, Error("Please provide a longer password"))

    // Make sure user doesn't exist yet
    if (Users.userExists(username)) halt(400, Error("Username already taken"))

    val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
    
    val usersRef = FirebaseDatabase.getInstance().getReference("users")
    val currentUser = usersRef.child(username)

    currentUser.child("email").setValue(email)
    currentUser.child("name").setValue(name)
    currentUser.child("password").setValue(hashedPassword)

    session("username") = username

    Success("User successfully registered & logged in")
  }

  post("/user/logout") {
    // Delete username from session
    session -= "username"

    Success("Successfully logged out")
  }

  post("/user/getsession") {
    if (session.contains("username")) {
      Success(session("username").toString)
    } else {
      Error("No session")
    }
  }

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
