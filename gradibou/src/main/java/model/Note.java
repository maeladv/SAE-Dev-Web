package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseManager;

public class Note {
    private int id_etudiant;
    private int id_examen;
    private int valeur;
    private LocalDate date;
    private boolean persisted = false;

    // Constructeurs
    public Note() {}

    public Note(int valeur, int id_examen, int id_etudiant) {
        this.valeur = valeur;
        this.id_examen = id_examen;
        this.id_etudiant = id_etudiant;
    }

    // ==================== Méthodes de recherche ====================

    /**
     * Trouver une note par ID
     */
    public static Note trouverParIdEtudiantExamen(int id_etudiant, int id_examen) throws SQLException {
        String sql = "SELECT id_etudiant, id_examen, note, date FROM note WHERE id_etudiant = ? AND id_examen = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_etudiant);
            stmt.setInt(2, id_examen);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creerDepuisResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Trouver toutes les notes
     */
    public static List<Note> trouverToutes() throws SQLException {
        List<Note> liste = new ArrayList<>();
        String sql = "SELECT id_etudiant, id_examen, note, date FROM note ORDER BY date";
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
     * Trouver les notes d'un examen
     */
    public static List<Note> trouverParExamen(int idExamen) throws SQLException {
        List<Note> liste = new ArrayList<>();
        String sql = "SELECT id_etudiant, id_examen, note, date FROM note WHERE id_examen = ? ORDER BY id_etudiant";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idExamen);
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
        String sql = "INSERT INTO note (id_etudiant, id_examen, note, date) VALUES (?, ?, ?, CURRENT_DATE)";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id_etudiant);
            stmt.setInt(2, this.id_examen);
            stmt.setInt(3, this.valeur);

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
        String sql = "UPDATE note SET id_etudiant = ?, id_examen = ?, note = ?, date = CURRENT_DATE WHERE id_etudiant = ? AND id_examen = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id_etudiant);
            stmt.setInt(2, this.id_examen);
            stmt.setInt(3, this.valeur);
            stmt.setInt(4, this.id_etudiant);
            stmt.setInt(5, this.id_examen);

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

        String sql = "DELETE FROM note WHERE id_etudiant = ? AND id_examen = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id_etudiant);
            stmt.setInt(2, this.id_examen);
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

        Note fresh = trouverParIdEtudiantExamen(this.id_etudiant, this.id_examen);
        if (fresh != null) {
            this.id_etudiant = fresh.id_etudiant;
            this.id_examen = fresh.id_examen;
            this.valeur = fresh.valeur;
            this.date = fresh.date;
            return true;
        }
        return false;
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Hydrater un objet depuis un ResultSet
     */
    private static Note creerDepuisResultSet(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.id_etudiant = rs.getInt("id_etudiant");
        note.id_examen = rs.getInt("id_examen");
        note.valeur = rs.getInt("note");
        note.date = rs.getDate("date").toLocalDate();
        note.persisted = true;
        return note;
    }

    /**
     * Vérifier si l'objet est persisté en base de données
     */
    public boolean estPersiste() {
        return persisted;
    }

    // Getters et Setters
    public int getIdEtudiant() {
        return id_etudiant;
    }
    public void setIdEtudiant(int id_etudiant) {
        this.id_etudiant = id_etudiant;
    }
    public int getIdExamen() {
        return id_examen;
    }
    public void setIdExamen(int id_examen) {
        this.id_examen = id_examen;
    }
    public int getValeur() {
        return valeur;
    }
    public void setValeur(int valeur) {
        this.valeur = valeur;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
