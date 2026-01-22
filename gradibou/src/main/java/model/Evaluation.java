package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseManager;

public class Evaluation {
    private int id;
    private LocalDateTime date_debut;
    private LocalDateTime date_fin;
    private int semestre;
    private boolean persisted = false;

    // Constructeurs
    public Evaluation() {}

    public Evaluation(int id, LocalDateTime date_debut, LocalDateTime date_fin, int semestre) {
        this.id = id;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.semestre = semestre;
    }

    public Evaluation(LocalDateTime date_debut, LocalDateTime date_fin, int semestre) {
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.semestre = semestre;
        this.persisted = false;
    }

    // ==================== Méthodes de persistance ====================

    /**
     * Insérer une nouvelle évaluation
     */
    public void insert() throws SQLException {
        String sql = "INSERT INTO evaluation (date_debut, date_fin, semestre) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, date_debut);
            stmt.setObject(2, date_fin);
            stmt.setInt(3, semestre);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
                this.persisted = true;
            }
        }
    }

    /**
     * Sauvegarder l'évaluation (insert ou update selon si elle existe déjà)
     */
    public void save() throws SQLException {
        if (persisted) {
            update();
        } else {
            insert();
        }
    }

    /**
     * Mettre à jour l'évaluation existante
     */
    private void update() throws SQLException {
        String sql = "UPDATE evaluation SET date_debut = ?, date_fin = ?, semestre = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, date_debut);
            stmt.setObject(2, date_fin);
            stmt.setInt(3, semestre);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Supprimer l'évaluation
     */
    public void supprimer() throws SQLException {
        if (!persisted) {
            throw new SQLException("Impossible de supprimer une évaluation qui n'existe pas en base");
        }
        String sql = "DELETE FROM evaluation WHERE id = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            this.persisted = false;
        }
    }

    /**
     * Trouver une évaluation par ID
     */
    public static Evaluation trouverParId(int id) throws SQLException {
        String sql = "SELECT id, date_debut, date_fin, semestre FROM evaluation WHERE id = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Evaluation eval = new Evaluation(
                    rs.getObject("date_debut", LocalDateTime.class),
                    rs.getObject("date_fin", LocalDateTime.class),
                    rs.getInt("semestre")
                );
                eval.id = rs.getInt("id");
                eval.persisted = true;
                return eval;
            }
        }
        return null;
    }

    /**
     * Trouver toutes les évaluations
     */
    public static List<Evaluation> trouverToutes() throws SQLException {
        List<Evaluation> liste = new ArrayList<>();
        String sql = "SELECT id, date_debut, date_fin, semestre FROM evaluation ORDER BY date_fin DESC";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Evaluation eval = new Evaluation(
                    rs.getObject("date_debut", LocalDateTime.class),
                    rs.getObject("date_fin", LocalDateTime.class),
                    rs.getInt("semestre")
                );
                eval.id = rs.getInt("id");
                eval.persisted = true;
                liste.add(eval);
            }
        }
        return liste;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(LocalDateTime date_debut) {
        this.date_debut = date_debut;
    }

    public LocalDateTime getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(LocalDateTime date_fin) {
        this.date_fin = date_fin;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id=" + id +
                ", date_debut=" + date_debut +
                ", date_fin=" + date_fin +
                ", semestre=" + semestre +
                '}';
    }
}