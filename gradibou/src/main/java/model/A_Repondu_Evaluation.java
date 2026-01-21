package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DatabaseManager;

public class A_Repondu_Evaluation {
    private int id_etudiant;
    private int id_matiere;
    private int id_evaluation;
    private boolean persisted = false;

    // Constructeurs
    public A_Repondu_Evaluation() {}

    public A_Repondu_Evaluation(int id_etudiant, int id_matiere, int id_evaluation) {
        this.id_etudiant = id_etudiant;
        this.id_matiere = id_matiere;
        this.id_evaluation = id_evaluation;
        this.persisted = false;
    }

    // ==================== Méthodes de persistance ====================

    /**
     * Insérer un nouveau suivi d'évaluation
     */
    public void insert() throws SQLException {
        String sql = "INSERT INTO a_repondu_evaluation (id_etudiant, id_matiere, id_evaluation) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_etudiant);
            stmt.setInt(2, id_matiere);
            stmt.setInt(3, id_evaluation);
            stmt.executeUpdate();
            this.persisted = true;
        }
    }

    /**
     * Sauvegarder le suivi d'évaluation (insert uniquement)
     */
    public void save() throws SQLException {
        if (!persisted) {
            insert();
        }
    }

    /**
     * Supprimer le suivi d'évaluation
     */
    public void supprimer() throws SQLException {
        if (!persisted) {
            throw new SQLException("Impossible de supprimer un suivi qui n'existe pas en base");
        }
        String sql = "DELETE FROM a_repondu_evaluation WHERE id_etudiant = ? AND id_matiere = ? AND id_evaluation = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_etudiant);
            stmt.setInt(2, id_matiere);
            stmt.setInt(3, id_evaluation);
            stmt.executeUpdate();
            this.persisted = false;
        }
    }

    /**
     * Vérifier si un étudiant a répondu à une évaluation pour une matière
     */
    public static boolean aRepondu(int id_etudiant, int id_matiere, int id_evaluation) throws SQLException {
        String sql = "SELECT COUNT(*) FROM a_repondu_evaluation WHERE id_etudiant = ? AND id_matiere = ? AND id_evaluation = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_etudiant);
            stmt.setInt(2, id_matiere);
            stmt.setInt(3, id_evaluation);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Trouver un suivi d'évaluation
     */
    public static A_Repondu_Evaluation trouver(int id_etudiant, int id_matiere, int id_evaluation) throws SQLException {
        String sql = "SELECT id_etudiant, id_matiere, id_evaluation FROM a_repondu_evaluation " +
                     "WHERE id_etudiant = ? AND id_matiere = ? AND id_evaluation = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_etudiant);
            stmt.setInt(2, id_matiere);
            stmt.setInt(3, id_evaluation);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                A_Repondu_Evaluation repondu = new A_Repondu_Evaluation(
                    rs.getInt("id_etudiant"),
                    rs.getInt("id_matiere"),
                    rs.getInt("id_evaluation")
                );
                repondu.persisted = true;
                return repondu;
            }
        }
        return null;
    }

    // Getters et Setters
    public int getId_etudiant() {
        return id_etudiant;
    }

    public void setId_etudiant(int id_etudiant) {
        this.id_etudiant = id_etudiant;
    }

    public int getId_matiere() {
        return id_matiere;
    }

    public void setId_matiere(int id_matiere) {
        this.id_matiere = id_matiere;
    }

    public int getId_evaluation() {
        return id_evaluation;
    }

    public void setId_evaluation(int id_evaluation) {
        this.id_evaluation = id_evaluation;
    }

    @Override
    public String toString() {
        return "A_Repondu_Evaluation{" +
                "id_etudiant=" + id_etudiant +
                ", id_matiere=" + id_matiere +
                ", id_evaluation=" + id_evaluation +
                '}';
    }
}