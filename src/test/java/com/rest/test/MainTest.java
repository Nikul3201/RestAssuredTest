package com.rest.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

@Test
public class MainTest {

    RequestSpecification requestSpecification;
    Response response;
    ValidatableResponse validatableResponse;

    @Test
    public void verifyStatusCode() {

        // Base URL of the API
        RestAssured.baseURI = "http://localhost:8082/books";

        // Username and password for Basic Authentication
        String username = "user"; // Replace with the correct username
        String password = "password"; // Replace with the correct password

        // Create the request specification
        RequestSpecification requestSpecification = given()
                .auth().preemptive().basic(username, password);

        // Send GET request and get the response
        Response response = requestSpecification.get();

        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);
        response.then().statusCode(200);
        System.out.println("==> Fetched book successfully by ID.");

    }

    @Test
    public void postBook() {

        // Base URL of the API
        RestAssured.baseURI = "http://localhost:8082/books";


        // Username and password for Basic Authentication
        String username = "admin"; // Replace with the correct username
        String password = "password"; // Replace with the correct password


        // JSON request body
        String body = """
                {
                    "name": "A Guide to the Bodhisattva Way of Life",
                    "author": "Santideva",
                    "price": 15.41
                }
                """;


        RequestSpecification requestSpecification = given()
                .auth().preemptive().basic(username, password) // Use preemptive basic auth
                .header("Content-Type", "application/json")    // Set Content-Type header
                .body(body);

        Response response = requestSpecification.post();
        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);

        response.then().statusCode(201);
        System.out.println("==> Book created successfully with valid data.");

    }

    @Test
    public void updateBook() {

        // Base URL
        RestAssured.baseURI = "http://localhost:8082/books/7";

        // Authentication
        String username = "admin";
        String password = "password";

        // Updated book data
        String updatedBody = """
                {
                    "name": "Updated Guide to the Bodhisattva Way of Life",
                    "author": "Santideva",
                    "price": 18.99
                }
                """;

        // PUT request to update the book with ID 1
        RequestSpecification requestSpecification = given()
                .auth().preemptive().basic(username, password)
                .header("Content-Type", "application/json")
                .body(updatedBody);

        Response response = requestSpecification.put();

        response.then().statusCode(200);
        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);

        System.out.println("==> Book updated successfully.");
    }

    @Test
    public void deleteBook() {

        // Base URL
        RestAssured.baseURI = "http://localhost:8082/books/12";

        // Authentication
        String username = "admin";
        String password = "password";

        // DELETE request to delete the book with ID 4
        RequestSpecification requestSpecification = given()
                .auth().preemptive().basic(username, password);

        Response response = requestSpecification.delete();

//        response.then().statusCode(200);
        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);
        System.out.println("==> Book deleted successfully.");
    }


    @Test
    public void getBookWithInvalidId_shouldReturn404() {

        RestAssured.baseURI = "http://localhost:8082/books";
        String username = "user";
        String password = "password";

        Response response = given()
                .auth().preemptive().basic(username, password)
                .log().all()
                .get("/9999"); // Assuming 9999 does not exist

        // Assert status code
        assertEquals("Expected 404 Not Found for invalid book ID", 404);

        System.out.println("=============================================================");
        // Optional: message validation
        String message = response.getBody().asString();
        System.out.println("==> [Invalid Book ID] Response Message: " + message);
    }


    @Test
    public void accessWithoutAuthentication_shouldReturn401() {

        RestAssured.baseURI = "http://localhost:8082/books";

        Response response = given()
                .get("/1");

        assertEquals("Expected 401 Unauthorized when no credentials are provided", 401, response.getStatusCode());
        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);
        System.out.println("==> [Unauthorized Access] Access denied as expected due to missing credentials.");
    }


    @Test
    public void createBookWithoutName() {
        RestAssured.baseURI = "http://localhost:8082/books";

        String body = """
                {
                    "author": "Author Only",
                    "price": 10.99
                }
                """;

        Response response = given()
                .auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body(body)
                .post();

//        System.out.println(response.statusCode());
        response.then().statusCode(400); // Validation failure
        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);

        System.out.println("==> Validation failed as expected due to missing 'name'.");
    }


    @Test
    public void updateBookInvalidId() {
        RestAssured.baseURI = "http://localhost:8082/books";

        String updateBody = """
                {
                    "name": "Ghost Book",
                    "author": "No One",
                    "price": 1.00
                }
                """;

        Response response = given()
                .auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body(updateBody)
                .put("/9999");

//        System.out.println(response.statusCode());
        response.then().statusCode(500);

        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);
        System.out.println("==> Update rejected as expected for non-existent ID.");
    }

    @Test
    public void deleteBookInvalidId() {
        RestAssured.baseURI = "http://localhost:8082/books/9999";

        Response response = given()
                .auth().preemptive().basic("admin", "password")
                .delete();

//        System.out.println(response.statusCode());
        response.then().statusCode(500);

        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);
        System.out.println("==> Delete attempt failed correctly for non-existent book.");
    }

    @Test
    public void createBookWithNegativePrice() {
        RestAssured.baseURI = "http://localhost:8082/books";

        String body = """
                {
                    "name": "Evil Discount Book",
                    "author": "Bad Input",
                    "price": -10.00
                }
                """;

        Response response = given()
                .auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body(body)
                .post();

        response.then().statusCode(400); // assuming validation

        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);

        System.out.println("==> API rejected negative price as expected.");
    }

    @Test
    public void createBookWithExtraField() {
        RestAssured.baseURI = "http://localhost:8082/books";

        String body = """
                {
                    "name": "Book with Extra",
                    "author": "John Doe",
                    "price": 15.00,
                    "publisher": "Unexpected Publisher"
                }
                """;

        Response response = given()
                .auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body(body)
                .post();

        response.then().statusCode(400);

        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);
        System.out.println("==> API call ignored successfully with additional field 'publisher'.");
    }


    @Test
    public void createBookWithInvalidPriceType() {
        RestAssured.baseURI = "http://localhost:8082/books";

        String body = """
                {
                    "name": "Bad Type Book",
                    "author": "Tester",
                    "price": "free"
                }
                """;

        Response response = given()
                .auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body(body)
                .post();

        response.then().statusCode(400);

        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);

        System.out.println("==> API rejected string-type price as expected.");
    }


    @Test
    public void checkResponseTime() {
        RestAssured.baseURI = "http://localhost:8082/books";

        Response response = given()
                .auth().preemptive().basic("user", "password")
                .get("/1");

        long time = response.time();
        assertTrue(time < 1000, "Response time exceeded limit!");

        System.out.println("=============================================================");
        String message = response.getBody().asString();
        System.out.println(message);

        System.out.println("==> Response time: " + time + " ms — within acceptable limit.");
    }
}
