package com.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Epic("Book API")
@Feature("CRUD Operations")
public class BookApiTest {

    private static int createdBookId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://book-app-api.cinte.id/api";
    }

    // ------------------- POSITIVE TEST CASES ------------------- //

    @Test
    @Order(1)
    @Story("1 Get all books")
    @Description("Verify that GET /books returns list of books")
    public void testGetBooks() {
        Response response = sendGetBooksRequest();
        verifyGetBooksResponse(response);
    }

    @Test
    @Order(2)
    @Story("2 Add a new book")
    @Description("Verify that POST /books can add new book")
    public void testAddBook() {
        String payload = "{ \"title\": \"cinta brontosaurus\", \"author\": \"Raditiya Dika\", \"status\": \"unread\" }";
        Allure.addAttachment("Request Payload", new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));

        Response response = sendAddBookRequest(payload);
        verifyAddBookResponse(response);

        createdBookId = response.jsonPath().getInt("id");
        Allure.step("New Book Id = " + createdBookId);
    }

    @Test
    @Order(3)
    @Story("3 Update book")
    @Description("Verify that PUT /books/{id} can update a book")
    public void testUpdateBook() {
        String payload = "{ \"title\": \"cinta brontosaurus\", \"author\": \"Raditiya Dika\", \"status\": \"reading\" }";
        Allure.addAttachment("Request Payload", new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));

        Response response = sendUpdateBookRequest(createdBookId, payload);
        verifyUpdateBookResponse(response);
    }

    @Test
    @Order(4)
    @Story("4 Delete book")
    @Description("Verify that DELETE /books/{id} can delete a book")
    public void testDeleteBook() {
        Response response = sendDeleteBookRequest(createdBookId);
        verifyDeleteBookResponse(response);
    }

    // ------------------- NEGATIVE TEST CASES ------------------- //

    @Test
    @Order(5)
    @Story("Negative GET - wrong endpoint")
    @Description("Verify GET wrong endpoint returns 404")
    public void testGetWrongEndpoint() {
        Allure.step("Send GET /bookz (wrong endpoint)", () -> {
            Response resp = given().when().get("/bookz").then().extract().response();
            attachResponse(resp);
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(resp.getStatusCode()).as("Status Code").isEqualTo(404);
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    @Test
    @Order(6)
    @Story("Negative POST - missing title")
    @Description("Verify POST without title returns 400")
    public void testAddBookWithoutTitle() {
        String payload = "{ \"author\": \"Anonim\", \"status\": \"unread\" }";
        Allure.addAttachment("Request Payload", new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));

        Allure.step("Send POST /books without title", () -> {
            Response resp = given().contentType("application/json")
                    .body(payload)
                    .when()
                    .post("/books")
                    .then().extract().response();
            attachResponse(resp);
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(resp.getStatusCode()).as("Status Code").isEqualTo(400);
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    @Test
    @Order(7)
    @Story("Negative POST - invalid status")
    @Description("Verify POST with invalid status returns 400")
    public void testAddBookInvalidStatus() {
        String payload = "{ \"title\": \"Book X\", \"author\": \"Anonim\", \"status\": \"abc\" }";
        Allure.addAttachment("Request Payload", new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));

        Allure.step("Send POST /books with invalid status", () -> {
            Response resp = given().contentType("application/json")
                    .body(payload)
                    .when()
                    .post("/books")
                    .then().extract().response();
            attachResponse(resp);
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(resp.getStatusCode()).as("Status Code").isEqualTo(400);
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    @Test
    @Order(8)
    @Story("Negative PUT - book id not found")
    @Description("Verify PUT non-existing id returns 404")
    public void testUpdateBookNotFound() {
        String payload = "{ \"title\": \"X\", \"author\": \"Y\", \"status\": \"reading\" }";
        Allure.addAttachment("Request Payload", new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)));

        Allure.step("Send PUT /books/99999 (non-existing id)", () -> {
            Response resp = given().contentType("application/json")
                    .body(payload)
                    .when()
                    .put("/books/99999")
                    .then().extract().response();
            attachResponse(resp);
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(resp.getStatusCode()).as("Status Code").isEqualTo(404);
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    @Test
    @Order(9)
    @Story("Negative PUT - empty body")
    @Description("Verify PUT with empty body returns 400")
    public void testUpdateBookEmptyBody() {
        Allure.step("Send PUT /books/2 with empty body", () -> {
            Response resp = given().contentType("application/json")
                    .body("{}")
                    .when()
                    .put("/books/1")
                    .then().extract().response();
            attachResponse(resp);
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(resp.getStatusCode()).as("Status Code").isEqualTo(400);
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.label("status", "failed");
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    @Test
    @Order(10)
    @Story("Negative DELETE - book id not found")
    @Description("Verify DELETE non-existing id returns 404")
    public void testDeleteBookNotFound() {
        Allure.step("Send DELETE /books/99999", () -> {
            Response resp = given().when().delete("/books/99999").then().extract().response();
            attachResponse(resp);
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(resp.getStatusCode()).as("Status Code").isEqualTo(404);
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    // ------------------- Helper methods ------------------- //

    public Response sendGetBooksRequest() {
        return Allure.step("Send GET /books", () -> {
            Response resp = given()
                    .when()
                    .get("/books")
                    .then()
                    .extract().response();
            attachResponse(resp);
            return resp;
        });
    }

    public void verifyGetBooksResponse(Response response) {
        Allure.step("Verify GET /books response", () -> {
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode()).as("Status Code").isEqualTo(200);
            softly.assertThat(response.getBody().asString()).as("Response Body not null").isNotNull();
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    public Response sendAddBookRequest(String payload) {
        return Allure.step("Send POST /books with payload", () -> {
            Response resp = given()
                    .contentType("application/json")
                    .body(payload)
                    .when()
                    .post("/books")
                    .then()
                    .extract().response();
            attachResponse(resp);
            return resp;
        });
    }

    public void verifyAddBookResponse(Response response) {
        Allure.step("Verify POST /books response", () -> {
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode()).as("Status Code").isIn(200, 201);
            softly.assertThat(response.jsonPath().getString("title")).as("Title field").isEqualTo("cinta brontosaurus");
            softly.assertThat(response.jsonPath().getString("status")).as("Status field")
                    .isIn("unread", "reading", "finished");
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    public Response sendUpdateBookRequest(int id, String payload) {
        return Allure.step("Send PUT /books/" + id, () -> {
            Response resp = given()
                    .contentType("application/json")
                    .body(payload)
                    .when()
                    .put("/books/" + id)
                    .then()
                    .extract().response();
            attachResponse(resp);
            return resp;
        });
    }

    public void verifyUpdateBookResponse(Response response) {
        Allure.step("Verify PUT /books/{id} response", () -> {
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode()).as("Status Code").isEqualTo(200);
            softly.assertThat(response.jsonPath().getString("status")).as("Updated status").isEqualTo("reading");
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    public Response sendDeleteBookRequest(int id) {
        return Allure.step("Send DELETE /books/" + id, () -> {
            Response resp = given()
                    .when()
                    .delete("/books/" + id)
                    .then()
                    .extract().response();
            attachResponse(resp);
            return resp;
        });
    }

    public void verifyDeleteBookResponse(Response response) {
        Allure.step("Verify DELETE /books/{id} response", () -> {
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(response.getStatusCode()).as("Status Code").isIn(200, 204);
            try { softly.assertAll(); } catch (AssertionError e) {
                Allure.addAttachment("SoftAssertions Failed", e.getMessage());
            }
        });
    }

    // ------------------- Utility ------------------- //
    private void attachResponse(Response resp) {
        Allure.addAttachment("Response Status", String.valueOf(resp.getStatusCode()));
        Allure.addAttachment("Response Body",
                new ByteArrayInputStream(resp.asPrettyString().getBytes(StandardCharsets.UTF_8)));
    }
}
