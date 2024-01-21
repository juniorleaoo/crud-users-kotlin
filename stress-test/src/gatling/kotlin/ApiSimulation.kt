// required for Gatling core structure DSL

// required for Gatling HTTP DSL

// can be omitted if you don't use jdbcFeeder

// used for specifying durations with a unit, eg Duration.ofMinutes(5)
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.*
import java.time.Duration

class ApiSimulation : Simulation() {
    val httpProtocol = http
        .baseUrl("http://localhost:8080/v1")
        .acceptHeader("application/json")

    val createAndGetUsers = scenario("Create and Get Users")
        .feed(tsv("create-users.tsv").circular())
        .exec(
            http("Create")
                .post("/users")
                .body(StringBody("#{payload}"))
                .header("Content-Type", "application/json")
                .check(status().`in`(201))
                .check(status().saveAs("httpStatus"))
                .checkIf { session -> session.getString("httpStatus") == "201" }
                .then(header("Location").saveAs("location"))

        ).pause(Duration.ofMillis(1), Duration.ofMillis(30))
        .doIf { session -> session.getString("httpStatus") == "201" }.then(
            exec(
                http("Get")
                    .get("#{location}")
                    .header("Content-Type", "application/json")
                    .check(status().`in`(200))
            )
        )

    init {
        this.setUp(
            createAndGetUsers.injectOpen(
                constantUsersPerSec(2.0).during(Duration.ofSeconds(10)),
                constantUsersPerSec(5.0).during(Duration.ofSeconds(15)).randomized(),

                rampUsersPerSec(6.0).to(100.0).during(Duration.ofMinutes(3))
            )
        ).protocols(httpProtocol)

    }
}