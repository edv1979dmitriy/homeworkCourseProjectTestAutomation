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

    public FormPage() {
    }

    private static final SelenideElement fieldNumberCard = $("input[placeholder='0000 0000 0000 0000']");
    private static final SelenideElement fieldYear = $("input[placeholder='22']");
    private static final SelenideElement fieldMonth = $("input[placeholder='08']");
    private static final SelenideElement fieldName = $(byText("Владелец")).closest("span").find("input");
    private static final SelenideElement fieldCVC = $("input[placeholder='999']");


    public void checkVisiableFormPagePayment() {
        $("h3[class='heading heading_size_m heading_theme_alfa-on-white']").shouldHave(Condition.text("Оплата по карте"));
    }

    public void checkVisiableFormPageCredit() {
        $("h3[class='heading heading_size_m heading_theme_alfa-on-white']").shouldHave(Condition.text("Кредит по данным карты"));
    }

    public void fillFormAndSend(DataHelper.DataCard dataCard) {
        fillFormWithoutSend(dataCard);
        $(withText("Продолжить")).closest("button").click();
    }

    public void fillFormWithoutSend(DataHelper.DataCard dataCard) {
        fieldNumberCard.setValue(dataCard.getNumberCard());
        fieldMonth.setValue(dataCard.getMonth());
        fieldYear.setValue(dataCard.getYear());
        fieldName.setValue(dataCard.getName());
        fieldCVC.setValue(dataCard.getCVC());
    }

    public void checkResultMessage(String message) {
        $(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(40));
    }

    public void checkErrorMessageFieldNumberCard(String message) {
        fieldNumberCard.closest("span").closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public void checkNullValueFieldNumberCard() {
        fieldNumberCard.shouldBe(empty);
    }

    public void checkValueFieldNumberCard(String numberCard) {
        fieldNumberCard.shouldHave(exactValue(numberCard));
    }

    public void checkErrorMessageFieldMonth(String message) {
        fieldMonth.closest("span").closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public void checkErrorMessageFieldYear(String message) {
        fieldYear.closest("span").closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public void checkNullValueFieldName() {
        fieldName.shouldBe(empty);
    }

    public void checkValueFieldName(String name) {
        fieldName.shouldHave(exactValue(name));
    }

    public void checkErrorMessageFieldName(String message) {
        $(byText("Владелец")).closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    public void checkNullValueFieldCVC() {
        fieldCVC.shouldBe(empty);
    }

    public void checkValueFieldCVC(String cvc) {
        fieldCVC.shouldHave(exactValue(cvc));
    }

    public void checkErrorMessageFieldCVC(String message) {
        fieldCVC.closest("span").closest("span").find(byText(message)).shouldBe(Condition.visible, Duration.ofSeconds(10));
    }
}