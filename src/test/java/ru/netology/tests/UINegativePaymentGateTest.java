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

    public static String approved = "1111 2222 3333 4444";
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
        HeadPage.loginOnPaymentGate();
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
    @DisplayName("1. Не должен купить c невалидной карты (через PaymentGate)")
    @Test
    public void noShouldPayViaPaymentGateWithNoValidNumberCard() {
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5225"));
        FormPage.checkResaltMessage(messageResaltNegative);
        assertAll(
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("2. Должен дать ошибку при вводе номера карты меньше 16-ти цифр (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardLessNormalViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 52"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("3. Должен дать ошибку если ввести буквы в номере карты (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardLettersViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard("аааа бббб вввв гггг"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageOnlyFigure),
                FormPage::checkNullValueFieldNumberCard,
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("4. Должен дать ошибку если ввести спецсимволы в номере карты (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardSpecialCharViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard("№№№№ %%%%% ???? ))))"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageOnlyFigure),
                FormPage::checkNullValueFieldNumberCard,
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("5. Должен дать ошибку при незаполненном номере карты (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardNotAddViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard(""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("6. Должен дать ошибку при вводе номера карты больше 16-ти цифр (через PaymentGate)")
    @Test
    public void shouldErrorIfNumberCardBiggerNormalViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedNumberCard("5555 2266 5558 5223 2"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldNumberCard(messageCharBiggerLimit),
                () -> FormPage.checkValueFieldNumberCard("5555 2266 5558 5223"),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("7. Должен дать ошибку при вводе месяца = 00 (через PaymentGate)")
    @Test
    public void shouldErrorIfMonth00ViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedMonth(approved, "00"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldMonth(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("8. Должен дать ошибку при вводе месяца = 13 (через PaymentGate)")
    @Test
    public void shouldErrorIfMonth13ViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedMonth(approved, "13"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldMonth(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("9. Должен дать ошибку при незаполненном месяце (через PaymentGate)")
    @Test
    public void shouldErrorIfMonthNotAddViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedMonth(approved, ""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldMonth(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("10. Должен дать ошибку при вводе месяца из прошлого (через PaymentGate)")
    @Test
    public void shouldErrorIfMonthPastViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardMonthPast(approved));
        assertAll(
                () -> FormPage.checkErrorMessageFieldMonth(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("11. Должен дать ошибку при вводе года из прошлого (через PaymentGate)")
    @Test
    public void shouldErrorIfYearPastViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardYearPast(approved));
        assertAll(
                () -> FormPage.checkErrorMessageFieldYear(messageIncorrectedPeriodYear),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("12. Должен дать ошибку при вводе года более срока действия (через PaymentGate)")
    @Test
    public void shouldErrorIfYearBiggerPeriodViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardYearBiggerPeriod(approved));
        assertAll(
                () -> FormPage.checkErrorMessageFieldYear(messageIncorrectedPeriodMonth),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("13. Должен дать ошибку при незаполненном годе (через PaymentGate)")
    @Test
    public void shouldErrorIfYearNotAddViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedYear(approved, ""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldYear(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("14. Должен дать ошибку если ввести кириллицу в поле Владелец (через PaymentGate)")
    @Test
    public void shouldErrorIfNameIncorrectedLettersViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardIncorrectedName(approved));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageIncorrectedText),
                FormPage::checkNullValueFieldName,
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("15. Должен дать ошибку если ввести цифры в поле Владелец (через PaymentGate)")
    @Test
    public void shouldErrorIfNameFigureViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedName(approved, "32467"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageIncorrectedText),
                FormPage::checkNullValueFieldName,
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("16. Должен дать ошибку если ввести спецсимволы в поле Владелец (через PaymentGate)")
    @Test
    public void shouldErrorIfNameSpecialCharViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedName(approved, "#&%"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageIncorrectedText),
                FormPage::checkNullValueFieldName,
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("17. Должен дать ошибку если ввести в поле Владелец более 26 символов (через PaymentGate)")
    @Test
    public void shouldErrorIfNameBiggerLimitViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedName(approved, "ggggg ggggg aaaaa aaaaa fff"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageCharBiggerLimit),
                () -> FormPage.checkValueFieldName("ggggg ggggg aaaaa aaaaa f")
        );
    }

    @Step
    @DisplayName("18. Должен дать ошибку если не заполнять поле Владелец (через PaymentGate)")
    @Test
    public void shouldErrorIfNameNotAddViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedName(approved, ""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldName(messageRequiredField),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("19. Должен дать ошибку если ввести буквы в поле CVC (через PaymentGate)")
    @Test
    public void shouldErrorIfCVCLettersViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedCVC(approved, "own"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldCVC(messageOnlyFigure),
                FormPage::checkNullValueFieldCVC,
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("20. Должен дать ошибку если ввести спецсимволы в поле CVC (через PaymentGate)")
    @Test
    public void shouldErrorIfCVCSpecialCharViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedCVC(approved, "#%&"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldCVC(messageOnlyFigure),
                FormPage::checkNullValueFieldCVC,
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }

    @Step
    @DisplayName("21. Должен дать ошибку если ввести в поле CVC более 3-х цифр (через PaymentGate)")
    @Test
    public void shouldErrorIfCVCBiggerLimitViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedCVC(approved, "5432"));
        assertAll(
                () -> FormPage.checkErrorMessageFieldCVC(messageCharBiggerLimit),
                () -> FormPage.checkValueFieldCVC("543")
        );
    }

    @Step
    @DisplayName("21. Должен дать ошибку если не заполнять поле CVC (через PaymentGate)")
    @Test
    public void shouldErrorIfCVCNotAddViaPaymentGate() {
        FormPage.fillForm(DataHelper.getDataCardFixedCVC(approved, ""));
        assertAll(
                () -> FormPage.checkErrorMessageFieldCVC(messageIncorrectedFormat),
                () -> assertNull(SQLHelper.statusPaymentGate(parameterDB)),
                () -> assertNull(SQLHelper.orderPaymentGate(parameterDB))
        );
    }
}
