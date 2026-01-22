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

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));

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
        String profEmail = request.getParameter("profEmail");

        try {
            // Check if all fields (including profId) are present
            if (nom == null || nom.isEmpty() || semestreStr == null || semestreStr.isEmpty() || 
                specialiteIdStr == null || specialiteIdStr.isEmpty() ||
                profEmail == null || profEmail.isEmpty()) {
                if (isAjax) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("text/plain;charset=UTF-8");
                    response.getWriter().write("Tous les champs sont requis, y compris le professeur.");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    request.setAttribute("error", "Tous les champs sont requis, y compris le professeur.");
                    request.getRequestDispatcher("/WEB-INF/views/creerMatiere.jsp").forward(request, response);
                }
                return;
            }

            int semestre = Integer.parseInt(semestreStr);
            int specialiteId = Integer.parseInt(specialiteIdStr);

            Utilisateur prof = Utilisateur.trouverParemail(profEmail);
            if (prof == null || !"professeur".equalsIgnoreCase(prof.getRole())) {
                if (isAjax) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("text/plain;charset=UTF-8");
                    response.getWriter().write("Le professeur doit exister et avoir le rôle professeur");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    request.setAttribute("error", "Le professeur doit exister et avoir le rôle professeur");
                    request.getRequestDispatcher("/WEB-INF/views/creerMatiere.jsp").forward(request, response);
                }
                return;
            }
            int profId = prof.getId();

            model.Matiere matiere = new model.Matiere(nom, semestre, specialiteId, profId);
            
            if (matiere.save()) {
                if (isAjax) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
                request.setAttribute("success", "Matière créée avec succès");
            } else {
                if (isAjax) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.setContentType("text/plain;charset=UTF-8");
                    response.getWriter().write("Erreur lors de la création de la matière");
                    return;
                }
                request.setAttribute("error", "Erreur lors de la création de la matière");
            }
        } catch (NumberFormatException e) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Format numérique invalide");
                return;
            }
            request.setAttribute("error", "Format numérique invalide");
        } catch (SQLException e) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Erreur BD: " + e.getMessage());
                return;
            }
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
        }

        if (!isAjax) {
            request.getRequestDispatcher("/WEB-INF/views/creerMatiere.jsp").forward(request, response);
        }
    }

    public static String afficherMatieres(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            boolean isAdmin = Role.estAdmin(session);
            boolean isProfesseur = Role.estProfesseur(session);
            
            String idSpecStr = request.getParameter("specId");
            if (idSpecStr != null && !idSpecStr.isEmpty()) {
                int idSpec = Integer.parseInt(idSpecStr);
                
                // Récupérer toutes les matières de la spécialité
                java.util.List<Matiere> matieres = model.Matiere.trouverParSpecialite(idSpec);
                
                // Si professeur, filtrer pour ne garder que ses matières
                if (isProfesseur && !isAdmin) {
                    Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
                    if (currentUser != null) {
                        int profId = currentUser.getId();
                        matieres = matieres.stream()
                            .filter(m -> m.getProfId() == profId)
                            .collect(java.util.stream.Collectors.toList());
                        
                        // Si le professeur n'a aucune matière dans cette spécialité, refuser l'accès
                        if (matieres.isEmpty()) {
                            request.setAttribute("error", "Accès refusé : vous n'avez aucune matière dans cette spécialité");
                            return "/WEB-INF/views/error.jsp";
                        }
                    }
                }
                
                request.setAttribute("matieres", matieres);
                request.setAttribute("specialite", model.Specialite.trouverParId(idSpec));
                // Load students for this specialty
                request.setAttribute("students", model.Utilisateur.trouverEtudiantsParSpecialite(idSpec));
            } else {
                request.setAttribute("matieres", model.Matiere.trouverToutes());
                request.setAttribute("specialite", null);
                request.setAttribute("students", new java.util.ArrayList<>());
            }
            
            // Passer le rôle à la JSP pour affichage conditionnel
            request.setAttribute("isAdmin", isAdmin);
            request.setAttribute("isProfesseur", isProfesseur);
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
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                if (isAjax) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre id manquant");
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
                return;
            }

            int id = Integer.parseInt(idStr);
            model.Matiere m = model.Matiere.trouverParId(id);
            int redirectSpecId = (m != null) ? m.getSpecialiteId() : -1;
            if (m != null) {
                if (!m.supprimer()) {
                    throw new SQLException("Suppression de la matière échouée");
                }
            }

            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204 pour fetch
            } else {
                if (redirectSpecId > 0) {
                    response.sendRedirect(request.getContextPath() + "/app/gestion/specialite/details?specId=" + redirectSpecId);
                } else {
                    response.sendRedirect(request.getContextPath() + "/app/admin/specialites");
                }
            }
        } catch (Exception e) {
            if (isAjax) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la suppression : " + e.getMessage());
            } else {
                request.setAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        }
    }

    public static void modifierMatiere(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!Role.estAdmin(request.getSession(false))) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN);
             return;
        }
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            int semestre = Integer.parseInt(request.getParameter("semestre"));
            String profEmail = request.getParameter("profEmail");

            Utilisateur prof = Utilisateur.trouverParemail(profEmail);
            if (prof == null || !"professeur".equalsIgnoreCase(prof.getRole())) {
                if (isAjax) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("text/plain;charset=UTF-8");
                    response.getWriter().write("Le professeur doit exister et avoir le rôle professeur");
                } else {
                    request.setAttribute("error", "Le professeur doit exister et avoir le rôle professeur");
                    request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                }
                return;
            }
            int profId = prof.getId();
            
            model.Matiere m = model.Matiere.trouverParId(id);
            if (m != null) {
                m.setNom(nom);
                m.setSemestre(semestre);
                m.setProfId(profId);
                if (!m.save()) {
                    throw new SQLException("Erreur lors de la modification de la matière");
                }
                if (isAjax) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
            }
            
            if (!isAjax) {
                response.sendRedirect(request.getContextPath() + "/app/gestion/specialite/details?specId=" + m.getSpecialiteId());
            }
        } catch (Exception e) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Erreur lors de la modification : " + e.getMessage());
            } else {
                request.setAttribute("error", "Erreur lors de la modification : " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
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