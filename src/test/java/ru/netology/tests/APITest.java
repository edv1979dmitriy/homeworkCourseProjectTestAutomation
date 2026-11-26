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
        SQLHelper.cleanDB();
    }

    @Step
    @DisplayName("Должен купить через PaymentGate c APPROVED")
    @Test
    public void shouldPayViaPaymentGateWithApproved() {
        var response = Requests.requestPaymentGate(DataHelper.getDataCardApproved());
        String actual = Requests.getValueFieldResponse(response, "status");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertEquals("APPROVED", actual),
                () -> assertEquals("APPROVED", SQLHelper.statusPaymentGate()),
                () -> assertNotNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("Должен купить через PaymentGate c DECLINED")
    @Test
    public void shouldPayViaPaymentGateWithDeclined() {
        var response = Requests.requestPaymentGate(DataHelper.getDataCardDeclined());
        String actual = Requests.getValueFieldResponse(response, "status");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertEquals("DECLINED", actual),
                () -> assertEquals("DECLINED", SQLHelper.statusPaymentGate()),
                () -> assertNotNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("Должен купить через CreditGate c APPROVED")
    @Test
    public void shouldPayViaCreditGateWithApproved() {
        var response = Requests.requestCreditGate(DataHelper.getDataCardApproved());
        String actual = Requests.getValueFieldResponse(response, "status");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertEquals("APPROVED", actual),
                () -> assertEquals("APPROVED", SQLHelper.statusCreditGate()),
                () -> assertNotNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("Должен купить через CreditGate c DECLINED")
    @Test
    public void shouldPayViaCreditGateWithDeclined() {
        var response = Requests.requestCreditGate(DataHelper.getDataCardDeclined());
        String actual = Requests.getValueFieldResponse(response, "status");
        assertAll(
                () -> assertEquals(200, response.getStatusCode()),
                () -> assertEquals("DECLINED", actual),
                () -> assertEquals("DECLINED", SQLHelper.statusCreditGate()),
                () -> assertNotNull(SQLHelper.orderCreditGate())
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
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
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
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("Не должен купить c валидной карты без других данных (через PaymentGate)")
    @Test
    public void noShouldPayViaPaymentGateWithNoValidParameters() {
        var response = Requests.requestPaymentGate(DataHelper.getDataCardApprovedFixedParameter("", "", "", ""));
        String actual = Requests.getValueFieldResponse(response, "message");
        assertAll(
                () -> assertEquals(500, response.getStatusCode()),
                () -> assertEquals("400 Bad Request", actual),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("Не должен купить c валидной карты без других данных (через CreditGate)")
    @Test
    public void noShouldPayViaCreditGateWithNoValidParameters() {
        var response = Requests.requestCreditGate(DataHelper.getDataCardDeclinedFixedParameter("", "", "", ""));
        String actual = Requests.getValueFieldResponse(response, "message");
        assertAll(
                () -> assertEquals(500, response.getStatusCode()),
                () -> assertEquals("400 Bad Request", actual),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }
}
