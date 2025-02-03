package util;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.SSLConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import java.io.PrintStream;
import static io.restassured.RestAssured.given;

public class RequestSpecificationFactory {

    private static final Logger LOG = LogManager.getLogger(RequestSpecificationFactory.class);
    private static final PrintStream logStream = IoBuilder.forLogger(LOG).buildPrintStream();

    public static RequestSpecification getInstance() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();

        requestSpecBuilder.addFilter(RequestLoggingFilter.logRequestTo(logStream))
                .addFilter(ResponseLoggingFilter.logResponseTo(logStream));

        requestSpecBuilder.setConfig(RestAssured.config().sslConfig(new SSLConfig()
                .relaxedHTTPSValidation().allowAllHostnames()));

        return given(requestSpecBuilder.build());
    }
}