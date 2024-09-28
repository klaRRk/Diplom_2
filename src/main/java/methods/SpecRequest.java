package methods;

import constants.APIConstants;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class SpecRequest {

    public static RequestSpecification requestSpecification() {
        return given().log().all()
                .contentType(ContentType.JSON)
                .baseUri(APIConstants.BURGERS_URL);
    }

}