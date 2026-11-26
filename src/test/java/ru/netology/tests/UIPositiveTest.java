package ru.netology.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.pages.HeadPage;

import static org.junit.jupiter.api.Assertions.*;


public class UIPositiveTest {

    private static final String messageResultSuccessful = "Операция одобрена Банком.";
    private static HeadPage headPage;

    @BeforeAll
    public static void setUpAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void openService() {
        Configuration.pageLoadTimeout = 100000;
        headPage = Selenide.open("http://localhost:8080", HeadPage.class);
    }

    @AfterEach
    public void cleanDB() {
        SQLHelper.cleanDB();
    }

    @AfterAll
    public static void tearDownAllure() {
        SelenideLogger.removeListener("allure");
    }

    @Step
    @DisplayName("Должен купить через PaymentGate c APPROVED")
    @Test
    public void shouldPayViaPaymentGateWithApproved() {
        var formPage = headPage.loginOnPaymentGate();
        formPage.checkVisiableFormPagePayment();
        formPage.fillFormAndSend(DataHelper.getDataCardApproved());
        formPage.checkResultMessage(messageResultSuccessful);
        assertAll(
                () -> assertEquals("APPROVED", SQLHelper.statusPaymentGate()),
                () -> assertNotNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("Должен купить через PaymentGate c DECLINED")
    @Test
    public void shouldPayViaPaymentGateWithDeclined() {
        var formPage = headPage.loginOnPaymentGate();
        formPage.checkVisiableFormPagePayment();
        formPage.fillFormAndSend(DataHelper.getDataCardDeclined());
        formPage.checkResultMessage(messageResultSuccessful);
        assertAll(
                () -> assertEquals("DECLINED", SQLHelper.statusPaymentGate()),
                () -> assertNotNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("Должен купить через CreditGate c APPROVED")
    @Test
    public void shouldPayViaCreditGateWithApproved() {
        var formPage = headPage.loginOnCreditGate();
        formPage.checkVisiableFormPageCredit();
        formPage.fillFormAndSend(DataHelper.getDataCardApproved());
        formPage.checkResultMessage(messageResultSuccessful);
        assertAll(
                () -> assertEquals("APPROVED", SQLHelper.statusCreditGate()),
                () -> assertNotNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("Должен купить через CreditGate c DECLINED")
    @Test
    public void shouldPayViaCreditGateWithDeclined() {
        var formPage = headPage.loginOnCreditGate();
        formPage.checkVisiableFormPageCredit();
        formPage.fillFormAndSend(DataHelper.getDataCardDeclined());
        formPage.checkResultMessage(messageResultSuccessful);
        assertAll(
                () -> assertEquals("DECLINED", SQLHelper.statusCreditGate()),
                () -> assertNotNull(SQLHelper.orderCreditGate())
        );
    }
}
