package solventum.challenge;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UrlTransformEndToEndTests {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void testEncodeDecodeFlow() {
        // Encode
        String testUrl = "https://somedomain.com";
        String key = given()
                .contentType("application/json")
                .body("{\"url\":\"" + testUrl + "\"}")
                .log().all()
                .when()
                .post("/encode")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("hash");

        // Decode
        given()
                .log().all()
                .when()
                .get("/decode/" + key)
                .then()
                .log().all()
                .statusCode(200)
                .body("url", equalTo(testUrl));
    }

    @Test
    public void testEncodeIsIdempotent() {
        // Encode
        String testUrl = "https://somedomain.com";
        String key1 = given()
                .contentType("application/json")
                .body("{\"url\":\"" + testUrl + "\"}")
                .log().all()
                .when()
                .post("/encode")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("hash");

        String key2 = given()
                .contentType("application/json")
                .body("{\"url\":\"" + testUrl + "\"}")
                .log().all()
                .when()
                .post("/encode")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .path("hash");

        assertEquals(key1, key2);
    }

    @Test
    public void testUrlValidation() {
        // Encode
        String testUrl = "ophqwf98h-q3f";
        String key = given()
                .contentType("application/json")
                .body("{\"url\":\"" + testUrl + "\"}")
                .log().all()
                .when()
                .post("/encode")
                .then()
                .log().all()
                .statusCode(400).toString();
    }

}