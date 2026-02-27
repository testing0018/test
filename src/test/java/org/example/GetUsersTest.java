package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetUsersTest {

    private final String baseUrl = "https://devhrms.hivelance.com";
    private final String securityHeaderKey   = "IOOQIESZJKMFYZKKIQYFGPXPJDR";
    private final String securityHeaderValue = "QZ8XoYF0LWLgLO6lAuWIlWgXJUo2Abmd5GYiQ13fAx0lDHmHgWmHuD4Xo6C4d7QzIdcTu6iXW";

    // -----------------------------
    // COMMON LOGIN METHOD
    // -----------------------------
    private String getToken() {

        Response loginResponse =
                RestAssured.given()
                        .relaxedHTTPSValidation()
                        .baseUri(baseUrl)
                        .contentType("application/json")
                        .accept("application/json")
                        .header(securityHeaderKey, securityHeaderValue)
                        .body("""
                              {
                                "email": "HL100057",
                                "password": "Deep@1234"
                              }
                              """)
                        .when()
                        .post("/api/userLogin");

        System.out.println("Login response:");
        System.out.println(loginResponse.asPrettyString());

        assertEquals(200, loginResponse.statusCode());

        String token = loginResponse.jsonPath().getString("token");
        assertNotNull(token, "Token should not be null");
        return token;
    }

    // ---------------------------------------------------
    // GET – dashboard
    // ---------------------------------------------------
    @Test
    void getDashboard() {

        String token = getToken();

        Response response =
                RestAssured.given()
                        .relaxedHTTPSValidation()
                        .baseUri(baseUrl)
                        .accept("application/json")
                        .header("Authorization", "Bearer " + token)
                        .header(securityHeaderKey, securityHeaderValue)
                        .when()
                        .get("/api/dashboard");

        System.out.println("Dashboard response:");
        System.out.println(response.asPrettyString());

        assertEquals(200, response.statusCode());
        assertEquals(1, response.jsonPath().getInt("status"));

        // few important keys
        assertNotNull(response.jsonPath().get("lastLoggedIn"));
        assertNotNull(response.jsonPath().get("login"));
        assertNotNull(response.jsonPath().get("logout"));
        assertNotNull(response.jsonPath().get("getWorkingHours"));
    }



    // ---------------------------------------------------
    // GET – calender
    // ---------------------------------------------------
    @Test
    void getCalender() {

        String token = getToken();

        Response response =
                RestAssured.given()
                        .relaxedHTTPSValidation()
                        .baseUri(baseUrl)
                        .accept("application/json")
                        .header("Authorization", "Bearer " + token)
                        .header(securityHeaderKey, securityHeaderValue)

                        // start & end of month
                        .queryParam("start", "2025-05-01")
                        .queryParam("end", "2025-06-01")

                        .when()
                        .post("/api/calender");

        System.out.println("Calender response:");
        System.out.println(response.asPrettyString());

        assertEquals(200, response.statusCode());
        assertEquals(1, response.jsonPath().getInt("status"));


        // ✅ VERY IMPORTANT → calendar API returns 'result', not 'msg'
        var resultList = response.jsonPath().getList("result");

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
    }

    // ---------------------------------------------------
    // GET – profile
    // ---------------------------------------------------
    @Test
    void getProfile() {

        String token = getToken();

        Response response =
                RestAssured.given()
                        .relaxedHTTPSValidation()
                        .baseUri(baseUrl)
                        .accept("application/json")
                        .header("Authorization", "Bearer " + token)
                        .header(securityHeaderKey, securityHeaderValue)
                        .when()
                        .get("/api/profile");

        System.out.println("Profile response:");
        System.out.println(response.asPrettyString());

        assertEquals(200, response.statusCode());
        assertEquals(1, response.jsonPath().getInt("status"));

        assertNotNull(response.jsonPath().get("userInfo.id"));
        assertNotNull(response.jsonPath().get("userInfo.userId"));
        assertNotNull(response.jsonPath().get("userInfo.username"));
    }


    // ---------------------------------------------------
    // POST – updatePassword
    // ---------------------------------------------------
    @Test
    void updatePassword() {

        String token = getToken();

        Response response =
                RestAssured.given()
                        .relaxedHTTPSValidation()
                        .baseUri(baseUrl)
                        .contentType("application/json")
                        .accept("application/json")
                        .header("Authorization", "Bearer " + token)
                        .header(securityHeaderKey, securityHeaderValue)
                        .body("""
                          {
                            "current_pwd":"Aishu@1625",
                            "password":"Hive@123",
                            "confirm_password":"Hive@123"
                          }
                          """)
                        .when()
                        .post("/api/updatePassword");

        System.out.println("Update password response:");
        System.out.println(response.asPrettyString());

        assertEquals(200, response.statusCode());

        Integer status = response.jsonPath().getInt("status");
        assertNotNull(status);

        if (status == 1) {

            assertEquals(
                    "Password changed successfully",
                    response.jsonPath().getString("msg")
            );

        } else {

            // ✅ API returns msg, not error
            assertNotNull(response.jsonPath().getString("msg"));
        }
    }


    // ---------------------------------------------------
    // GET – logout
    // ---------------------------------------------------
    @Test
    void logout() {

        String token = getToken();

        Response response =
                RestAssured.given()
                        .relaxedHTTPSValidation()
                        .baseUri(baseUrl)
                        .accept("application/json")
                        .header("Authorization", "Bearer " + token)
                        .header(securityHeaderKey, securityHeaderValue)
                        .when()
                        .get("/api/logout");

        System.out.println("Logout response:");
        System.out.println(response.asPrettyString());

        assertEquals(200, response.statusCode());
        assertEquals(1, response.jsonPath().getInt("status"));
        assertEquals("Logged out", response.jsonPath().getString("msg"));
    }


}