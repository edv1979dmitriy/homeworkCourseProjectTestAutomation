package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DataHelper {

    private DataHelper() {
    }

    private static final LocalDate dateCurrent = LocalDate.now(ZoneId.of("Europe/Moscow"));

    private static LocalDate getDateFuture() {
        Faker faker = new Faker();
        return LocalDate.ofInstant(faker.date().future(1800, TimeUnit.DAYS).toInstant(), ZoneId.of("Europe/Moscow"));
    }

    private static String getYearCurrent() {
        return DateTimeFormatter.ofPattern("yy").format(dateCurrent);
    }

    private static String getYearPast() {
        return DateTimeFormatter.ofPattern("yy").format(dateCurrent.minusYears(1));
    }

    private static String getYearBiggerPeriod() {
        return DateTimeFormatter.ofPattern("yy").format(dateCurrent.plusYears(6));
    }

    private static String getMonth() {
        var date = getDateFuture();
        return DateTimeFormatter.ofPattern("MM").format(date);
    }

    private static String getMonthPast() {
        return DateTimeFormatter.ofPattern("MM").format(dateCurrent.minusMonths(1));
    }

    private static String getYear() {
        var date = getDateFuture();
        return DateTimeFormatter.ofPattern("yy").format(date);
    }

    private static String getName() {
        Faker faker = new Faker();
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    private static String getNameIncorrected() {
        Faker faker = new Faker(new Locale("ru"));
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    private static String getCVC() {
        Faker faker = new Faker();
        return String.valueOf(faker.random().nextInt(100, 999));
    }

    public static DataCard getDataCardFixedNumberCard(String numberCard) {
        return new DataCard(numberCard, getMonth(), getYear(), getName(), getCVC());
    }

    public static DataCard getDataCardFixedMonth(String numberCard, String month) {
        return new DataCard(numberCard, month, getYear(), getName(), getCVC());
    }

    public static DataCard getDataCardMonthPast(String numberCard) {
        return new DataCard(numberCard, getMonthPast(), getYearCurrent(), getName(), getCVC());
    }

    public static DataCard getDataCardFixedYear(String numberCard, String year) {
        return new DataCard(numberCard, getMonth(), year, getName(), getCVC());
    }

    public static DataCard getDataCardYearBiggerPeriod(String numberCard) {
        return new DataCard(numberCard, getMonth(), getYearBiggerPeriod(), getName(), getCVC());
    }

    public static DataCard getDataCardYearPast(String numberCard) {
        return new DataCard(numberCard, getMonth(), getYearPast(), getName(), getCVC());
    }

    public static DataCard getDataCardFixedName(String numberCard, String name) {
        return new DataCard(numberCard, getMonth(), getYear(), name, getCVC());
    }

    public static DataCard getDataCardIncorrectedName(String numberCard) {
        return new DataCard(numberCard, getMonth(), getYear(), getNameIncorrected(), getCVC());
    }

    public static DataCard getDataCardFixedCVC(String numberCard, String cvc) {
        return new DataCard(numberCard, getMonth(), getYear(), getName(), cvc);
    }

    public static DataCard getDataCardFixedParameter(String numberCard, String month, String year, String name, String cvc) {
        return new DataCard(numberCard, month, year, name, cvc);
    }

    @Value
    public static class DataCard {
        String numberCard;
        String month;
        String year;
        String name;
        String CVC;
    }
}
