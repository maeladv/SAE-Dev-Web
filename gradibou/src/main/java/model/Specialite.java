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
import util.DatabaseManager;
import util.Role;

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
     * Trouver toutes les spécialités
     */
    public static List<Specialite> trouverToutes() throws SQLException {
        List<Specialite> liste = new ArrayList<>();
        String sql = "SELECT id, tag, annee, nom FROM specialite ORDER BY nom";

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
     * Trouver les spécialités où un professeur enseigne
     */
    public static List<Specialite> trouverParProfesseur(int idProf) throws SQLException {
        List<Specialite> liste = new ArrayList<>();
        String sql = "SELECT DISTINCT s.id, s.tag, s.annee, s.nom " +
                     "FROM specialite s " +
                     "JOIN matiere m ON m.id_specialite = s.id " +
                     "WHERE m.id_prof = ? " +
                     "ORDER BY s.nom";

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProf);
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
     * Insérer une nouvelle spécialité en base de données
     */
    private boolean insert() throws SQLException {
        // Ici, le tag est fourni par l'objet et non généré par la BDD
        String sql = "INSERT INTO specialite (tag, annee, nom) VALUES (?, ?, ?)";

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.tag);
            stmt.setInt(2, this.annee);
            stmt.setString(3, this.nom);
            stmt.setInt(4, this.id);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer la spécialité (avec suppression en cascade)
     * - Supprime toutes les matières de la spécialité (qui supprimeront leurs examens/notes/évaluations)
     * - Met à jour les étudiants ayant cette spécialité (id_specialite -> NULL)
     */
    public boolean supprimer() throws SQLException {
        if (!persisted) {
            return false;
        }

        Connection conn = DatabaseManager.obtenirConnexion();
        // 1. Supprimer toutes les matières de cette spécialité (cascade vers examens, notes, évaluations)
        List<Matiere> matieres = Matiere.trouverParSpecialite(this.id);
        for (Matiere m : matieres) {
            m.supprimer(conn);
        }
        
        // 2. Mettre à jour les étudiants ayant cette spécialité
        String updateEtudiantsSql = "UPDATE etudiant SET id_specialite = NULL WHERE id_specialite = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateEtudiantsSql)) {
            stmt.setInt(1, this.id);
            stmt.executeUpdate();
        }
        
        // 3. Supprimer la spécialité
        String sql = "DELETE FROM specialite WHERE id = ?";
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
     * Creer un objet depuis un ResultSet
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

    // ================ Méthodes pour le controllers ================

    public static void creationSpecialiteParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        if (!Role.estAdmin(request.getSession(false))) {
            util.Json.envoyerJsonError(response, "Accès refusé", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String tag = request.getParameter("tag");
        String anneeStr = request.getParameter("annee");
        String nom = request.getParameter("nom");

        if (tag == null || tag.isEmpty() || anneeStr == null || anneeStr.isEmpty() || nom == null || nom.isEmpty()) {
            util.Json.envoyerJsonError(response, "Tous les champs sont requis", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int annee = Integer.parseInt(anneeStr);
            model.Specialite spec = new model.Specialite(tag, annee, nom);
            
            if (spec.save()) {
                util.Json.envoyerJsonSuccess(response, "Spécialité créée avec succès", request.getContextPath() + "/app/gestion/specialites");
            } else {
                util.Json.envoyerJsonError(response, "Erreur lors de la création de la spécialité", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            util.Json.envoyerJsonError(response, "L'année doit être un nombre valide", HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            util.Json.envoyerJsonError(response, "Erreur BD: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public static String afficherSpecialites(HttpServletRequest request) {
        try {
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            boolean isAdmin = Role.estAdmin(session);
            boolean isProfesseur = Role.estProfesseur(session);
            
            // Récupérer les spécialités selon le rôle
            if (isAdmin) {
                request.setAttribute("specialites", model.Specialite.trouverToutes());
                request.setAttribute("userRole", "admin");
            } else if (isProfesseur) {
                Utilisateur prof = (Utilisateur) session.getAttribute("utilisateur");
                if (prof != null) {
                    request.setAttribute("specialites", model.Specialite.trouverParProfesseur(prof.getId()));
                    request.setAttribute("userRole", "professeur");
                }
            }
            
            return "/WEB-INF/views/listeSpecialites.jsp";
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur lors du chargement des spécialités : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
        }
    }

    public static void supprimerSpecialite(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!Role.estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            model.Specialite s = model.Specialite.trouverParId(id);
            if (s != null) {
                s.supprimer();
                util.Json.envoyerJsonSuccess(response, "Spécialité supprimée avec succès", "");
            } else {
                util.Json.envoyerJsonError(response, "Spécialité non trouvée", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            util.Json.envoyerJsonError(response, "Erreur lors de la suppression : " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public static void modifierSpecialite(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!Role.estAdmin(request.getSession(false))) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN);
             return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            
            if (nom == null || nom.isEmpty()) {
                util.Json.envoyerJsonError(response, "Le nom est requis", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            model.Specialite s = model.Specialite.trouverParId(id);
            if (s != null) {
                s.setNom(nom);
                // Note: Le tag et l'année ne sont pas modifiables
                s.save();
                util.Json.envoyerJsonSuccess(response, "Spécialité modifiée avec succès", request.getContextPath() + "/app/gestion/specialites");
            } else {
                util.Json.envoyerJsonError(response, "Spécialité non trouvée", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            util.Json.envoyerJsonError(response, "Erreur lors de la modification : " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
        
        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
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
        
        Connection conn = DatabaseManager.obtenirConnexion();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
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