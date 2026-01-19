package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {
    private static final Dotenv dotenv;
    
    static {
        String catalinaBase = System.getProperty("catalina.base");
        
        if (catalinaBase != null) {
            String envPath = catalinaBase + "/gradibou";
            System.out.println("Recherche .env dans: " + envPath);
            dotenv = Dotenv.configure()
                .directory(envPath)
                .ignoreIfMissing()
                .load();
        } else {
            throw new RuntimeException("Impossible de trouver catalina.base");
        }
    }
    
    private static final String DB_HOST = dotenv.get("DB_HOST", "localhost");
    private static final String DB_PORT = dotenv.get("DB_PORT", "5432");
    private static final String DB_NAME = dotenv.get("DB_NAME", "gradibou");
    private static final String DB_USER = dotenv.get("DB_USER", "gradibou_user");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD", "password");
    
    private static final String DB_URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    private static Connection connection;

    // Initialiser la connexion
    public static void init() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        System.out.println("Base de données PostgreSQL initialisée");
    }

    // Obtenir la connexion
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                init();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Créer les tables
    // public static void createTables() throws SQLException {
    //     String sql = "CREATE TABLE IF NOT EXISTS users (" +
    //             "id SERIAL PRIMARY KEY," +
    //             "name VARCHAR(255) NOT NULL," +
    //             "email VARCHAR(255) NOT NULL UNIQUE," +
    //             "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
    //             ")";
        
    //     try (Statement stmt = connection.createStatement()) {
    //         stmt.execute(sql);
    //         System.out.println("Tables créées");
    //     }
    // }

    // Fermer la connexion
    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connexion fermée");
        }
    }
}