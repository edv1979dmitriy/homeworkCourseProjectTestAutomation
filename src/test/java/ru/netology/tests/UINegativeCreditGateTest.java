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

public class UINegativeCreditGateTest {

    public static String messageResultNegative = "Ошибка! Банк отказал в проведении операции.";
    public static String messageIncorrectedFormat = "Неверный формат";
    public static String messageOnlyFigure = "Значение может содержать только цифры";
    public static String messageCharBiggerLimit = "Количество символов превышает допустимое";
    public static String messageIncorrectedPeriodMonth = "Неверно указан срок действия карты";
    public static String messageIncorrectedPeriodYear = "Истёк срок действия карты";
    public static String messageIncorrectedText = "Значение может содержать только латиницу, дефисы и пробелы";
    public static String messageRequiredField = "Поле обязательно для заполнения";

    private static HeadPage headPage;
    private static FormPage formPage;

    @BeforeAll
    public static void openService() {
        Configuration.pageLoadTimeout = 100000;
        headPage = Selenide.open("http://localhost:8080", HeadPage.class);
    }

    @BeforeAll
    public static void setUpAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void openForm() {
        formPage = headPage.loginOnCreditGate();
        formPage.checkVisiableFormPageCredit();
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
    @DisplayName("1. Не должен купить c невалидной карты (через CreditGate)")
    @Test
    public void noShouldPayViaCreditGateWithNoValidNumberCard() {
        formPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5225"));
        formPage.checkResultMessage(messageResultNegative);
        assertAll(
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("2. Должен дать ошибку при вводе номера карты меньше 16-ти цифр (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardLessNormalViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 52"));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("3. Должен дать ошибку если ввести буквы в номере карты (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardLettersViaCreditGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardFixedNumberCard("аааа бббб вввв гггг"));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageOnlyFigure),
                () -> formPage.checkNullValueFieldNumberCard()
        );
    }

    @Step
    @DisplayName("4. Должен дать ошибку если ввести спецсимволы в номере карты (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardSpecialCharViaCreditGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardFixedNumberCard("№№№№ %%%%% ???? ))))"));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageOnlyFigure),
                () -> formPage.checkNullValueFieldNumberCard()
        );
    }

    @Step
    @DisplayName("5. Должен дать ошибку при незаполненном номере карты (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardNotAddViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("6. Должен дать ошибку при вводе номера карты больше 16-ти цифр (через CreditGate)")
    @Test
    public void shouldErrorIfNumberCardBiggerNormalViaCreditGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5223 2"));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageCharBiggerLimit),
                () -> formPage.checkValueFieldNumberCard("5555 2266 5558 5223")
        );
    }

    @Step
    @DisplayName("7. Должен дать ошибку при вводе месяца = 00 (через CreditGate)")
    @Test
    public void shouldErrorIfMonth00ViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedFixedMonth("00"));
        assertAll(
                () -> formPage.checkErrorMessageFieldMonth(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("8. Должен дать ошибку при вводе месяца = 13 (через CreditGate)")
    @Test
    public void shouldErrorIfMonth13ViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedFixedMonth("13"));
        assertAll(
                () -> formPage.checkErrorMessageFieldMonth(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("9. Должен дать ошибку при незаполненном месяце (через CreditGate)")
    @Test
    public void shouldErrorIfMonthNotAddViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedFixedMonth(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldMonth(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("10. Должен дать ошибку при вводе месяца из прошлого (через CreditGate)")
    @Test
    public void shouldErrorIfMonthPastViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedMonthPast());
        assertAll(
                () -> formPage.checkErrorMessageFieldMonth(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("11. Должен дать ошибку при вводе года из прошлого (через CreditGate)")
    @Test
    public void shouldErrorIfYearPastViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedYearPast());
        assertAll(
                () -> formPage.checkErrorMessageFieldYear(messageIncorrectedPeriodYear),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("12. Должен дать ошибку при вводе года более срока действия (через CreditGate)")
    @Test
    public void shouldErrorIfYearBiggerPeriodViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedYearBiggerPeriod());
        assertAll(
                () -> formPage.checkErrorMessageFieldYear(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("13. Должен дать ошибку при незаполненном годе (через CreditGate)")
    @Test
    public void shouldErrorIfYearNotAddViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedFixedYear(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldYear(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("14. Должен дать ошибку если ввести кириллицу в поле Владелец (через CreditGate)")
    @Test
    public void shouldErrorIfNameIncorrectedLettersViaCreditGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardDeclinedIncorrectedName());
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageIncorrectedText),
                () -> formPage.checkNullValueFieldName()
        );
    }

    @Step
    @DisplayName("15. Должен дать ошибку если ввести цифры в поле Владелец (через CreditGate)")
    @Test
    public void shouldErrorIfNameFigureViaCreditGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardDeclinedFixedName("32467"));
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageIncorrectedText),
                () -> formPage.checkNullValueFieldName()
        );
    }

    @Step
    @DisplayName("16. Должен дать ошибку если ввести спецсимволы в поле Владелец (через CreditGate)")
    @Test
    public void shouldErrorIfNameSpecialCharViaCreditGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardDeclinedFixedName("#&%"));
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageIncorrectedText),
                () -> formPage.checkNullValueFieldName()
        );
    }

    @Step
    @DisplayName("17. Должен дать ошибку если ввести в поле Владелец более 26 символов (через CreditGate)")
    @Test
    public void shouldErrorIfNameBiggerLimitViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedFixedName("ggggg ggggg aaaaa aaaaa fff"));
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageCharBiggerLimit),
                () -> formPage.checkValueFieldName("ggggg ggggg aaaaa aaaaa f")
        );
    }

    @Step
    @DisplayName("18. Должен дать ошибку если не заполнять поле Владелец (через CreditGate)")
    @Test
    public void shouldErrorIfNameNotAddViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedName(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageRequiredField),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }

    @Step
    @DisplayName("19. Должен дать ошибку если ввести буквы в поле CVC (через CreditGate)")
    @Test
    public void shouldErrorIfCVCLettersViaCreditGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardDeclinedFixedCVC("own"));
        assertAll(
                () -> formPage.checkErrorMessageFieldCVC(messageOnlyFigure),
                () -> formPage.checkNullValueFieldCVC()
        );
    }

    @Step
    @DisplayName("20. Должен дать ошибку если ввести спецсимволы в поле CVC (через CreditGate)")
    @Test
    public void shouldErrorIfCVCSpecialCharViaCreditGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardDeclinedFixedCVC("#%&"));
        assertAll(
                () -> formPage.checkErrorMessageFieldCVC(messageOnlyFigure),
                () -> formPage.checkNullValueFieldCVC()
        );
    }

    @Step
    @DisplayName("21. Должен дать ошибку если ввести в поле CVC более 3-х цифр (через CreditGate)")
    @Test
    public void shouldErrorIfCVCBiggerLimitViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardDeclinedFixedCVC("5432"));
        assertAll(
                () -> formPage.checkErrorMessageFieldCVC(messageCharBiggerLimit),
                () -> formPage.checkValueFieldCVC("543")
        );
    }

    @Step
    @DisplayName("22. Должен дать ошибку если не заполнять поле CVC (через CreditGate)")
    @Test
    public void shouldErrorIfCVCNotAddViaCreditGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedCVC(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldCVC(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusCreditGate()),
                () -> assertNull(SQLHelper.orderCreditGate())
        );
    }
}