package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.api.Requests;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class APITest {

    public static String approved = "1111 2222 3333 4444";
    public static String declined = "5555 6666 7777 8888";
    public static String db = System.getProperty("db.url");
    public static String user = System.getProperty("db.user");
    public static String password = System.getProperty("db.pass");

    SQLHelper.ParameterDB parameterDB = new SQLHelper.ParameterDB(db, user, password);

    @BeforeAll
    public static void setUpAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    public static void tearDownAllure() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    public void cleanDB() {
        SQLHelper.cleanDB(parameterDB);
    }

    @Step
    @DisplayName("Должен купить через PaymentGate c APPROVED")
    @Test
    public void shouldPayViaPaymentGateWithApproved() {
        var response = Requests.requestPaymentGate(DataHelper.getDataCardFixedNumberCard(approved));
        String actual = Requests.getValueFieldResponse(response, "status");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertEquals("APPROVED", actual),
                () -> assertEquals("APPROVED", SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNotNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Должен купить через PaymentGate c DECLINED")
    @Test
    public void shouldPayViaPaymentGateWithDeclined() {
        var response = Requests.requestPaymentGate(DataHelper.getDataCardFixedNumberCard(declined));
        String actual = Requests.getValueFieldResponse(response, "status");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertEquals("DECLINED", actual),
                () -> assertEquals("DECLINED", SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNotNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Должен купить через CreditGate c APPROVED")
    @Test
    public void shouldPayViaCreditGateWithApproved() {
        var response = Requests.requestCreditGate(DataHelper.getDataCardFixedNumberCard(approved));
        String actual = Requests.getValueFieldResponse(response, "status");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertEquals("APPROVED", actual),
                () -> assertEquals("APPROVED", SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNotNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Должен купить через CreditGate c DECLINED")
    @Test
    public void shouldPayViaCreditGateWithDeclined() {
        var response = Requests.requestCreditGate(DataHelper.getDataCardFixedNumberCard(declined));
        String actual = Requests.getValueFieldResponse(response, "status");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertEquals("DECLINED", actual),
                () -> assertEquals("DECLINED", SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNotNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Не должен купить c невалидной карты (через PaymentGate)")
    @Test
    public void noShouldPayViaPaymentGateWithNoValidNumberCard() {
        var response = Requests.requestPaymentGate(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5225"));
        String actual = Requests.getValueFieldResponse(response, "message");
        assertAll(
                () -> assertEquals(500, response.getStatusCode()),
                () -> assertEquals("400 Bad Request", actual),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Не должен купить c невалидной карты (через CreditGate)")
    @Test
    public void noShouldPayViaCreditGateWithNoValidNumberCard() {
        var response = Requests.requestCreditGate(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5225"));
        String actual = Requests.getValueFieldResponse(response, "message");
        assertAll(
                () -> assertEquals(500, response.getStatusCode()),
                () -> assertEquals("400 Bad Request", actual),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Не должен купить c валидной карты без других данных (через PaymentGate)")
    @Test
    public void noShouldPayViaPaymentGateWithNoValidParameters() {
        var response = Requests.requestPaymentGate(DataHelper.getDataCardFixedParameter(approved, "", "", "", ""));
        String actual = Requests.getValueFieldResponse(response, "message");
        assertAll(
                () -> assertEquals(500, response.getStatusCode()),
                () -> assertEquals("400 Bad Request", actual),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Не должен купить c валидной карты без других данных (через CreditGate)")
    @Test
    public void noShouldPayViaCreditGateWithNoValidParameters() {
        var response = Requests.requestCreditGate(DataHelper.getDataCardFixedParameter(declined, "", "", "", ""));
        String actual = Requests.getValueFieldResponse(response, "message");
        assertAll(
                () -> assertEquals(500, response.getStatusCode()),
                () -> assertEquals("400 Bad Request", actual),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }
}
