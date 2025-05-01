package com.borisey.personal_finance.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DataInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Вставляем только если значение не существует
            statement.executeUpdate("INSERT INTO transaction_status (title) " +
                    "SELECT * FROM (SELECT 'Новая') AS tmp " +
                    "WHERE NOT EXISTS (SELECT title FROM transaction_status WHERE title = 'Новая')");

            statement.executeUpdate("INSERT INTO transaction_status (title) " +
                    "SELECT * FROM (SELECT 'Подтвержденная') AS tmp " +
                    "WHERE NOT EXISTS (SELECT title FROM transaction_status WHERE title = 'Подтвержденная')");

            statement.executeUpdate("INSERT INTO transaction_status (title) " +
                    "SELECT * FROM (SELECT 'В обработке') AS tmp " +
                    "WHERE NOT EXISTS (SELECT title FROM transaction_status WHERE title = 'В обработке')");

            statement.executeUpdate("INSERT INTO transaction_status (title) " +
                    "SELECT * FROM (SELECT 'Отменена') AS tmp " +
                    "WHERE NOT EXISTS (SELECT title FROM transaction_status WHERE title = 'Отменена')");

            statement.executeUpdate("INSERT INTO transaction_status (title) " +
                    "SELECT * FROM (SELECT 'Платеж выполнен') AS tmp " +
                    "WHERE NOT EXISTS (SELECT title FROM transaction_status WHERE title = 'Платеж выполнен')");

            statement.executeUpdate("INSERT INTO transaction_status (title) " +
                    "SELECT * FROM (SELECT 'Платеж удален') AS tmp " +
                    "WHERE NOT EXISTS (SELECT title FROM transaction_status WHERE title = 'Платеж удален')");

            statement.executeUpdate("INSERT INTO transaction_status (title) " +
                    "SELECT * FROM (SELECT 'Возврат') AS tmp " +
                    "WHERE NOT EXISTS (SELECT title FROM transaction_status WHERE title = 'Возврат')");
        }
    }
}