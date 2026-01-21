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

    // ==================== Calcul des statistiques ====================

    // Global

    // Par specialite
    public static int calculerTauxReponseParSpecialite(int id_evaluation, int id_specialite) throws SQLException {
        String sqlTotal = "SELECT COUNT(e.id_utilisateur) AS total " +
                  "FROM etudiant e " +
                  "WHERE e.id_specialite = ?";
        String sqlRepondu = "SELECT COUNT(DISTINCT are.id_etudiant) AS repondu " +
                            "FROM a_repondu_evaluation are " +
                            "JOIN matiere m ON are.id_matiere = m.id " +
                            "WHERE are.id_evaluation = ? AND m.id_specialite = ?";

        int total = 0;
        int repondu = 0;

        try (Connection conn = DatabaseManager.obtenirConnexion()) {
            try (PreparedStatement totalStmt = conn.prepareStatement(sqlTotal)) {
                totalStmt.setInt(1, id_specialite);
                ResultSet totalRs = totalStmt.executeQuery();
                if (totalRs.next()) {
                    total = totalRs.getInt("total");
                }
            }

            try (PreparedStatement reponduStmt = conn.prepareStatement(sqlRepondu)) {
                reponduStmt.setInt(1, id_evaluation);
                reponduStmt.setInt(2, id_specialite);
                ResultSet reponduRs = reponduStmt.executeQuery();
                if (reponduRs.next()) {
                    repondu = reponduRs.getInt("repondu");
                }
            }
        }

        if (total == 0) {
            return 0;
        }
        return (repondu * 100) / total;
    }

    public static double calculerMoyenneGeneraleParSpecialite(int id_evaluation, int id_specialite) throws SQLException {
        String sqlMatieres = "SELECT id FROM matiere WHERE id_specialite = ?";
        double somme = 0.0;
        int nbMatieres = 0;

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement matieresStmt = conn.prepareStatement(sqlMatieres)) {
            matieresStmt.setInt(1, id_specialite);
            try (ResultSet matieresRs = matieresStmt.executeQuery()) {
                while (matieresRs.next()) {
                    int idMatiere = matieresRs.getInt("id");
                    somme += calculerMoyenneGeneraleParMatiere(id_evaluation, idMatiere);
                    nbMatieres++;
                }
            }
        }

        if (nbMatieres == 0) {
            return 0.0;
        }
        return somme / nbMatieres;
    }

    public static int[] recupererMeilleurEtPireMatiere(int id_evaluation, int id_specialite) throws SQLException {
        String sqlMatieres = "SELECT id FROM matiere WHERE id_specialite = ?";
        int meilleurId = -1;
        int pireId = -1;
        double meilleureMoyenne = -1.0;
        double pireMoyenne = 101.0;

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement matieresStmt = conn.prepareStatement(sqlMatieres)) {
            matieresStmt.setInt(1, id_specialite);
            try (ResultSet matieresRs = matieresStmt.executeQuery()) {
                while (matieresRs.next()) {
                    int idMatiere = matieresRs.getInt("id");
                    double moyenne = calculerMoyenneGeneraleParMatiere(id_evaluation, idMatiere);
                    if (moyenne > meilleureMoyenne) {
                        meilleureMoyenne = moyenne;
                        meilleurId = idMatiere;
                    }
                    if (moyenne < pireMoyenne) {
                        pireMoyenne = moyenne;
                        pireId = idMatiere;
                    }
                }
            }
        }

        return new int[] {meilleurId, pireId};
    }

    // Par matière

    public static int calculerTauxReponseParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        String sqlTotal = "SELECT COUNT(e.id_utilisateur) AS total " +
                  "FROM etudiant e " +
                  "JOIN matiere m ON e.id_specialite = m.id_specialite " +
                  "WHERE m.id = ?";
        String sqlRepondu = "SELECT COUNT(DISTINCT id_etudiant) AS repondu FROM a_repondu_evaluation " +
                            "WHERE id_evaluation = ? AND id_matiere = ?";

        int total = 0;
        int repondu = 0;

        try (Connection conn = DatabaseManager.obtenirConnexion()) {
            try (PreparedStatement totalStmt = conn.prepareStatement(sqlTotal)) {
                totalStmt.setInt(1, id_matiere);
                ResultSet totalRs = totalStmt.executeQuery();
                if (totalRs.next()) {
                    total = totalRs.getInt("total");
                }
            }

            try (PreparedStatement reponduStmt = conn.prepareStatement(sqlRepondu)) {
                reponduStmt.setInt(1, id_evaluation);
                reponduStmt.setInt(2, id_matiere);
                ResultSet reponduRs = reponduStmt.executeQuery();
                if (reponduRs.next()) {
                    repondu = reponduRs.getInt("repondu");
                }
            }
        }

        if (total == 0) {
            return 0;
        }
        return (repondu * 100) / total;
    }

    public static double calculerMoyenneQualiteSupportParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        String sql = "SELECT AVG(qualité_support) AS moyenne FROM reponse_evaluation WHERE id_evaluation = ? AND id_matiere = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_evaluation);
            stmt.setInt(2, id_matiere);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("moyenne");
            }
        }
        return 0.0;
    }

    public static double calculerMoyenneQualiteEquipeParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        String sql = "SELECT AVG(qualité_equipe) AS moyenne FROM reponse_evaluation WHERE id_evaluation = ? AND id_matiere = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_evaluation);
            stmt.setInt(2, id_matiere);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("moyenne");
            }
        }
        return 0.0;
    }

    public static double calculerMoyenneQualiteMaterielParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        String sql = "SELECT AVG(qualité_materiel) AS moyenne FROM reponse_evaluation WHERE id_evaluation = ? AND id_matiere = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_evaluation);
            stmt.setInt(2, id_matiere);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("moyenne");
            }
        }
        return 0.0;
    }

    public static double calculerMoyennePertinenceExamenParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        String sql = "SELECT AVG(pertinence_examen) AS moyenne FROM reponse_evaluation WHERE id_evaluation = ? AND id_matiere = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_evaluation);
            stmt.setInt(2, id_matiere);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("moyenne");
            }
        }
        return 0.0;
    }

    public static double calculerMoyenneGeneraleParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        double support = calculerMoyenneQualiteSupportParMatiere(id_evaluation, id_matiere);
        double equipe = calculerMoyenneQualiteEquipeParMatiere(id_evaluation, id_matiere);
        double materiel = calculerMoyenneQualiteMaterielParMatiere(id_evaluation, id_matiere);
        double pertinence = calculerMoyennePertinenceExamenParMatiere(id_evaluation, id_matiere);
        
        return (support + equipe + materiel + pertinence) / 4.0;
    }

    public static double calculerProportionOuiUtilitePourFormationParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        String sql = "SELECT SUM(CASE WHEN utilite_pour_formation = 1 THEN 1 ELSE 0 END)::double precision / NULLIF(COUNT(*), 0) AS proportion " +
                    "FROM reponse_evaluation WHERE id_evaluation = ? AND id_matiere = ?";
        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_evaluation);
            stmt.setInt(2, id_matiere);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("proportion");
            }
        }
        return 0.0;
    }

    public static double[] calculerProportionTempsParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        double[] proportions = new double[5];
        String sqlTotal = "SELECT COUNT(*) AS total FROM reponse_evaluation WHERE id_evaluation = ? AND id_matiere = ?";
        String sqlCounts = "SELECT temps_par_semaine, COUNT(*) AS cnt FROM reponse_evaluation " +
                        "WHERE id_evaluation = ? AND id_matiere = ? GROUP BY temps_par_semaine";

        try (Connection conn = DatabaseManager.obtenirConnexion()) {
            int total = 0;
            try (PreparedStatement totalStmt = conn.prepareStatement(sqlTotal)) {
                totalStmt.setInt(1, id_evaluation);
                totalStmt.setInt(2, id_matiere);
                ResultSet totalRs = totalStmt.executeQuery();
                if (totalRs.next()) {
                    total = totalRs.getInt("total");
                }
            }
            if (total == 0) {
                return proportions;
            }

            try (PreparedStatement countStmt = conn.prepareStatement(sqlCounts)) {
                countStmt.setInt(1, id_evaluation);
                countStmt.setInt(2, id_matiere);
                ResultSet rs = countStmt.executeQuery();
                while (rs.next()) {
                    int temps = rs.getInt("temps_par_semaine");
                    if (temps >= 1 && temps <= 5) {
                        proportions[temps - 1] = rs.getDouble("cnt") / total;
                    }
                }
            }
        }
        return proportions;
    }

    public static String[] recupererCommentairesParMatiere(int id_evaluation, int id_matiere) throws SQLException {
        String sql = "SELECT commentaires FROM reponse_evaluation WHERE id_evaluation = ? AND id_matiere = ? AND commentaires IS NOT NULL";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_evaluation);
            stmt.setInt(2, id_matiere);
            ResultSet rs = stmt.executeQuery();

            java.util.List<String> commentairesList = new java.util.ArrayList<>();
            while (rs.next()) {
                commentairesList.add(rs.getString("commentaires"));
            }
            return commentairesList.toArray(new String[0]);
        }
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