package io.quarkiverse.cxf.it.security.policy;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

@QuarkusTest
public class CustomEncryptSignPolicyTest {

    @Test
    void helloDefaultCustomValues() {
        RestAssured.given()
                .config(PolicyTestUtils.restAssuredConfig())
                .body("Dolly")
                .post("/cxf/security-policy/helloCustomEncryptSign")
                .then()
                .statusCode(200)
                .body(is("Hello Dolly from CustomEncryptSign!"));
    }

    @Test
    void helloCustomizedValuesCorrectly() throws IOException {

        ValidatableResponse response = RestAssured.given()
                .config(PolicyTestUtils.restAssuredConfig())
                .body("Dolly")
                .post("/cxf/security-policy/helloCustomizedEncryptSign")
                .then();

        if (PolicyTestUtils.isFipsEnabled()) {
            response.statusCode(500)
                    .body(containsString("unsupported key transport encryption algorithm"));
        } else {
            response.statusCode(200)
                    .body(is("Hello Dolly from CustomEncryptSign!"));
        }
    }

    @Test
    void helloCustomizedValuesWrong1() throws IOException {

        //client used default custom algorithm suite, but server is changed (server is same as in the test 'helloDefaultCustomValues')
        RestAssured.given()
                .config(PolicyTestUtils.restAssuredConfig())
                .body("Dolly")
                .post("/cxf/security-policy/helloCustomEncryptSignWrong1")
                .then()
                .statusCode(500)
                .body(containsString("An error was discovered processing the <wsse:Security> header"));
    }

    @Test
    void helloCustomizedValuesWrong02() throws IOException {

        String condition = PolicyTestUtils.isFipsEnabled() ? "unsupported key transport encryption algorithm"
                : "An error was discovered processing the <wsse:Security> header";
        //client customizes custom algorithm suite, but server is using default one (server is same as in the test 'helloCustomizedValuesCorrectly')
        RestAssured.given()
                .config(PolicyTestUtils.restAssuredConfig())
                .body("Dolly")
                .post("/cxf/security-policy/helloCustomEncryptSignWrong02")
                .then()
                .statusCode(500)
                .body(containsString(condition));

    }

}
