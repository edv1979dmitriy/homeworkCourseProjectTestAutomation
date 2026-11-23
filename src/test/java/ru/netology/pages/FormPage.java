package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class FormPage {

    private FormPage() {
    }

    private static final SelenideElement fieldNumberCard = $("input[placeholder='0000 0000 0000 0000']");
    private static final SelenideElement fieldYear = $("input[placeholder='22']");
    private static final SelenideElement fieldMonth = $("input[placeholder='08']");
    private static final SelenideElement fieldName = $(byText("Владелец")).closest("span").find("input");
    private static final SelenideElement fieldCVC = $("input[placeholder='999']");

    public static void fillFormAndSend(DataHelper.DataCard dataCard) {
        fieldNumberCard.setValue(dataCard.getNumberCard());
        fieldMonth.setValue(dataCard.getMonth());
        fieldYear.setValue(dataCard.getYear());
        fieldName.setValue(dataCard.getName());
        fieldCVC.setValue(dataCard.getCVC());
        $(withText("Продолжить")).closest("button").click();
    }

    public static void fillFormWithoutSend(DataHelper.DataCard dataCard) {
        fieldNumberCard.setValue(dataCard.getNumberCard());
        fieldMonth.setValue(dataCard.getMonth());
        fieldYear.setValue(dataCard.getYear());
        fieldName.setValue(dataCard.getName());
        fieldCVC.setValue(dataCard.getCVC());
    }

    public static void checkResaltMessage(String message) {
        $(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(30));
    }

    public static void checkErrorMessageFieldNumberCard(String message) {
        fieldNumberCard.closest("span").closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public static void checkNullValueFieldNumberCard() {
        fieldNumberCard.shouldBe(empty);
    }

    public static void checkValueFieldNumberCard(String numberCard) {
        fieldNumberCard.shouldHave(exactValue(numberCard));
    }

    public static void checkErrorMessageFieldMonth(String message) {
        fieldMonth.closest("span").closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public static void checkErrorMessageFieldYear(String message) {
        fieldYear.closest("span").closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public static void checkNullValueFieldName() {
        fieldName.shouldBe(empty);
    }

    public static void checkValueFieldName(String name) {
        fieldName.shouldHave(exactValue(name));
    }

    public static void checkErrorMessageFieldName(String message) {
        $(byText("Владелец")).closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public static void checkNullValueFieldCVC() {
        fieldCVC.shouldBe(empty);
    }

    public static void checkValueFieldCVC(String cvc) {
        fieldCVC.shouldHave(exactValue(cvc));
    }

    public static void checkErrorMessageFieldCVC(String message) {
        fieldCVC.closest("span").closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }
}
