

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        route(FakeRequest(GET, "/boum")) must beNone
      }
    }

    "render the index page" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val home = route(FakeRequest(GET, "/")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain("Welcome to Open Innovation")
      }
    }

    "allow to create new users" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val result = route(FakeRequest(GET, "/signup")).get
        status(result) must equalTo(OK)

        models.User.add("pp@pp.com", "Open#NoV4T10")

        models.User.getByEmail("pp@pp.com") must not(beNone)
        models.User.getUserById(1) must not(beNone)
      }
    }
  }
}
