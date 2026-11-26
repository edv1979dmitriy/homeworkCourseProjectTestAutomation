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

public class UINegativePaymentGateTest {

    public static String messageResultNegative = "Ошибка! Банк отказал в проведении операции.";
    public static String messageIncorrectedFormat = "Неверный формат";
    public static String messageOnlyFigure = "Значение может содержать только цифры";
    public static String messageCharBiggerLimit = "Количество символов превышает допустимое";
    public static String messageIncorrectedPeriodMonth = "Неверно указан срок действия карты";
    public static String messageIncorrectedPeriodYear = "Истёк срок действия карты";
    public static String messageIncorrectedText = "Значение может содержать только латиницу, дефисы и пробелы";
    public static String messageRequiredField = "Поле обязательно для заполнения";

    private static FormPage formPage;

    @BeforeAll
    public static void setUpAllure() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void openForm() {
        Configuration.pageLoadTimeout = 100000;
        var headPage = Selenide.open("http://localhost:8080", HeadPage.class);
        formPage = headPage.loginOnPaymentGate();
        formPage.checkVisiableFormPagePayment();
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
    @DisplayName("1. Не должен купить c невалидной карты (через PaymentGate)")
    @Test
    public void noShouldPayViaPaymentGateWithNoValidNumberCard() {
        formPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5225"));
        formPage.checkResultMessage(messageResultNegative);
        assertAll(
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("2. Должен дать ошибку при вводе номера карты меньше 16-ти цифр (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardLessNormalViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 52"));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("3. Должен дать ошибку если ввести буквы в номере карты (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardLettersViaPaymentGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardFixedNumberCard("аааа бббб вввв гггг"));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageOnlyFigure),
                () -> formPage.checkNullValueFieldNumberCard()
        );
    }

    @Step
    @DisplayName("4. Должен дать ошибку если ввести спецсимволы в номере карты (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardSpecialCharViaPaymentGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardFixedNumberCard("№№№№ %%%%% ???? ))))"));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageOnlyFigure),
                () -> formPage.checkNullValueFieldNumberCard()
        );
    }

    @Step
    @DisplayName("5. Должен дать ошибку при незаполненном номере карты (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardNotAddViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardFixedNumberCard(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("6. Должен дать ошибку при вводе номера карты больше 16-ти цифр (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardBiggerNormalViaPaymentGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5223 2"));
        assertAll(
                () -> formPage.checkErrorMessageFieldNumberCard(messageCharBiggerLimit),
                () -> formPage.checkValueFieldNumberCard("5555 2266 5558 5223")
        );
    }

    @Step
    @DisplayName("7. Должен дать ошибку при вводе месяца = 00 (через PaymentGate)")
    @Test
    public void shouldErrorIfMonth00ViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedMonth("00"));
        assertAll(
                () -> formPage.checkErrorMessageFieldMonth(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("8. Должен дать ошибку при вводе месяца = 13 (через PaymentGate)")
    @Test
    public void shouldErrorIfMonth13ViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedMonth("13"));
        assertAll(
                () -> formPage.checkErrorMessageFieldMonth(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("9. Должен дать ошибку при незаполненном месяце (через PaymentGate)")
    @Test
    public void shouldErrorIfMonthNotAddViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedMonth(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldMonth(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("10. Должен дать ошибку при вводе месяца из прошлого (через PaymentGate)")
    @Test
    public void shouldErrorIfMonthPastViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedMonthPast());
        assertAll(
                () -> formPage.checkErrorMessageFieldMonth(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("11. Должен дать ошибку при вводе года из прошлого (через PaymentGate)")
    @Test
    public void shouldErrorIfYearPastViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedYearPast());
        assertAll(
                () -> formPage.checkErrorMessageFieldYear(messageIncorrectedPeriodYear),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("12. Должен дать ошибку при вводе года более срока действия (через PaymentGate)")
    @Test
    public void shouldErrorIfYearBiggerPeriodViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedYearBiggerPeriod());
        assertAll(
                () -> formPage.checkErrorMessageFieldYear(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("13. Должен дать ошибку при незаполненном годе (через PaymentGate)")
    @Test
    public void shouldErrorIfYearNotAddViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedYear(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldYear(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("14. Должен дать ошибку если ввести кириллицу в поле Владелец (через PaymentGate)")
    @Test
    public void shouldErrorIfNameIncorrectedLettersViaPaymentGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardApprovedIncorrectedName());
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageIncorrectedText),
                () -> formPage.checkNullValueFieldName()
        );
    }

    @Step
    @DisplayName("15. Должен дать ошибку если ввести цифры в поле Владелец (через PaymentGate)")
    @Test
    public void shouldErrorIfNameFigureViaPaymentGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardApprovedFixedName("32467"));
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageIncorrectedText),
                () -> formPage.checkNullValueFieldName()
        );
    }

    @Step
    @DisplayName("16. Должен дать ошибку если ввести спецсимволы в поле Владелец (через PaymentGate)")
    @Test
    public void shouldErrorIfNameSpecialCharViaPaymentGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardApprovedFixedName("#&%"));
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageIncorrectedText),
                () -> formPage.checkNullValueFieldName()
        );
    }

    @Step
    @DisplayName("17. Должен дать ошибку если ввести в поле Владелец более 26 символов (через PaymentGate)")
    @Test
    public void shouldErrorIfNameBiggerLimitViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedName("ggggg ggggg aaaaa aaaaa fff"));
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageCharBiggerLimit),
                () -> formPage.checkValueFieldName("ggggg ggggg aaaaa aaaaa f")
        );
    }

    @Step
    @DisplayName("18. Должен дать ошибку если не заполнять поле Владелец (через PaymentGate)")
    @Test
    public void shouldErrorIfNameNotAddViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedName(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldName(messageRequiredField),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }

    @Step
    @DisplayName("19. Должен дать ошибку если ввести буквы в поле CVC (через PaymentGate)")
    @Test
    public void shouldErrorIfCVCLettersViaPaymentGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardApprovedFixedCVC("own"));
        assertAll(
                () -> formPage.checkErrorMessageFieldCVC(messageOnlyFigure),
                () -> formPage.checkNullValueFieldCVC()
        );
    }

    @Step
    @DisplayName("20. Должен дать ошибку если ввести спецсимволы в поле CVC (через PaymentGate)")
    @Test
    public void shouldErrorIfCVCSpecialCharViaPaymentGate() {
        formPage.fillFormWithoutSend(DataHelper.getDataCardApprovedFixedCVC("#%&"));
        assertAll(
                () -> formPage.checkErrorMessageFieldCVC(messageOnlyFigure),
                () -> formPage.checkNullValueFieldCVC()
        );
    }

    @Step
    @DisplayName("21. Должен дать ошибку если ввести в поле CVC более 3-х цифр (через PaymentGate)")
    @Test
    public void shouldErrorIfCVCBiggerLimitViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedCVC("5432"));
        assertAll(
                () -> formPage.checkErrorMessageFieldCVC(messageCharBiggerLimit),
                () -> formPage.checkValueFieldCVC("543")
        );
    }

    @Step
    @DisplayName("22. Должен дать ошибку если не заполнять поле CVC (через PaymentGate)")
    @Test
    public void shouldErrorIfCVCNotAddViaPaymentGate() {
        formPage.fillFormAndSend(DataHelper.getDataCardApprovedFixedCVC(""));
        assertAll(
                () -> formPage.checkErrorMessageFieldCVC(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate()),
                () -> assertNull(SQLHelper.orderPaymentGate())
        );
    }
}