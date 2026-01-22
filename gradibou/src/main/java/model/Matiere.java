package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.DatabaseManager;
import util.Role;

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

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return creerDepuisResultSet(rs);
                }
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

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                liste.add(creerDepuisResultSet(rs));
            }
        }
        return liste;
    }

    /**
     * Trouver les matières d'une spécialité
     */
    public static List<Matiere> trouverParSpecialite(int specialiteId) throws SQLException {
        List<Matiere> liste = new ArrayList<>();
        String sql = "SELECT id, nom, semestre, id_specialite, id_prof FROM matiere WHERE id_specialite = ? ORDER BY nom";

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, specialiteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(creerDepuisResultSet(rs));
                }
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
     * Insérer une nouvelle matière en base de données
     */
    private boolean insert() throws SQLException {
        String sql = "INSERT INTO matiere (nom, semestre, id_specialite, id_prof) VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

     //Mettre à jour une matière existante
    private boolean update() throws SQLException {
        String sql = "UPDATE matiere SET nom = ?, semestre = ?, id_specialite = ?, id_prof = ? WHERE id = ?";

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nom);
            stmt.setInt(2, this.semestre);
            stmt.setInt(3, this.specialiteId);
            stmt.setInt(4, this.profId);
            stmt.setInt(5, this.id);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer la matière (avec suppression en cascade)
     * - Supprime tous les examens de la matière (qui supprimeront leurs notes)
     * - Supprime les réponses d'évaluation pour cette matière
     * - Supprime les a_repondu_evaluation pour cette matière
     */   
    public boolean supprimer() throws SQLException {
        Connection conn = DatabaseManager.obtenirConnexion();
        return supprimer(conn);
    }
    
    /**
     * Supprimer la matière avec une connexion fournie (pour les suppressions en cascade)
     */
    public boolean supprimer(Connection conn) throws SQLException {
        if (!persisted) {
            return false;
        }

        // 1. Supprimer tous les examens de cette matière (cascade vers notes)
        List<Examen> examens = Examen.trouverParMatiere(this.id);
        for (Examen e : examens) {
            e.supprimer(conn);
        }
        
        // 2. Supprimer les a_repondu_evaluation pour cette matière
        String deleteAReponduSql = "DELETE FROM a_repondu_evaluation WHERE id_matiere = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteAReponduSql)) {
            stmt.setInt(1, this.id);
            stmt.executeUpdate();
        }
        
        // 3. Supprimer les réponses d'évaluation pour cette matière
        String deleteReponsesSql = "DELETE FROM reponse_evaluation WHERE id_matiere = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteReponsesSql)) {
            stmt.setInt(1, this.id);
            stmt.executeUpdate();
        }
        
        // 4. Supprimer la matière
        String sql = "DELETE FROM matiere WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
     * Creer un objet depuis un ResultSet
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

    // ================ Méthodes pour le controllers ================

    public static void creationMatiereParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (!Role.estAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            request.setAttribute("specialites", model.Specialite.trouverToutes());
            request.setAttribute("professeurs", model.Utilisateur.trouverTousLesProfesseurs());
        } catch (Exception e) {
             e.printStackTrace();
             request.setAttribute("error", "Erreur lors du chargement des listes: " + e.getMessage());
        }

        // Traitement du formulaire (POST)
        String nom = request.getParameter("nom");
        String semestreStr = request.getParameter("semestre");
        String specialiteIdStr = request.getParameter("specialiteId");
        String profIdStr = request.getParameter("profId");

        try {
            // Check if all fields (including profId) are present
            if (nom == null || nom.isEmpty() || semestreStr == null || semestreStr.isEmpty() || 
                specialiteIdStr == null || specialiteIdStr.isEmpty() ||
                profIdStr == null || profIdStr.isEmpty()) {
                request.setAttribute("error", "Tous les champs sont requis, y compris le professeur.");
                request.getRequestDispatcher("/WEB-INF/views/creerMatiere.jsp").forward(request, response);
                return;
            }

            int semestre = Integer.parseInt(semestreStr);
            int specialiteId = Integer.parseInt(specialiteIdStr);
            int profId = Integer.parseInt(profIdStr);

            model.Matiere matiere = new model.Matiere(nom, semestre, specialiteId, profId);
            
            if (matiere.save()) {
                request.setAttribute("success", "Matière créée avec succès");
            } else {
                request.setAttribute("error", "Erreur lors de la création de la matière");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Format numérique invalide");
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/creerMatiere.jsp").forward(request, response);
    }

    public static String afficherMatieres(HttpServletRequest request) {
        try {
            String idSpecStr = request.getParameter("specId");
            if (idSpecStr != null && !idSpecStr.isEmpty()) {
                int idSpec = Integer.parseInt(idSpecStr);
                request.setAttribute("matieres", model.Matiere.trouverParSpecialite(idSpec));
                request.setAttribute("specialite", model.Specialite.trouverParId(idSpec));
            } else {
                request.setAttribute("matieres", model.Matiere.trouverToutes());
                request.setAttribute("specialite", null);
            }
            request.setAttribute("professeurs", model.Utilisateur.trouverTousLesProfesseurs());
            return "/WEB-INF/views/listeMatieres.jsp";
        } catch (Exception e) {
            request.setAttribute("error", "Erreur : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
        }
    }

    public static void supprimerMatiere(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!Role.estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            model.Matiere m = model.Matiere.trouverParId(id);
            if (m != null) {
                m.supprimer();
            }
            // Redirection intelligente : on essaie de revenir à la liste filtrée si on a l'info
            String specId = request.getParameter("specId");
            if (specId != null && !specId.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/app/admin/matieres?specId=" + specId);
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin/specialites");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    public static void modifierMatiere(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!Role.estAdmin(request.getSession(false))) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN);
             return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            int semestre = Integer.parseInt(request.getParameter("semestre"));
            int profId = Integer.parseInt(request.getParameter("profId"));
            
            model.Matiere m = model.Matiere.trouverParId(id);
            if (m != null) {
                m.setNom(nom);
                m.setSemestre(semestre);
                m.setProfId(profId);
                m.save();
            }
            String specId = request.getParameter("specId");
            if (specId != null && !specId.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/app/admin/matieres?specId=" + specId);
            } else {
                 response.sendRedirect(request.getContextPath() + "/app/admin/specialites");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
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