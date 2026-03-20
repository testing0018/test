package org.example.api;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Base.BaseTest;
import java.util.HashMap;

public class LoginApi extends BaseTest {

    public Response login(String email, String password) {

        HashMap<String,Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
       // File image = new File("C:\\images\\profile.png");
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .baseUri(baseurl)
                .contentType("application/json")
                .accept("application/json")
                //.multiPart("profileImage", image)
                .header(securityHeaderKey,securityHeaderValue)
                .body(body)
                .post("/api/userLogin");
        return response;
    }
}