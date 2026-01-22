package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseManager;

public class Specialite {
    private int id;
    private String tag;
    private int annee;
    private String nom;
    private boolean persisted = false;
    
    // Constructeurs
    public Specialite() {}

    public Specialite(String tag, int annee, String nom) {
        this.tag = tag;
        this.annee = annee;
        this.nom = nom;
    }


    // ==================== Méthodes de recherche ====================

    /**
     * Trouver une spécialité par ID
     */
    public static Specialite trouverParId(int id) throws SQLException {
        String sql = "SELECT id, tag, annee, nom FROM specialite WHERE id = ?";

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
     * Trouver toutes les spécialités
     */
    public static List<Specialite> trouverToutes() throws SQLException {
        List<Specialite> liste = new ArrayList<>();
        String sql = "SELECT id, tag, annee, nom FROM specialite ORDER BY nom";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                liste.add(creerDepuisResultSet(rs));
            }
        }
        return liste;
    }

    /**
     * Trouver les spécialités où un professeur enseigne
     */
    public static List<Specialite> trouverParProfesseur(int idProf) throws SQLException {
        List<Specialite> liste = new ArrayList<>();
        String sql = "SELECT DISTINCT s.id, s.tag, s.annee, s.nom " +
                     "FROM specialite s " +
                     "JOIN matiere m ON m.id_specialite = s.id " +
                     "WHERE m.id_prof = ? " +
                     "ORDER BY s.nom";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProf);
            ResultSet rs = stmt.executeQuery();
            
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
        String sql = "INSERT INTO specialite (tag, annee, nom) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.tag);
            stmt.setInt(2, this.annee);
            stmt.setString(3, this.nom);

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
        String sql = "UPDATE specialite SET tag = ?, annee = ?, nom = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.tag);
            stmt.setInt(2, this.annee);
            stmt.setString(3, this.nom);
            stmt.setInt(4, this.id);

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

        String sql = "DELETE FROM specialite WHERE tag = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.tag);
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

        Specialite fresh = trouverParId(this.id);
        if (fresh != null) {
            this.tag = fresh.tag;
            this.annee = fresh.annee;
            this.nom = fresh.nom;
            return true;
        }
        return false;
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Hydrater un objet depuis un ResultSet
     */
    private static Specialite creerDepuisResultSet(ResultSet rs) throws SQLException {
        Specialite spec = new Specialite();
        spec.id = rs.getInt("id");
        spec.tag = rs.getString("tag");
        spec.annee = rs.getInt("annee");
        spec.nom = rs.getString("nom");
        spec.persisted = true;
        return spec;
    }

    /**
     * Vérifier si l'objet est persisté en base de données
     */
    public boolean estPersiste() {
        return persisted;
    }

    /**
     * Compter le nombre d'étudiants dans cette spécialité
     */
    public int compterEtudiants() throws SQLException {
        if (!persisted) {
            return 0;
        }
        
        String sql = "SELECT COUNT(*) FROM etudiant WHERE id_specialite = ?";
        
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Compter le nombre de professeurs enseignant dans cette spécialité
     */
    public int compterProfesseurs() throws SQLException {
        if (!persisted) {
            return 0;
        }
        
        String sql = "SELECT COUNT(DISTINCT id_prof) FROM matiere WHERE id_specialite = ? AND id_prof IS NOT NULL";
        
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }


    // Getters et Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getAnnee() {
        return annee;
    }
    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
}