package ru.netology.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.pages.FormPage;
import ru.netology.pages.HeadPage;

import static org.junit.jupiter.api.Assertions.*;


public class UIPositiveTest {

    public static String approved = "1111 2222 3333 4444";
    public static String declined = "5555 6666 7777 8888";
    public static String db = System.getProperty("db.url");
    public static String user = System.getProperty("db.user");
    public static String password = System.getProperty("db.pass");
    public static String messageResaltSuccessful = "Операция одобрена Банком.";

    SQLHelper.ParameterDB parameterDB = new SQLHelper.ParameterDB(db, user, password);

    @BeforeAll
    public static void openService() {
        Configuration.pageLoadTimeout = 100000;
        Selenide.open("http://localhost:8080");
    }

    @BeforeAll
    public static void setUpAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    public void cleanDB() {
        SQLHelper.cleanDB(parameterDB);
    }

    @AfterAll
    public static void tearDownAllure() {
        SelenideLogger.removeListener("allure");
    }

    @Step
    @DisplayName("Должен купить через PaymentGate c APPROVED")
    @Test
    public void shouldPayViaPaymentGateWithApproved() {
        HeadPage.loginOnPaymentGate();
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard(approved));
        FormPage.checkResaltMessage(messageResaltSuccessful);
        assertAll(
                () -> assertEquals("APPROVED", SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNotNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Должен купить через PaymentGate c DECLINED")
    @Test
    public void shouldPayViaPaymentGateWithDeclined() {
        HeadPage.loginOnPaymentGate();
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard(declined));
        FormPage.checkResaltMessage(messageResaltSuccessful);
        assertAll(
                () -> assertEquals("DECLINED", SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNotNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Должен купить через CreditGate c APPROVED")
    @Test
    public void shouldPayViaCreditGateWithApproved() {
        HeadPage.loginOnCreditGate();
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard(approved));
        FormPage.checkResaltMessage(messageResaltSuccessful);
        assertAll(
                () -> assertEquals("APPROVED", SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNotNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("Должен купить через CreditGate c DECLINED")
    @Test
    public void shouldPayViaCreditGateWithDeclined() {
        HeadPage.loginOnCreditGate();
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard(declined));
        FormPage.checkResaltMessage(messageResaltSuccessful);
        assertAll(
                () -> assertEquals("DECLINED", SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNotNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }
}
