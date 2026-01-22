package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DatabaseManager;
import util.Json;
import util.Role;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDate dateNaissance;
    private String motDePasse;
    private String role;
    private boolean persisted = false; 


    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, LocalDate dateNaissance, String motDePasse, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.motDePasse = motDePasse;
        this.role = role;
        this.persisted = false;
    }

    // ==================== Méthodes de recherche ====================

    /**
     * Trouver un utilisateur par ID
     */
    public static Utilisateur trouverParId(int id) throws SQLException {
        String sql = "SELECT id, nom, prenom, email, date_naissance, mot_de_passe, role FROM utilisateur WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creerDepuisResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Trouver un utilisateur par email
     */
    public static Utilisateur trouverParemail(String email) throws SQLException {
        String sql = "SELECT id, nom, prenom, email, date_naissance, mot_de_passe, role FROM utilisateur WHERE email = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creerDepuisResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Trouver un utilisateur par email et mot de passe
     */
    public static Utilisateur trouverParemailEtMotDePasse(String email, String motDePasse) throws SQLException {
        Utilisateur utilisateur = trouverParemail(email);
        if (utilisateur != null) {
            if (org.mindrot.jbcrypt.BCrypt.checkpw(motDePasse, utilisateur.motDePasse)) {
                return utilisateur;
            }
        }
        return null;
    }

    /**
     * Trouver tous les professeurs
     */
    public static List<Utilisateur> trouverTousLesProfesseurs() throws SQLException {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, email, date_naissance, mot_de_passe, role FROM utilisateur WHERE role = 'professeur' ORDER BY nom, prenom";

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
     * Trouver tous les étudiants
     */
    public static List<Utilisateur> trouverTousLesEtudiants() throws SQLException {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, email, date_naissance, mot_de_passe, role FROM utilisateur WHERE role = 'etudiant' ORDER BY nom, prenom";

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
     * Trouver tous les utilisateurs (admin uniquement)
     */
    public static List<Utilisateur> trouverTousLesUtilisateurs() throws SQLException {
        
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT id, nom, prenom, email, date_naissance, mot_de_passe, role FROM utilisateur ORDER BY id";

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
     * Vérifier si un email existe
     */
    public static boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Créer un utilisateur en attente (non activé)
     */
    public static Utilisateur creerEnAttente(String nom, String prenom, String email, 
                                            LocalDate dateNaissance, String role, String ine) throws SQLException {
        Utilisateur utilisateur = new Utilisateur(nom, prenom, email, dateNaissance, "", role);
        utilisateur.save();

        // Si étudiant, créer l'entrée dans la table etudiant avec l'INE
        if ("etudiant".equalsIgnoreCase(role) && ine != null && !ine.isEmpty()) {
            String sqlEtudiant = "INSERT INTO etudiant (id_utilisateur, ine) VALUES (?, ?)";
            try (Connection conn = DatabaseManager.obtenirConnexion();
                 PreparedStatement stmtEtudiant = conn.prepareStatement(sqlEtudiant)) {
                stmtEtudiant.setInt(1, utilisateur.id);
                stmtEtudiant.setString(2, ine);
                stmtEtudiant.executeUpdate();
            }
        }

        return utilisateur;
    }

    // ==================== Méthodes de persistence (Active Record) ====================

    /**
     * Sauvegarder l'utilisateur (INSERT ou UPDATE automatique)
     */
    public boolean save() throws SQLException {
        if (persisted) {
            return update();
        } else {
            return insert();
        }
    }

    /**
     * Insérer un nouvel utilisateur en base de données
     */
    private boolean insert() throws SQLException {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, date_naissance, mot_de_passe, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, this.nom);
            stmt.setString(2, this.prenom);
            stmt.setString(3, this.email);
            stmt.setDate(4, java.sql.Date.valueOf(this.dateNaissance));
            stmt.setString(5, this.motDePasse.isEmpty() ? "" : hacherMotDePasse(this.motDePasse));
            stmt.setString(6, this.role);

            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this.id = rs.getInt(1);
                    this.persisted = true;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Mettre à jour un utilisateur existant
     */
    private boolean update() throws SQLException {
        String sql = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, date_naissance = ?, role = ? " +
                     "WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nom);
            stmt.setString(2, this.prenom);
            stmt.setString(3, this.email);
            stmt.setDate(4, java.sql.Date.valueOf(this.dateNaissance));
            stmt.setString(5, this.role);
            stmt.setInt(6, this.id);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Mettre à jour le mot de passe uniquement
     */
    public boolean mettreAJourMotDePasse(String nouveauMotDePasse) throws SQLException {
        if (!persisted) {
            throw new SQLException("Impossible de mettre à jour le mot de passe d'un utilisateur non persisté");
        }

        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hacherMotDePasse(nouveauMotDePasse));
            stmt.setInt(2, this.id);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                this.motDePasse = hacherMotDePasse(nouveauMotDePasse);
            }
            return success;
        }
    }

    /**
     * Compléter le profil (mettre à jour le mot de passe lors de l'activation)
     */
    public boolean completerProfil(String motDePasse) throws SQLException {
        return mettreAJourMotDePasse(motDePasse);
    }

    /**
     * Supprimer l'utilisateur et toutes ses données associées
     */
    public boolean supprimer() throws SQLException {
        if (!persisted) {
            return false;
        }

        try (Connection conn = DatabaseManager.obtenirConnexion()) {
            conn.setAutoCommit(false);
            
            try {
                // Supprimer les liens de réinitialisation
                String deleteLienSql = "DELETE FROM lien WHERE id_utilisateur = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteLienSql)) {
                    stmt.setInt(1, this.id);
                    stmt.executeUpdate();
                }

                // Si c'est un étudiant, supprimer les données d'étudiant
                String deleteNoteSql = "DELETE FROM note WHERE id_etudiant = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteNoteSql)) {
                    stmt.setInt(1, this.id);
                    stmt.executeUpdate();
                }

                String deleteARepondsql = "DELETE FROM a_repondu_evaluation WHERE id_etudiant = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteARepondsql)) {
                    stmt.setInt(1, this.id);
                    stmt.executeUpdate();
                }

                String deleteEtudiantSql = "DELETE FROM etudiant WHERE id_utilisateur = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteEtudiantSql)) {
                    stmt.setInt(1, this.id);
                    stmt.executeUpdate();
                }

                // Si c'est un professeur, réinitialiser les matières
                String updateMatiereSql = "UPDATE matiere SET id_prof = NULL WHERE id_prof = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateMatiereSql)) {
                    stmt.setInt(1, this.id);
                    stmt.executeUpdate();
                }

                // Supprimer l'utilisateur
                String deleteUtilisateurSql = "DELETE FROM utilisateur WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteUtilisateurSql)) {
                    stmt.setInt(1, this.id);
                    boolean success = stmt.executeUpdate() > 0;
                    
                    if (success) {
                        this.persisted = false;
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**
     * Rafraîchir les données de l'utilisateur depuis la base de données
     */
    public boolean recharger() throws SQLException {
        if (!persisted) {
            return false;
        }

        Utilisateur fresh = trouverParId(this.id);
        if (fresh != null) {
            this.nom = fresh.nom;
            this.prenom = fresh.prenom;
            this.email = fresh.email;
            this.dateNaissance = fresh.dateNaissance;
            this.motDePasse = fresh.motDePasse;
            this.role = fresh.role;
            return true;
        }
        return false;
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Creer un objet depuis un ResultSet
     */
    private static Utilisateur creerDepuisResultSet(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.id = rs.getInt("id");
        utilisateur.nom = rs.getString("nom");
        utilisateur.prenom = rs.getString("prenom");
        utilisateur.email = rs.getString("email");
        utilisateur.dateNaissance = rs.getDate("date_naissance").toLocalDate();
        utilisateur.motDePasse = rs.getString("mot_de_passe");
        utilisateur.role = rs.getString("role");
        utilisateur.persisted = true;
        return utilisateur;
    }

    public static Optional<Integer> trouverIdSpecialiteEtudiantParId(int idUtilisateur) throws SQLException {
        String sql = "SELECT e.id_specialite FROM etudiant e " +
                     "JOIN utilisateur u ON e.id_utilisateur = u.id " +
                     "WHERE e.id_utilisateur = ? AND u.role = 'etudiant'";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idSpecValue = rs.getInt("id_specialite");
                return rs.wasNull() ? Optional.empty() : Optional.of(idSpecValue);
            }
        }
        return Optional.empty();
    }

    public static Optional<String> trouverIneEtudiantParId(int idUtilisateur) throws SQLException {
        String sql = "SELECT e.ine FROM etudiant e " +
                     "JOIN utilisateur u ON e.id_utilisateur = u.id " +
                     "WHERE e.id_utilisateur = ? AND u.role = 'etudiant'";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.ofNullable(rs.getString("ine"));
            }
        }
        return Optional.empty();
    }

    /**
     * Hasher un mot de passe avec BCrypt
     */
    private static String hacherMotDePasse(String motDePasse) {
        return org.mindrot.jbcrypt.BCrypt.hashpw(motDePasse, org.mindrot.jbcrypt.BCrypt.gensalt(12));
    }

    /**
     * Vérifier un mot de passe
     */
    public boolean verifierMotDePasse(String motDePassePlain) {
        return org.mindrot.jbcrypt.BCrypt.checkpw(motDePassePlain, this.motDePasse);
    }

    /**
     * Vérifier si l'utilisateur est persisté en base de données
     */
    public boolean estPersiste() {
        return persisted;
    }

    // ================ Méthodes pour le controllers ================

    public static void creationUtilisateurParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        if (!Role.estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String dateNaissance = request.getParameter("dateNaissance");
        String role = request.getParameter("role");
        String ine = request.getParameter("ine");

        if (nom == null || nom.isEmpty() || prenom == null || prenom.isEmpty() || 
            email == null || email.isEmpty() || dateNaissance == null || dateNaissance.isEmpty() ||
            role == null || role.isEmpty()) {
            Json.envoyerJsonError(response, "Nom, prénom, email, date de naissance et rôle requis", 400);
            return;
        }

        if ("etudiant".equalsIgnoreCase(role) && (ine == null || ine.isEmpty())) {
            Json.envoyerJsonError(response, "L'INE est obligatoire pour les étudiants", 400);
            return;
        }

        try {
            if (Utilisateur.emailExiste(email)) {
                Json.envoyerJsonError(response, "Cet email est déjà utilisé", 409);
                return;
            }

            Utilisateur newUser = Utilisateur.creerEnAttente(nom, prenom, email, LocalDate.parse(dateNaissance), role, ine);
            if (newUser != null) {
                // Créer le lien directement au lieu d'appeler creerLienPourMAJMotDePasse
                String token = model.Lien.creerLien(newUser.getId(), 7); // 7 jours pour activation
                String lienActivation = request.getContextPath() + "/app/complete-profil?token=" + token;
                
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write(String.format(
                    "{\"success\": true, \"message\": \"Utilisateur créé avec succès\", \"lien\": \"%s\", \"utilisateur\": {\"id\": %d, \"email\": \"%s\"}}", 
                    lienActivation, newUser.getId(), email
                ));
            } else {
                Json.envoyerJsonError(response, "Erreur lors de la création de l'utilisateur", 500);
            }
        } catch (java.time.format.DateTimeParseException e) {
            Json.envoyerJsonError(response, "Format de date invalide (utiliser YYYY-MM-DD)", 400);
        } catch (Exception e) {
            Json.envoyerJsonError(response, "Erreur: " + e.getMessage(), 500);
        }
    }

    public static void completerProfil(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String token = request.getParameter("token");
        String motDePasse = request.getParameter("motDePasse");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || token.isEmpty()) {
            request.setAttribute("error", "Token manquant");
            request.getRequestDispatcher("/WEB-INF/views/indedx.jsp").forward(request, response);
            return;
        }

        try {
            int userId = model.Lien.validerLien(token);
            if (userId == -1) {
                request.setAttribute("error", "Lien invalide ou expiré");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }

            if (motDePasse == null || motDePasse.isEmpty()) {
                request.setAttribute("error", "Le mot de passe est obligatoire");
                request.setAttribute("token", token);
                request.setAttribute("userId", userId);
                request.getRequestDispatcher("/WEB-INF/views/complete-profil.jsp").forward(request, response);
                return;
            }

            if (!motDePasse.equals(confirmPassword)) {
                request.setAttribute("error", "Les mots de passe ne correspondent pas");
                request.setAttribute("token", token);
                request.setAttribute("userId", userId);
                request.getRequestDispatcher("/WEB-INF/views/complete-profil.jsp").forward(request, response);
                return;
            }

            Utilisateur utilisateur = Utilisateur.trouverParId(userId);
            if (utilisateur != null) {
                if (utilisateur.completerProfil(motDePasse)) {
                    model.Lien.marquerCommeUtilise(token);
                    request.setAttribute("success", "Profil complété avec succès ! Vous pouvez maintenant vous connecter.");
                    request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Erreur lors de la mise à jour du profil");
                    request.setAttribute("token", token);
                    request.setAttribute("userId", userId);
                    request.getRequestDispatcher("/WEB-INF/views/complete-profil.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    // ==================== GETTERS & SETTERS ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getemail() { return email; }
    public void setemail(String email) { this.email = email; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Fonction pour recuperer la specialite d'un etudiant
    public int getIdSpecialite() throws SQLException {
        return trouverIdSpecialiteEtudiantParId(this.id)
            .orElse(-1);
    }

    public String getIne() throws SQLException {
        return trouverIneEtudiantParId(this.id)
            .orElse("");
    }

    public String getSpecialiteTag() throws SQLException {
        String sql = "SELECT s.tag FROM specialite s " +
                     "JOIN etudiant e ON e.id_specialite = s.id " +
                     "JOIN utilisateur u ON e.id_utilisateur = u.id " +
                     "WHERE e.id_utilisateur = ? AND u.role = 'etudiant'";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String tag = rs.getString("tag");
                return tag == null ? "" : tag;
            }
        }
        return "";
    }

    // ==================== Méthodes statiques pour les actions admin ====================

    public static void modifierUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!util.Role.estAdmin(request.getSession(false))) {
            util.Json.envoyerJsonError(response, "Accès refusé", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String email = request.getParameter("email");
            String role = request.getParameter("role");

            if (nom == null || nom.isEmpty() || prenom == null || prenom.isEmpty() ||
                email == null || email.isEmpty() || role == null || role.isEmpty()) {
                util.Json.envoyerJsonError(response, "Tous les champs obligatoires doivent être remplis", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Utilisateur user = Utilisateur.trouverParId(id);
            if (user != null) {
                user.setNom(nom);
                user.setPrenom(prenom);
                user.setemail(email);
                user.setRole(role);
                user.save();
                util.Json.envoyerJsonSuccess(response, "Utilisateur modifié avec succès", request.getContextPath() + "/app/admin");
            } else {
                util.Json.envoyerJsonError(response, "Utilisateur non trouvé", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            util.Json.envoyerJsonError(response, "Erreur : " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public static void supprimerUtilisateur(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!util.Role.estAdmin(request.getSession(false))) {
            util.Json.envoyerJsonError(response, "Accès refusé", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.isEmpty()) {
                util.Json.envoyerJsonError(response, "ID utilisateur requis", HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            int id = Integer.parseInt(idStr);
            Utilisateur user = Utilisateur.trouverParId(id);
            if (user != null) {
                user.supprimer();
                util.Json.envoyerJsonSuccess(response, "Utilisateur supprimé avec succès", request.getContextPath() + "/app/admin");
            } else {
                util.Json.envoyerJsonError(response, "Utilisateur non trouvé", HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            util.Json.envoyerJsonError(response, "ID utilisateur invalide", HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            util.Json.envoyerJsonError(response, "Erreur base de données", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            util.Json.envoyerJsonError(response, "Erreur : " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}