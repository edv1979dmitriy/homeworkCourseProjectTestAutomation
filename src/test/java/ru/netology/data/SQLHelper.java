package ru.netology.data;

import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {

    private SQLHelper() {
    }

    private static final QueryRunner runner = new QueryRunner();

    private static Connection connect(ParameterDB parameterDB) throws SQLException {
        return DriverManager.getConnection(parameterDB.getDb(), parameterDB.getUser(), parameterDB.getPassword());
    }

    @SneakyThrows
    public static String statusCreditGate(ParameterDB parameterDB) {
        var codeSQL = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        try (var conn = connect(parameterDB)) {
            return runner.query(conn, codeSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static String statusPaymentGate(ParameterDB parameterDB) {
        var codeSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (var conn = connect(parameterDB)) {
            return runner.query(conn, codeSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static String orderPaymentGate(ParameterDB parameterDB) {
        var codeSQL = "SELECT payment_id FROM order_entity ORDER BY created DESC LIMIT 1";
        try (var conn = connect(parameterDB)) {
            return runner.query(conn, codeSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static String orderCreditGate(ParameterDB parameterDB) {
        var codeSQL = "SELECT credit_id FROM order_entity ORDER BY created DESC LIMIT 1";
        try (var conn = connect(parameterDB)) {
            return runner.query(conn, codeSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static void cleanDB(ParameterDB parameterDB) {
        try (var conn = connect(parameterDB)) {
            runner.update(conn, "DELETE FROM order_entity");
            runner.update(conn, "DELETE FROM credit_request_entity");
            runner.update(conn, "DELETE FROM payment_entity");
        }
    }

    @Value
    public static class ParameterDB {
        String db;
        String user;
        String password;
    }
}
