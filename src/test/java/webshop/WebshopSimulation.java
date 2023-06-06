package webshop;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class WebshopSimulation extends Simulation {

    FeederBuilder<String> feederProducts = csv("products.csv").queue();
    FeederBuilder<String> feederIds = csv("ids.csv").queue();

    ChainBuilder getProducts = exec(http("Home")
            .get("/"))
            .pause(1)
            .exec(
                    http("Get Products")
                            .get("/listAllProducts.action"))
            .pause(1);

    ChainBuilder createProducts = exec(http("Home")
            .get("/"))
            .pause(1)
            .feed(feederProducts)
            .exec(http("Login Request")
                    .post("/LoginAction.action")
                    .formParam("username", "admin")
                    .formParam("password", "admin")
                    .formParam("method:execute", "login")
                    .check(header("Set-Cookie").saveAs("authCookie"))
                    .check(status().is(200)))
            .exec(addCookie(Cookie("Cookie", "${authCookie}")))
            .exec(
                    http("Create Products")
                            .post("/AddProductAction.action")
                            .formParam("name", "${name}")
                            .formParam("price", "${price}")
                            .formParam("categoryId", "${categoryId}")
                            .formParam("details", "${details}")
                            .formParam("method:execute", "Hinzufügen"))
            .pause(1);

    ChainBuilder deleteProducts = exec(http("Home")
            .get("/"))
            .pause(1)
            .feed(feederIds)
            .exec(http("Login Request")
                    .post("/LoginAction.action")
                    .formParam("username", "admin")
                    .formParam("password", "admin")
                    .formParam("method:execute", "login")
                    .check(header("Set-Cookie").saveAs("authCookie"))
                    .check(status().is(200)))
            .exec(addCookie(Cookie("Cookie", "${authCookie}")))
            .exec(
                    http("Delete Products")
                            .get("/DeleteProductAction.action")
                            .queryParam("id", "${id}"))
            .pause(1);

    FeederBuilder<String> feederCategories = csv("categories.csv").queue();
    FeederBuilder<String> feederCategoryIds = csv("categoryIds.csv").queue();

    ChainBuilder createCategories = exec(http("Home")
            .get("/"))
            .pause(1)
            .feed(feederCategories)
            .exec(http("Login Request")
                    .post("/LoginAction.action")
                    .formParam("username", "admin")
                    .formParam("password", "admin")
                    .formParam("method:execute", "login")
                    .check(header("Set-Cookie").saveAs("authCookie"))
                    .check(status().is(200)))
            .exec(addCookie(Cookie("Cookie", "${authCookie}")))
            .exec(
                    http("Create Categories")
                            .post("/AddCategoryAction.action")
                            .formParam("newCatName", "${newCatName}"))
            .pause(1);

    ChainBuilder deleteCategories = exec(http("Home")
            .get("/"))
            .pause(1)
            .feed(feederCategoryIds)
            .exec(http("Login Request")
                    .post("/LoginAction.action")
                    .formParam("username", "admin")
                    .formParam("password", "admin")
                    .formParam("method:execute", "login")
                    .check(header("Set-Cookie").saveAs("authCookie"))
                    .check(status().is(200)))
            .exec(addCookie(Cookie("Cookie", "${authCookie}")))
            .exec(
                    http("Delete Categories")
                            .post("/DeleteCategoryAction.action")
                            .queryParam("catId", "${catId}"))
            .pause(1);

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8888/EShop-1.0.0")
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0");

    ScenarioBuilder createProductsScenario = scenario("Create Products").exec(createProducts);
    ScenarioBuilder getProductsScenario = scenario("Get Products").exec(getProducts);
    ScenarioBuilder deleteProductsScenario = scenario("Delete Products").exec(deleteProducts);

    ScenarioBuilder createCategoriesScenario = scenario("Create Categories").exec(createCategories);
    ScenarioBuilder deleteCategoriesScenario = scenario("Delete Categories").exec(deleteCategories);

    {
        setUp(
                createCategoriesScenario.injectOpen(rampUsers(50).during(10)),

                createProductsScenario.injectOpen(rampUsers(100).during(10)),
                getProductsScenario.injectOpen(rampUsers(100).during(10)),
                deleteProductsScenario.injectOpen(rampUsers(100).during(10)),

                deleteCategoriesScenario.injectOpen(rampUsers(50).during(10))
                ).protocols(httpProtocol);
    }

}
