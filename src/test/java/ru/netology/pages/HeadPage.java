package ru.netology.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class HeadPage {

    private HeadPage() {
    }

    public static void loginOnPaymentGate() {
        $(byText("Купить в кредит")).closest("button").click();
        $(byText("Купить")).closest("button").click();
        $("h3[class='heading heading_size_m heading_theme_alfa-on-white']").shouldHave(Condition.text("Оплата по карте"));
    }

    public static void loginOnCreditGate() {
        $(byText("Купить")).closest("button").click();
        $(byText("Купить в кредит")).closest("button").click();
        $("h3[class='heading heading_size_m heading_theme_alfa-on-white']").shouldHave(Condition.text("Кредит по данным карты"));
    }
}
