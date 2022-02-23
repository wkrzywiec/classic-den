package io.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class GreetingTest
{
    // NOTE: RestAssured is aware of the application.properties quarkus.http.root-path switch

    @Test
    public void testJaxrs() {
        RestAssured.when().get("/hello").then()
                .statusCode(200)
                .contentType("text/plain")
                .body(equalTo("hello jaxrs"));
    }
}
