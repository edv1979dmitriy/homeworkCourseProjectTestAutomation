package ru.netology.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import ru.netology.data.DataHelper;

import static io.restassured.RestAssured.given;

public class Requests {

    private Requests() {
    }

    public static Response requestPaymentGate(DataHelper.DataCard dataCard) {
        JSONObject json = new JSONObject();
        json.put("number", dataCard.getNumberCard());
        json.put("year", dataCard.getYear());
        json.put("month", dataCard.getMonth());
        json.put("holder", dataCard.getName());
        json.put("cvc", dataCard.getCVC());
        return given()
                .baseUri("http://localhost:8080/api")
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .log()
                .all()
                .post("/v1/pay")
                .then()
                .log()
                .all()
                .extract().response();
    }

    public static String getValueFieldResponse(Response response, String key) {
        return response.jsonPath().getString(key);
    }

    public static Response requestCreditGate(DataHelper.DataCard dataCard) {
        JSONObject json = new JSONObject();
        json.put("number", dataCard.getNumberCard());
        json.put("year", dataCard.getYear());
        json.put("month", dataCard.getMonth());
        json.put("holder", dataCard.getName());
        json.put("cvc", dataCard.getCVC());
        return given()
                .baseUri("http://localhost:8080/api")
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .log()
                .all()
                .post("/v1/credit")
                .then()
                .log()
                .all()
                .extract().response();
    }
}
