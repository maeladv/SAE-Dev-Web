package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
    public static Connection obtenirConnexion() throws SQLException {
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
    public static void creerTables() throws SQLException {
    String[] sqlStatements = {
        "CREATE TABLE IF NOT EXISTS utilisateur (" +
            "id SERIAL PRIMARY KEY," +
            "nom VARCHAR(50) NOT NULL," +
            "prenom VARCHAR(50) NOT NULL," +
            "mail VARCHAR(100) NOT NULL UNIQUE," +
            "date_naissance DATE," +
            "mot_de_passe VARCHAR(255) NOT NULL," +
            "role VARCHAR(50) NOT NULL" +
            ")",
        "CREATE TABLE IF NOT EXISTS specialite (" +
            "id SERIAL PRIMARY KEY," +
            "nom VARCHAR(100) NOT NULL," +
            "abbreviation VARCHAR(100) NOT NULL" +
            ")",
        "CREATE TABLE IF NOT EXISTS etudiant (" +
            "id_utilisateur INT PRIMARY KEY," +
            "ine VARCHAR(50) NOT NULL," +
            "id_specialite INT," +
            "FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id)," +
            "FOREIGN KEY (id_specialite) REFERENCES specialite(id)" +
            ")",
        "CREATE TABLE IF NOT EXISTS matiere (" +
            "id SERIAL PRIMARY KEY," +
            "nom VARCHAR(100) NOT NULL," +
            "semestre INT NOT NULL," +
            "coefficient INT NOT NULL," +
            "id_specialite INT NOT NULL," +
            "id_prof INT NOT NULL," +
            "FOREIGN KEY (id_specialite) REFERENCES specialite(id)," +
            "FOREIGN KEY (id_prof) REFERENCES utilisateur(id)" +
            ")",
        "CREATE TABLE IF NOT EXISTS note (" +
            "id SERIAL PRIMARY KEY," +
            "id_etudiant INT NOT NULL," +
            "id_matiere INT NOT NULL," +
            "note INT NOT NULL," +
            "date TIMESTAMP DEFAULT NOW()," +
            "FOREIGN KEY (id_etudiant) REFERENCES etudiant(id_utilisateur)," +
            "FOREIGN KEY (id_matiere) REFERENCES matiere(id)" +
            ")",
        "CREATE TABLE IF NOT EXISTS evaluation (" +
            "id SERIAL PRIMARY KEY," +
            "note INT NOT NULL," +
            "commentaires TEXT," +
            "date_expiration DATE," +
            "id_matiere INT NOT NULL," +
            "FOREIGN KEY (id_matiere) REFERENCES matiere(id)" +
            ")",
        "CREATE TABLE IF NOT EXISTS lien (" +
            "id SERIAL PRIMARY KEY," +
            "token_hash VARCHAR(255) NOT NULL," +
            "date_utilisation DATE," +
            "date_expiration DATE," +
            "id_utilisateur INT NOT NULL," +
            "FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id)" +
            ")",
    };
    
    try (Statement stmt = connection.createStatement()) {
        for (String sql : sqlStatements) {
            stmt.execute(sql);
            System.out.println("Table créée: " + sql.substring(0, 50) + "...");
        }
        System.out.println("Toutes les tables ont été créées");
    }
}

    // Fermer la connexion
    public static void fermer() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connexion fermée");
        }
    }
}