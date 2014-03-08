import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.evolutions.Evolutions
import play.api.db.evolutions.Evolution


@RunWith(classOf[JUnitRunner])
class ChallengesSpec extends Specification {

  "ChallengeManager" should {

    "send 404 on a bad request" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        route(FakeRequest(GET, "/boum")) must beNone
      }
    }
    "allow to create new challenges" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        models.Challenge.add("Test challenge", "Some testing motivation", 1)
        models.Challenge.getChallenge(1) must not(beNone)
      }
    }
  }
}
