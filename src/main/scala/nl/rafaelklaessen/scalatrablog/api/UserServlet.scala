package nl.rafaelklaessen.scalatrablog

import org.scalatra._
import org.json4s._
import org.scalatra.json._
import com.google.firebase
import com.google.firebase._
import com.google.firebase.auth._
import com.google.firebase.database._
import org.mindrot.jbcrypt._

class UserServlet extends ScalatraBlogStack with JacksonJsonSupport with CorsSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal
  
  private val userRegistrationEnabled = true

  before() {
    contentType = formats("json")
  }

  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def validEmail(email: String): Boolean = email match {
    case null                                           => false
    case e if e.trim.isEmpty                            => false
    case e if emailRegex.findFirstMatchIn(e).isDefined  => true
    case _                                              => false
  }

  options("/*"){
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
  }

  post("/login/?") {
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

  post("/register/?") {
    if (!userRegistrationEnabled) halt(404, Error("Page not found"))

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

  post("/logout/?") {
    // Delete username from session
    session -= "username"

    Success("Successfully logged out")
  }

  post("/getsession/?") {
    if (session.contains("username")) {
      Success(session("username").toString)
    } else {
      Error("No session")
    }
  }

  post("/delete/?") {
    if (!session.contains("username")) halt(401, Error("You have to log in before you can delete your account"))

    // Get user
    val user = Users.get(session("username").toString)

    // Remove user from Firebase
    val ref = FirebaseDatabase.getInstance()
    val usersRef = ref.getReference("users")
    val postsRef = ref.getReference("posts")
    val currentUser = usersRef.child(session("username").toString)

    currentUser.removeValue()

    // Remove posts from Firebase
    for (post <- user.posts.keys) {
      postsRef.child(post).removeValue()
    }

    // End session
    session -= "username"

    Success("Successfully deleted account :(")
  }

  post("/update/?") {
    val fieldsJson: String = params.getOrElse("fields", halt(400, Error("Please provide fields")))
    var fields: Map[String, String] = Map()

    try {
      fields = parse(fieldsJson).extract[Map[String, String]]
    } catch {
      case jpe: com.fasterxml.jackson.core.JsonParseException => halt(400, Error("Please provide valid fields"))
    }

    // Make sure the user is logged in
    if (!session.contains("username")) halt(401, Error("You have to log in before you can edit your profile"))

    val filteredFields = fields.filterKeys(_ match {
      case "email" | "name" | "password" => true
      case _ => false
    })

    // Make sure given email is valid
    if (fields.contains("email")) {
      if (!validEmail(fields("email"))) halt(400, Error("Please provide a valid email"))
    }

    // Transform required fields (which is hashing the password)
    val finalFields = filteredFields.transform((k, v) => k match {
      case "password" => BCrypt.hashpw(v, BCrypt.gensalt())
      case k => v
    })

    val usersRef = FirebaseDatabase.getInstance().getReference("users")
    val currentUser = usersRef.child(session("username").toString)

    for (field <- finalFields.keys) {
      currentUser.child(field).setValue(finalFields(field))
    }

    Success("Successfully updated user")
  }

  post("/get/?") {
    val username: String = params.getOrElse("username", halt(400, Error("Please provide a username")))

    if (!Users.userExists(username)) halt(400, Error("User doesn't exist"))

    Users.get(username)
  }

  post("/getall/?") {
    Users.getAll
  }

  post("/getposts/?") {
    val username: String = params.getOrElse("username", halt(400, Error("Please provide a username")))

    if (!Users.userExists(username)) halt(400, Error("User doesn't exist"))

    Posts.getByUsername(username)
  }
}