package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DatabaseManager;
import util.Role;

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

    // ================ Méthodes pour le controllers ================

    public static void creationEvaluationParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        if (!Role.estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String dateDebutStr = request.getParameter("date_debut");
        String dateFinStr = request.getParameter("date_fin");
        String semestreStr = request.getParameter("semestre");

        try {
            if (dateDebutStr == null || dateDebutStr.isEmpty() || dateFinStr == null || dateFinStr.isEmpty() || 
                semestreStr == null || semestreStr.isEmpty()) {
                request.setAttribute("error", "Tous les champs sont requis.");
                request.getRequestDispatcher("/WEB-INF/views/creerEvaluation.jsp").forward(request, response);
                return;
            }

            int semestre = Integer.parseInt(semestreStr);
            
            // Convertir les dates du format datetime-local
            java.time.LocalDateTime dateDebut = java.time.LocalDateTime.parse(dateDebutStr);
            java.time.LocalDateTime dateFin = java.time.LocalDateTime.parse(dateFinStr);

            model.Evaluation evaluation = new model.Evaluation(dateDebut, dateFin, semestre);
            evaluation.save();
            
            request.setAttribute("success", "Évaluation créée avec succès (ID: " + evaluation.getId() + ")");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Format numérique invalide");
        } catch (java.time.format.DateTimeParseException e) {
            request.setAttribute("error", "Format de date invalide");
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/creerEvaluation.jsp").forward(request, response);
    }

    public static java.util.List<java.util.Map<String, Object>> obtenirEvaluationsDisponibles(int idEtudiant) throws SQLException {
        java.util.List<java.util.Map<String, Object>> evaluations = new java.util.ArrayList<>();
        
        // Récupérer la spécialité de l'étudiant
        Utilisateur etudiant = Utilisateur.trouverParId(idEtudiant);
        if (etudiant == null || etudiant.getIdSpecialite() <= 0) {
            return evaluations;
        }

        int idSpecialite = etudiant.getIdSpecialite();

        // Récupérer toutes les évaluations et les matières de la spécialité
        String sql = "SELECT DISTINCT e.id, e.date_debut, e.date_fin, e.semestre, " +
                 "m.id as matiere_id, m.nom as matiere_nom, m.semestre as matiere_semestre " +
                 "FROM evaluation e, matiere m " +
                 "WHERE m.id_specialite = ? " +
                 "ORDER BY e.date_fin DESC";
        
        try (java.sql.Connection conn = util.DatabaseManager.obtenirConnexion();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSpecialite);
            java.sql.ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                java.util.Map<String, Object> eval = new java.util.HashMap<>();
                int evalId = rs.getInt("id");
                int matiereId = rs.getInt("matiere_id");
                
                eval.put("evaluation_id", evalId);
                eval.put("matiere_id", matiereId);
                eval.put("matiere_nom", rs.getString("matiere_nom"));
                eval.put("date_debut", rs.getObject("date_debut"));
                eval.put("date_fin", rs.getObject("date_fin"));
                eval.put("semestre", rs.getInt("semestre"));
                eval.put("matiere_semestre", rs.getInt("matiere_semestre"));
                
                // Vérifier si l'étudiant a déjà répondu
                boolean aRepondu = model.A_Repondu_Evaluation.aRepondu(idEtudiant, matiereId, evalId);
                
                // Vérifier si l'évaluation est toujours ouverte
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime dateDebut = rs.getObject("date_debut", java.time.LocalDateTime.class);
                java.time.LocalDateTime dateFin = rs.getObject("date_fin", java.time.LocalDateTime.class);
                int semestreEval = rs.getInt("semestre");
                int semestreMat = rs.getInt("matiere_semestre");

                boolean semestreMismatch = semestreEval != semestreMat;
                boolean horsIntervalle = now.isBefore(dateDebut) || now.isAfter(dateFin);
                
                String status;
                if (aRepondu) {
                    status = "answered";
                } else if (semestreMismatch || horsIntervalle) {
                    status = "closed";
                } else {
                    status = "open";
                }
                
                eval.put("status", status);
                
                // Calculer le taux de réponse
                try {
                    int tauxReponse = model.Reponse_Evaluation.calculerTauxReponseParMatiere(evalId, matiereId);
                    eval.put("taux_reponse", tauxReponse);
                } catch (Exception e) {
                    eval.put("taux_reponse", 0);
                }
                
                evaluations.add(eval);
            }
        }
        
        return evaluations;
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