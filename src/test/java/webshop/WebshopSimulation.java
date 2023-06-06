package webshop;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class WebshopSimulation extends Simulation {

    ChainBuilder getProducts = exec(http("Home").get("/"))
            .pause(1)
            .exec(
                    http("Get Products")
                            .get("/listAllProducts.action"))
            .pause(1);

    HttpProtocolBuilder httpProtocol = http.baseUrl("https://computer-database.gatling.io")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0");

    ScenarioBuilder users = scenario("Users").exec(getProducts);

    {
        setUp(
            users.injectOpen(rampUsers(10).during(10))
        ).protocols(httpProtocol);
    }

}
