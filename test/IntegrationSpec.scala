import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "work from within a browser" in new WithBrowser {
      browser.goTo("http://localhost:" + port)
      browser.pageSource must contain("Welcome to Open Innovation.")
    }

    "allow users to login" in new WithBrowser {
      browser.goTo("http://localhost:" + port + "/signup")

      browser.$("#email").text("sample@sample.com")
      browser.$("#password").text("secret111")
      browser.$("#passwordConfirm").text("secret111")
      browser.$("#signupButton").click()
      browser.pageSource must contain("Welcome: sample@sample.com")
      
      browser.goTo("http://localhost:" + port + "/logout")

      browser.goTo("http://localhost:" + port + "/login")
      browser.$("#email").text("sample@sample.com")
      browser.$("#password").text("secret112")
      browser.$("#loginbutton").click()
      browser.pageSource must contain("Invalid email or password")

      
      browser.$("#email").text("sample@sample.com")
      browser.$("#password").text("secret111")
      browser.$("#loginbutton").click()
      browser.pageSource must contain("Welcome to Open Innovation.")
    }
  }
}
