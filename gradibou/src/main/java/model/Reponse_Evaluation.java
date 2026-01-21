package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import util.DatabaseManager;

public class Reponse_Evaluation {
    private int id;
    private int qualite_support;
    private int qualite_equipe;
    private int qualite_materiel;
    private int pertinence_examen;
    private int temps_par_semaine;
    private int utilite_pour_formation;
    private String commentaires;
    private int id_matiere;
    private int id_evaluation;
    private boolean persisted = false;

    // Constructeurs
    public Reponse_Evaluation() {}

    public Reponse_Evaluation(int qualite_support, int qualite_equipe, int qualite_materiel,
                             int pertinence_examen, int temps_par_semaine, int utilite_pour_formation,
                             String commentaires, int id_matiere, int id_evaluation) {
        this.qualite_support = qualite_support;
        this.qualite_equipe = qualite_equipe;
        this.qualite_materiel = qualite_materiel;
        this.pertinence_examen = pertinence_examen;
        this.temps_par_semaine = temps_par_semaine;
        this.utilite_pour_formation = utilite_pour_formation;
        this.commentaires = commentaires;
        this.id_matiere = id_matiere;
        this.id_evaluation = id_evaluation;
    }

    public Reponse_Evaluation(int id, int qualite_support, int qualite_equipe, int qualite_materiel,
                             int pertinence_examen, int temps_par_semaine, int utilite_pour_formation,
                             String commentaires, int id_matiere, int id_evaluation) {
        this.id = id;
        this.qualite_support = qualite_support;
        this.qualite_equipe = qualite_equipe;
        this.qualite_materiel = qualite_materiel;
        this.pertinence_examen = pertinence_examen;
        this.temps_par_semaine = temps_par_semaine;
        this.utilite_pour_formation = utilite_pour_formation;
        this.commentaires = commentaires;
        this.id_matiere = id_matiere;
        this.id_evaluation = id_evaluation;
        this.persisted = false;
    }

    // ==================== Méthodes de persistance ====================

    /**
     * Insérer une nouvelle réponse d'évaluation
     */
    public void insert() throws SQLException {
        String sql = "INSERT INTO reponse_evaluation (qualité_support, qualité_equipe, qualité_materiel, " +
                     "pertinence_examen, temps_par_semaine, utilite_pour_formation, commentaires, id_matiere, id_evaluation) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, qualite_support);
            stmt.setInt(2, qualite_equipe);
            stmt.setInt(3, qualite_materiel);
            stmt.setInt(4, pertinence_examen);
            stmt.setInt(5, temps_par_semaine);
            stmt.setInt(6, utilite_pour_formation);
            stmt.setString(7, commentaires);
            stmt.setInt(8, id_matiere);
            stmt.setInt(9, id_evaluation);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
                this.persisted = true;
            }
        }
    }

    /**
     * Sauvegarder la réponse d'évaluation (insert ou update selon si elle existe déjà)
     */
    public void save() throws SQLException {
        if (persisted) {
            update();
        } else {
            insert();
        }
    }

    /**
     * Mettre à jour la réponse d'évaluation existante
     */
    private void update() throws SQLException {
        String sql = "UPDATE reponse_evaluation SET qualité_support = ?, qualité_equipe = ?, qualité_materiel = ?, " +
                     "pertinence_examen = ?, temps_par_semaine = ?, utilite_pour_formation = ?, commentaires = ?, " +
                     "id_matiere = ?, id_evaluation = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, qualite_support);
            stmt.setInt(2, qualite_equipe);
            stmt.setInt(3, qualite_materiel);
            stmt.setInt(4, pertinence_examen);
            stmt.setInt(5, temps_par_semaine);
            stmt.setInt(6, utilite_pour_formation);
            stmt.setString(7, commentaires);
            stmt.setInt(8, id_matiere);
            stmt.setInt(9, id_evaluation);
            stmt.setInt(10, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Supprimer la réponse d'évaluation
     */
    public void supprimer() throws SQLException {
        if (!persisted) {
            throw new SQLException("Impossible de supprimer une réponse qui n'existe pas en base");
        }
        String sql = "DELETE FROM reponse_evaluation WHERE id = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            this.persisted = false;
        }
    }

    /**
     * Trouver une réponse d'évaluation par ID
     */
    public static Reponse_Evaluation trouverParId(int id) throws SQLException {
        String sql = "SELECT id, qualité_support, qualité_equipe, qualité_materiel, pertinence_examen, " +
                     "temps_par_semaine, utilite_pour_formation, commentaires, id_matiere, id_evaluation " +
                     "FROM reponse_evaluation WHERE id = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Reponse_Evaluation rep = new Reponse_Evaluation(
                    rs.getInt("qualité_support"),
                    rs.getInt("qualité_equipe"),
                    rs.getInt("qualité_materiel"),
                    rs.getInt("pertinence_examen"),
                    rs.getInt("temps_par_semaine"),
                    rs.getInt("utilite_pour_formation"),
                    rs.getString("commentaires"),
                    rs.getInt("id_matiere"),
                    rs.getInt("id_evaluation")
                );
                rep.id = rs.getInt("id");
                rep.persisted = true;
                return rep;
            }
        }
        return null;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQualite_support() {
        return qualite_support;
    }

    public void setQualite_support(int qualite_support) {
        this.qualite_support = qualite_support;
    }

    public int getQualite_equipe() {
        return qualite_equipe;
    }

    public void setQualite_equipe(int qualite_equipe) {
        this.qualite_equipe = qualite_equipe;
    }

    public int getQualite_materiel() {
        return qualite_materiel;
    }

    public void setQualite_materiel(int qualite_materiel) {
        this.qualite_materiel = qualite_materiel;
    }

    public int getPertinence_examen() {
        return pertinence_examen;
    }

    public void setPertinence_examen(int pertinence_examen) {
        this.pertinence_examen = pertinence_examen;
    }

    public int getTemps_par_semaine() {
        return temps_par_semaine;
    }

    public void setTemps_par_semaine(int temps_par_semaine) {
        this.temps_par_semaine = temps_par_semaine;
    }

    public int getUtilite_pour_formation() {
        return utilite_pour_formation;
    }

    public void setUtilite_pour_formation(int utilite_pour_formation) {
        this.utilite_pour_formation = utilite_pour_formation;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
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
        return "Reponse_Evaluation{" +
                "id=" + id +
                ", qualite_support=" + qualite_support +
                ", qualite_equipe=" + qualite_equipe +
                ", qualite_materiel=" + qualite_materiel +
                ", pertinence_examen=" + pertinence_examen +
                ", temps_par_semaine=" + temps_par_semaine +
                ", utilite_pour_formation=" + utilite_pour_formation +
                ", commentaires='" + commentaires + '\'' +
                ", id_matiere=" + id_matiere +
                ", id_evaluation=" + id_evaluation +
                '}';
    }
}