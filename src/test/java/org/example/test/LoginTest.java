package org.example.test;

import io.restassured.response.Response;
import org.example.api.LoginApi;
import org.example.utils.DataProviderUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class LoginTest {

    LoginApi loginAPI = new LoginApi();

    @Test(dataProvider = "loginData", dataProviderClass = DataProviderUtil.class)
    public void verifyLogin(String email, String password) {

        Response response = loginAPI.login(email, password);

        System.out.println(response.asPrettyString());

        // Status code validation
        Assert.assertEquals(response.statusCode(), 200);

        // JSON Schema validation
        response.then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath("schema/loginSchema.json"));

        // Token validation
        String token = response.jsonPath().getString("token");

        Assert.assertNotNull(token, "Token should not be null");
    }
}