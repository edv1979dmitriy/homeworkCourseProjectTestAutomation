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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UINegativeCreditGateTest {

    public static String declined = "5555 6666 7777 8888";
    public static String db = System.getProperty("db.url");
    public static String user = System.getProperty("db.user");
    public static String password = System.getProperty("db.pass");
    public static String messageResaltNegative = "Ошибка! Банк отказал в проведении операции.";
    public static String messageIncorrectedFormat = "Неверный формат";
    public static String messageOnlyFigure = "Значение может содержать только цифры";
    public static String messageCharBiggerLimit = "Количество символов превышает допустимое";
    public static String messageIncorrectedPeriodMonth = "Неверно указан срок действия карты";
    public static String messageIncorrectedPeriodYear = "Истёк срок действия карты";
    public static String messageIncorrectedText = "Значение может содержать только латиницу, дефисы и пробелы";
    public static String messageRequiredField = "Поле обязательно для заполнения";

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

    @BeforeEach
    public void openForm() {
        HeadPage.loginOnCreditGate();
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
    @DisplayName("1. Не должен купить c невалидной карты (через CreditGate)")
    @Test
    public void noShouldPayViaCreditGateWithNoValidNumberCard() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5225"));
        FormPage.checkResaltMessage(messageResaltNegative);
        assertAll(
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("2. Должен дать ошибку при вводе номера карты меньше 16-ти цифр (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardLessNormalViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 52"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("3. Должен дать ошибку если ввести буквы в номере карты (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardLettersViaCreditGate() {
        FormPage.fillFormWithoutSend(DataHelper.getDataCardFixedNumberCard("аааа бббб вввв гггг"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageOnlyFigure),
                FormPage::checkNullValueFieldNumberCard
        );
    }

    @Step
    @DisplayName("4. Должен дать ошибку если ввести спецсимволы в номере карты (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardSpecialCharViaCreditGate() {
        FormPage.fillFormWithoutSend(DataHelper.getDataCardFixedNumberCard("№№№№ %%%%% ???? ))))"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageOnlyFigure),
                FormPage::checkNullValueFieldNumberCard
        );
    }

    @Step
    @DisplayName("5. Должен дать ошибку при незаполненном номере карты (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardNotAddViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard(""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("6. Должен дать ошибку при вводе номера карты больше 16-ти цифр (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardBiggerNormalViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5223 2"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageCharBiggerLimit),
                () -> FormPage.checkValueFieldNumberCard("5555 2266 5558 5223"),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("7. Должен дать ошибку при вводе месяца = 00 (через CreditGate)")
    @Test
    public void shouldErrorIfMonth00ViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedMonth(declined, "00"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldMonth(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("8. Должен дать ошибку при вводе месяца = 13 (через CreditGate)")
    @Test
    public void shouldErrorIfMonth13ViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedMonth(declined, "13"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldMonth(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("9. Должен дать ошибку при незаполненном месяце (через CreditGate)")
    @Test
    public void shouldErrorIfMonthNotAddViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedMonth(declined, ""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldMonth(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("10. Должен дать ошибку при вводе месяца из прошлого (через CreditGate)")
    @Test
    public void shouldErrorIfMonthPastViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardMonthPast(declined));
        assertAll(
                () -> FormPage.checkErrorMessageFieldMonth(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("11. Должен дать ошибку при вводе года из прошлого (через CreditGate)")
    @Test
    public void shouldErrorIfYearPastViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardYearPast(declined));
        assertAll(
                () -> FormPage.checkErrorMessageFieldYear(messageIncorrectedPeriodYear),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("12. Должен дать ошибку при вводе года более срока действия (через CreditGate)")
    @Test
    public void shouldErrorIfYearBiggerPeriodViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardYearBiggerPeriod(declined));
        assertAll(
                () -> FormPage.checkErrorMessageFieldYear(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("13. Должен дать ошибку при незаполненном годе (через CreditGate)")
    @Test
    public void shouldErrorIfYearNotAddViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedYear(declined, ""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldYear(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("14. Должен дать ошибку если ввести кириллицу в поле Владелец (через CreditGate)")
    @Test
    public void shouldErrorIfNameIncorrectedLettersViaCreditGate() {
        FormPage.fillFormWithoutSend(DataHelper.getDataCardIncorrectedName(declined));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageIncorrectedText),
                FormPage::checkNullValueFieldName
        );
    }

    @Step
    @DisplayName("15. Должен дать ошибку если ввести цифры в поле Владелец (через CreditGate)")
    @Test
    public void shouldErrorIfNameFigureViaCreditGate() {
        FormPage.fillFormWithoutSend(DataHelper.getDataCardFixedName(declined, "32467"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageIncorrectedText),
                FormPage::checkNullValueFieldName
        );
    }

    @Step
    @DisplayName("16. Должен дать ошибку если ввести спецсимволы в поле Владелец (через CreditGate)")
    @Test
    public void shouldErrorIfNameSpecialCharViaCreditGate() {
        FormPage.fillFormWithoutSend(DataHelper.getDataCardFixedName(declined, "#&%"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageIncorrectedText),
                FormPage::checkNullValueFieldName
        );
    }

    @Step
    @DisplayName("17. Должен дать ошибку если ввести в поле Владелец более 26 символов (через CreditGate)")
    @Test
    public void shouldErrorIfNameBiggerLimitViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedName(declined, "ggggg ggggg aaaaa aaaaa fff"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageCharBiggerLimit),
                () -> FormPage.checkValueFieldName("ggggg ggggg aaaaa aaaaa f")
        );
    }

    @Step
    @DisplayName("18. Должен дать ошибку если не заполнять поле Владелец (через CreditGate)")
    @Test
    public void shouldErrorIfNameNotAddViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedName(declined, ""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageRequiredField),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }

    @Step
    @DisplayName("19. Должен дать ошибку если ввести буквы в поле CVC (через CreditGate)")
    @Test
    public void shouldErrorIfCVCLettersViaCreditGate() {
        FormPage.fillFormWithoutSend(DataHelper.getDataCardFixedCVC(declined, "own"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldCVC(messageOnlyFigure),
                FormPage::checkNullValueFieldCVC
        );
    }

    @Step
    @DisplayName("20. Должен дать ошибку если ввести спецсимволы в поле CVC (через CreditGate)")
    @Test
    public void shouldErrorIfCVCSpecialCharViaCreditGate() {
        FormPage.fillFormWithoutSend(DataHelper.getDataCardFixedCVC(declined, "#%&"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldCVC(messageOnlyFigure),
                FormPage::checkNullValueFieldCVC
        );
    }

    @Step
    @DisplayName("21. Должен дать ошибку если ввести в поле CVC более 3-х цифр (через CreditGate)")
    @Test
    public void shouldErrorIfCVCBiggerLimitViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedCVC(declined, "5432"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldCVC(messageCharBiggerLimit),
                () -> FormPage.checkValueFieldCVC("543")
        );
    }

    @Step
    @DisplayName("22. Должен дать ошибку если не заполнять поле CVC (через CreditGate)")
    @Test
    public void shouldErrorIfCVCNotAddViaCreditGate() {
        FormPage.fillFormAndSend(DataHelper.getDataCardFixedCVC(declined, ""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldCVC(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate(parameterDB)),
                () -> assertNull(SQLHelper.orderCreditGate(parameterDB))
        );
    }
}
