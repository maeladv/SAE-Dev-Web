package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseManager;

public class Examen {
    private int id;
    private String nom;
    private int coefficient;
    private LocalDate date;
    private int id_matiere;
    private boolean persisted = false;

    // Constructeurs
    public Examen() {}

    public Examen(String nom, int coefficient, int id_matiere) {
        this.nom = nom;
        this.coefficient = coefficient;
        this.id_matiere = id_matiere;
    }

    // ==================== Méthodes de recherche ====================

    /**
     * Trouver un examen par ID
     */
    public static Examen trouverParId(int id) throws SQLException {
        String sql = "SELECT id, nom, coefficient, date, id_matiere FROM examen WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creerDepuisResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Trouver tous les examens d'une matière
     */
    public static List<Examen> trouverParMatiere(int idMatiere) throws SQLException {
        List<Examen> liste = new ArrayList<>();
        String sql = "SELECT id, nom, coefficient, date, id_matiere FROM examen WHERE id_matiere = ? ORDER BY date";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMatiere);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                liste.add(creerDepuisResultSet(rs));
            }
        }
        return liste;
    }

    /**
     * Trouver tous les examens
     */
    public static List<Examen> trouverTous() throws SQLException {
        List<Examen> liste = new ArrayList<>();
        String sql = "SELECT id, nom, coefficient, date, id_matiere FROM examen ORDER BY date DESC";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                liste.add(creerDepuisResultSet(rs));
            }
        }
        return liste;
    }

    // ==================== Méthodes de persistence (Active Record) ====================
    /**
     * Sauvegarder la spécialité (INSERT automatique)
     */
    public boolean save() throws SQLException {
        if (persisted) {
            return update();
        } else {
            return insert();
        }
    }

    /**
     * Insérer une nouvelle spécialité en base de données
     */
    private boolean insert() throws SQLException {
        // Ici, le tag est fourni par l'objet et non généré par la BDD
        String sql = "INSERT INTO examen (nom, coefficient, date, id_matiere) VALUES (?, ?, CURRENT_DATE, ?)";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nom);
            stmt.setInt(2, this.coefficient);
            stmt.setInt(3, this.id_matiere);

            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                this.persisted = true;
                return true;
            }
        }
        return false;
    }
     
    //Mettre à jour une spécialité existante
    private boolean update() throws SQLException {
        String sql = "UPDATE examen SET nom = ?, coefficient = ?, date = ?, id_matiere = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nom);
            stmt.setInt(2, this.coefficient);
            stmt.setObject(3, this.date);
            stmt.setInt(4, this.id_matiere);
            stmt.setInt(5, this.id);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer la spécialité
     */
    public boolean supprimer() throws SQLException {
        if (!persisted) {
            return false;
        }

        String sql = "DELETE FROM examen WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                this.persisted = false;
            }
            return success;
        }
    }

    /**
     * Rafraîchir les données de la spécialité depuis la base de données
     */
    public boolean recharger() throws SQLException {
        if (!persisted) {
            return false;
        }

        Examen fresh = trouverParId(this.id);
        if (fresh != null) {
            this.nom = fresh.nom;
            this.coefficient = fresh.coefficient;
            this.date = fresh.date;
            this.id_matiere = fresh.id_matiere;
            return true;
        }
        return false;
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Hydrater un objet depuis un ResultSet
     */
    private static Examen creerDepuisResultSet(ResultSet rs) throws SQLException {
        Examen exam = new Examen();
        exam.id = rs.getInt("id");
        exam.nom = rs.getString("nom");
        exam.coefficient = rs.getInt("coefficient");
        exam.date = rs.getDate("date").toLocalDate();
        exam.id_matiere = rs.getInt("id_matiere");
        exam.persisted = true;
        return exam;
    }

    /**
     * Vérifier si l'objet est persisté en base de données
     */
    public boolean estPersiste() {
        return persisted;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getCoefficient() {
        return coefficient;
    }
    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getId_matiere() {
        return id_matiere;
    }
    public void setId_matiere(int id_matiere) {
        this.id_matiere = id_matiere;
    }
}
