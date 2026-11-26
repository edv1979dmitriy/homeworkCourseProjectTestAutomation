package ru.netology.pages;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class HeadPage {

    public HeadPage() {
    }

    public FormPage loginOnPaymentGate() {
        $(byText("Купить")).closest("button").click();
        return new FormPage();
    }

    public FormPage loginOnCreditGate() {
        $(byText("Купить в кредит")).closest("button").click();
        return new FormPage();
    }
}