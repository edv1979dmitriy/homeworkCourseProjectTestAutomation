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

    private static final String approved = "1111 2222 3333 4444";
    private static final String declined = "5555 6666 7777 8888";

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

    public static DataCard getDataCardApproved() {
        return new DataCard(approved, getMonth(), getYear(), getName(), getCVC());
    }

    public static DataCard getDataCardDeclined() {
        return new DataCard(declined, getMonth(), getYear(), getName(), getCVC());
    }

    public static DataCard getDataCardApprovedFixedMonth(String month) {
        return new DataCard(approved, month, getYear(), getName(), getCVC());
    }

    public static DataCard getDataCardDeclinedFixedMonth(String month) {
        return new DataCard(declined, month, getYear(), getName(), getCVC());
    }

    public static DataCard getDataCardApprovedMonthPast() {
        return new DataCard(approved, getMonthPast(), getYearCurrent(), getName(), getCVC());
    }

    public static DataCard getDataCardDeclinedMonthPast() {
        return new DataCard(declined, getMonthPast(), getYearCurrent(), getName(), getCVC());
    }

    public static DataCard getDataCardApprovedFixedYear(String year) {
        return new DataCard(approved, getMonth(), year, getName(), getCVC());
    }

    public static DataCard getDataCardDeclinedFixedYear(String year) {
        return new DataCard(declined, getMonth(), year, getName(), getCVC());
    }

    public static DataCard getDataCardApprovedYearBiggerPeriod() {
        return new DataCard(approved, getMonth(), getYearBiggerPeriod(), getName(), getCVC());
    }

    public static DataCard getDataCardDeclinedYearBiggerPeriod() {
        return new DataCard(declined, getMonth(), getYearBiggerPeriod(), getName(), getCVC());
    }

    public static DataCard getDataCardApprovedYearPast() {
        return new DataCard(approved, getMonth(), getYearPast(), getName(), getCVC());
    }

    public static DataCard getDataCardDeclinedYearPast() {
        return new DataCard(declined, getMonth(), getYearPast(), getName(), getCVC());
    }

    public static DataCard getDataCardApprovedFixedName(String name) {
        return new DataCard(approved, getMonth(), getYear(), name, getCVC());
    }

    public static DataCard getDataCardDeclinedFixedName(String name) {
        return new DataCard(declined, getMonth(), getYear(), name, getCVC());
    }


    public static DataCard getDataCardApprovedIncorrectedName() {
        return new DataCard(approved, getMonth(), getYear(), getNameIncorrected(), getCVC());
    }

    public static DataCard getDataCardDeclinedIncorrectedName() {
        return new DataCard(declined, getMonth(), getYear(), getNameIncorrected(), getCVC());
    }

    public static DataCard getDataCardApprovedFixedCVC(String cvc) {
        return new DataCard(approved, getMonth(), getYear(), getName(), cvc);
    }

    public static DataCard getDataCardDeclinedFixedCVC(String cvc) {
        return new DataCard(declined, getMonth(), getYear(), getName(), cvc);
    }

    public static DataCard getDataCardApprovedFixedParameter(String month, String year, String name, String cvc) {
        return new DataCard(approved, month, year, name, cvc);
    }

    public static DataCard getDataCardDeclinedFixedParameter(String month, String year, String name, String cvc) {
        return new DataCard(declined, month, year, name, cvc);
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
