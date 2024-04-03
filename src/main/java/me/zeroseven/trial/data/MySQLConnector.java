package me.zeroseven.trial.data;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.zeroseven.trial.Punishment;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MySQLConnector {

    public final static String ACTIVE_TABLE = "active_p", HISTORY_TABLE = "history_p";
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("punishment-thread").build());
    private final Punishment plugin;
    private Connection connection;

    public MySQLConnector(Punishment plugin) {
        this.plugin = plugin;
    }

    public void init() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minecraft", "root", "root");

            // Create tables if they don't exist
            createTables();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        // Create active punishments table
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + ACTIVE_TABLE + " " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, type VARCHAR(4), name VARCHAR(16), author VARCHAR(16), date BIGINT, expire BIGINT, reason VARCHAR(255))")) {
            statement.execute();
        }

        // Create history table
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + HISTORY_TABLE + " " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, type VARCHAR(4), name VARCHAR(16), author VARCHAR(16), date BIGINT, expire BIGINT, reason VARCHAR(255))")) {
            statement.execute();
        }
    }

    public void stop() {
        executor.shutdown();

        plugin.getLogger().info("Waiting for punishment-thread to end all tasks...");
        try {
            if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                plugin.getLogger().info("All tasks completed for punishment-thread");
            } else {
                plugin.getLogger().info("Timed out for punishment-thread");
            }
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().info("An error occured while completing tasks for punishment-thread");
        }
    }

    public void execute(Runnable runnable) {
        if (Thread.currentThread().getName().equals("punishment-thread")) runnable.run();
        else executor.execute(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Connection connection() throws SQLException {
        return connection();
    }

}
