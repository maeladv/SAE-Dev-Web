package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseManager;

public class Matiere {
    private int id;
    private String nom;
    private int semestre;
    private int specialiteId;
    private int profId;
    private boolean persisted = false;

    // Constructeurs
    public Matiere() {}

    public Matiere(String nom, int semestre, int specialiteId, int profId) {
        this.nom = nom;
        this.semestre = semestre;
        this.specialiteId = specialiteId;
        this.profId = profId;
    }

    public Matiere(String nom, int semestre, int specialiteId) {
        this.nom = nom;
        this.semestre = semestre;
        this.specialiteId = specialiteId;
    }

    // ==================== Méthodes de recherche ====================

    /**
     * Trouver une spécialité par ID
     */
    public static Matiere trouverParId(int id) throws SQLException {
        String sql = "SELECT id, nom, semestre, id_specialite, id_prof FROM matiere WHERE id = ?";

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
     * Trouver tous les professeurs
     */
    public static List<Matiere> trouverToutes() throws SQLException {
        List<Matiere> liste = new ArrayList<>();
        String sql = "SELECT id, nom, semestre, id_specialite, id_prof FROM matiere ORDER BY nom";

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
        String sql = "INSERT INTO matiere (nom, semestre, id_specialite, id_prof) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nom);
            stmt.setInt(2, this.semestre);
            stmt.setInt(3, this.specialiteId);
            stmt.setInt(4, this.profId);

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
        String sql = "UPDATE matiere SET nom = ?, semestre = ?, id_specialite = ?, id_prof = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nom);
            stmt.setInt(2, this.semestre);
            stmt.setInt(3, this.specialiteId);
            stmt.setInt(4, this.profId);
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

        String sql = "DELETE FROM matiere WHERE id = ?";

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

        Matiere fresh = trouverParId(this.id);
        if (fresh != null) {
            this.id = fresh.id;
            this.nom = fresh.nom;
            this.semestre = fresh.semestre;
            this.specialiteId = fresh.specialiteId;
            this.profId = fresh.profId;
            return true;
        }
        return false;
    }
    

    // ==================== Méthodes utilitaires ====================

    /**
     * Hydrater un objet depuis un ResultSet
     */
    private static Matiere creerDepuisResultSet(ResultSet rs) throws SQLException {
        Matiere matiere = new Matiere();
        matiere.id = rs.getInt("id");
        matiere.nom = rs.getString("nom");
        matiere.semestre = rs.getInt("semestre");
        matiere.specialiteId = rs.getInt("id_specialite");
        matiere.profId = rs.getInt("id_prof");
        matiere.persisted = true;
        return matiere;
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
    public int getSemestre() {
        return semestre;
    }
    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }
    public int getSpecialiteId() {
        return specialiteId;
    }
    public void setSpecialiteId(int specialiteId) {
        this.specialiteId = specialiteId;
    }
    public int getProfId() {
        return profId;
    }
    public void setProfId(int profId) {
        this.profId = profId;
    }
}