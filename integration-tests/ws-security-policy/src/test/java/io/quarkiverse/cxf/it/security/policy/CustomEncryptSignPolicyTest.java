package io.quarkiverse.cxf.it.security.policy;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
public class CustomEncryptSignPolicyTest {
    @Test
    void helloEncryptSign() {
        RestAssured.given()
                .config(PolicyTestUtils.restAssuredConfig())
                .body("Dolly")
                .post("/cxf/security-policy/helloCustomEncryptSign")
                .then()
                .statusCode(200)
                .body(is("Hello Dolly from CustomEncryptSign!"));
    }
}
