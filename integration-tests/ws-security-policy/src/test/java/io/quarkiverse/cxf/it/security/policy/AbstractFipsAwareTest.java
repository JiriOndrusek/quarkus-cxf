package io.quarkiverse.cxf.it.security.policy;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.assertj.core.api.Assertions;

import io.restassured.RestAssured;

public abstract class AbstractFipsAwareTest {

    private Boolean _fips;

    void failFipsInNative() {
        if (this.getClass().isAnnotationPresent(QuarkusIntegrationTest.class) && isFipsEnabled()) {
            //native binary itself has to be FIPS compliant and has tobe created on FIPS machine to make native test FIPS compliant
            // because of that, the native tests with FIPS compliant binaries should fail with the corresponding message
            Assertions.fail("Not supported combination: native mode with FIPS compliant binary.");
        }

    }

    boolean isFipsEnabled() {
        if (_fips == null) {
            _fips = Boolean.valueOf(RestAssured.get("/cxf/security-policy/isfips")
                    .then()
                    .statusCode(200)
                    .extract().body().asString());
        }

        return _fips;
    }
}