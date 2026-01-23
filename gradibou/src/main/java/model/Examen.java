package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.DatabaseManager;
import util.Role;

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
     * Trouver tous les examens d'une matière
     */
    public static List<Examen> trouverParMatiere(int idMatiere) throws SQLException {
        List<Examen> liste = new ArrayList<>();
        String sql = "SELECT id, nom, coefficient, date, id_matiere FROM examen WHERE id_matiere = ? ORDER BY date";
        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMatiere);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(creerDepuisResultSet(rs));
                }
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
        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
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

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nom);
            stmt.setInt(2, this.coefficient);
            stmt.setObject(3, this.date);
            stmt.setInt(4, this.id_matiere);
            stmt.setInt(5, this.id);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer l'examen (avec suppression en cascade)
     * - Supprime toutes les notes associées à cet examen
     */
    public boolean supprimer() throws SQLException {
        Connection conn = DatabaseManager.obtenirConnexion();
        return supprimer(conn);
    }
    
    /**
     * Supprimer l'examen avec une connexion fournie (pour les suppressions en cascade)
     */
    public boolean supprimer(Connection conn) throws SQLException {
        if (!persisted) {
            return false;
        }

        // 1. Supprimer toutes les notes de cet examen
        String deleteNotesSql = "DELETE FROM note WHERE id_examen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteNotesSql)) {
            stmt.setInt(1, this.id);
            stmt.executeUpdate();
        }
        
        // 2. Supprimer l'examen
        String sql = "DELETE FROM examen WHERE id = ?";
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
     * Creer un objet depuis un ResultSet
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

    // ================ Méthodes pour le controllers ================

    public static void creationExamenParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendRedirect(request.getContextPath() + "/app/login");
            return;
        }

        if (!Role.estAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String nom = request.getParameter("nom");
        String coefficientStr = request.getParameter("coefficient");
        String idmatiereStr = request.getParameter("matiereId");
        int matiereId = -1;

        try {
            if (idmatiereStr != null && !idmatiereStr.isEmpty()) {
                matiereId = Integer.parseInt(idmatiereStr);
            }
            
            if (nom == null || nom.isEmpty() || coefficientStr == null || coefficientStr.isEmpty() || matiereId == -1) {
                response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matiereId + "&error=" + java.net.URLEncoder.encode("Tous les champs sont requis.", "UTF-8"));
                return;
            }

            int coefficient = Integer.parseInt(coefficientStr);
            int idmatiere = Integer.parseInt(idmatiereStr);

            model.Examen examen = new model.Examen(nom, coefficient, idmatiere);
            
            if (examen.save()) {
                response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matiereId + "&success=" + java.net.URLEncoder.encode("Examen créé avec succès", "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matiereId + "&error=" + java.net.URLEncoder.encode("Erreur lors de la création de l'examen", "UTF-8"));
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matiereId + "&error=" + java.net.URLEncoder.encode("Format numérique invalide", "UTF-8"));
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matiereId + "&error=" + java.net.URLEncoder.encode("Erreur BD: " + e.getMessage(), "UTF-8"));
        }
    }

    public static String afficherExamens(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            boolean isAdmin = Role.estAdmin(session);
            boolean isProfesseur = Role.estProfesseur(session);
            
            String idMatStr = request.getParameter("matId");
            if (idMatStr != null && !idMatStr.isEmpty()) {
                int idMat = Integer.parseInt(idMatStr);
                
                // Vérifier que le professeur a accès à cette matière
                if (isProfesseur && !isAdmin) {
                    Utilisateur currentUser = (Utilisateur) session.getAttribute("utilisateur");
                    if (currentUser != null) {
                        Matiere matiere = model.Matiere.trouverParId(idMat);
                        if (matiere == null || matiere.getProfId() != currentUser.getId()) {
                            request.setAttribute("error", "Accès refusé : vous n'avez pas accès à cette matière");
                            return "/WEB-INF/views/error.jsp";
                        }
                    }
                }
                
                request.setAttribute("examens", model.Examen.trouverParMatiere(idMat));
                request.setAttribute("matiere", model.Matiere.trouverParId(idMat));
                
                // Charger les informations du professeur
                Matiere matiere = model.Matiere.trouverParId(idMat);
                if (matiere != null && matiere.getProfId() > 0) {
                    Utilisateur professeur = model.Utilisateur.trouverParId(matiere.getProfId());
                    if (professeur != null) {
                        request.setAttribute("professeur", professeur);
                    }
                }
            } else {
                request.setAttribute("examens", model.Examen.trouverTous());
            }
            return "/WEB-INF/views/listeExamens.jsp";
        } catch (Exception e) {
            request.setAttribute("error", "Erreur : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
        }
    }

    public static void supprimerExamen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        boolean isAdmin = Role.estAdmin(session);
        boolean isProfesseur = Role.estProfesseur(session);
        
        if (!isAdmin && !isProfesseur) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            model.Examen e = model.Examen.trouverParId(id);
            int matId = -1;
            
            if (e != null) {
                matId = e.getId_matiere();
                
                // Vérifier que le professeur a accès à cette matière
                if (isProfesseur && !isAdmin) {
                    Utilisateur currentUser = (Utilisateur) session.getAttribute("utilisateur");
                    if (currentUser != null) {
                        Matiere matiere = model.Matiere.trouverParId(matId);
                        if (matiere == null || matiere.getProfId() != currentUser.getId()) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }
                    }
                }
                
                e.supprimer();
                
                if (matId != -1) {
                    response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matId + "&success=" + java.net.URLEncoder.encode("Examen supprimé avec succès", "UTF-8"));
                } else {
                    response.sendRedirect(request.getContextPath() + "/app/gestion/specialites");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin/examens?error=" + java.net.URLEncoder.encode("Examen introuvable", "UTF-8"));
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/app/admin/examens?error=" + java.net.URLEncoder.encode("Erreur lors de la suppression : " + e.getMessage(), "UTF-8"));
        }
    }

    public static void modifierExamen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        boolean isAdmin = Role.estAdmin(session);
        boolean isProfesseur = Role.estProfesseur(session);
        
        if (!isAdmin && !isProfesseur) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            int coefficient = Integer.parseInt(request.getParameter("coefficient"));
             // Date n'est pas modifiable dans cette version simplifiée ou on garde l'existante
            
            model.Examen e = model.Examen.trouverParId(id);
            int matId = -1;
            
            if (e != null) {
                matId = e.getId_matiere();
                
                // Vérifier que le professeur a accès à cette matière
                if (isProfesseur && !isAdmin) {
                    Utilisateur currentUser = (Utilisateur) session.getAttribute("utilisateur");
                    if (currentUser != null) {
                        Matiere matiere = model.Matiere.trouverParId(matId);
                        if (matiere == null || matiere.getProfId() != currentUser.getId()) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }
                    }
                }
                
                e.setNom(nom);
                e.setCoefficient(coefficient);
                e.save();
                
                response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matId + "&success=" + java.net.URLEncoder.encode("Examen modifié avec succès", "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin/examens?error=" + java.net.URLEncoder.encode("Examen introuvable", "UTF-8"));
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/app/admin/examens?error=" + java.net.URLEncoder.encode("Erreur lors de la modification : " + e.getMessage(), "UTF-8"));
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
