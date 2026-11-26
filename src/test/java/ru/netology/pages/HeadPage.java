package ru.netology.pages;

import com.codeborne.selenide.Condition;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class HeadPage {

    public HeadPage() {
    }

    public FormPage loginOnPaymentGate() {
        $(byText("Купить в кредит")).closest("button").click();
        $(byText("Купить")).closest("button").click();
        return new FormPage();
    }

    public FormPage loginOnCreditGate() {
        $(byText("Купить")).closest("button").click();
        $(byText("Купить в кредит")).closest("button").click();
        return new FormPage();
    }
}