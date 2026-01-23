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
     * Trouver tous les étudiants d'une spécialité
     */
    public static List<Utilisateur> trouverEtudiantsParSpecialite(int idSpecialite) throws SQLException {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT u.id, u.nom, u.prenom, u.email, u.date_naissance, u.mot_de_passe, u.role " +
                     "FROM utilisateur u " +
                     "JOIN etudiant e ON u.id = e.id_utilisateur " +
                     "WHERE e.id_specialite = ? " +
                     "ORDER BY u.nom, u.prenom";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSpecialite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    liste.add(creerDepuisResultSet(rs));
                }
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
     * Mettre à jour un utilisateur avec gestion du changement de rôle
     */
    public boolean updateWithRoleChange(String ancienRole) throws SQLException {
        if (!persisted) {
            return false;
        }

        try (Connection conn = DatabaseManager.obtenirConnexion()) {
            conn.setAutoCommit(false);
            
            try {
                // Si le rôle passe de étudiant à autre chose, nettoyer les tables liées
                if ("etudiant".equalsIgnoreCase(ancienRole) && !"etudiant".equalsIgnoreCase(this.role)) {
                    // Supprimer les notes
                    String deleteNoteSql = "DELETE FROM note WHERE id_etudiant = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteNoteSql)) {
                        stmt.setInt(1, this.id);
                        stmt.executeUpdate();
                    }

                    // Supprimer les réponses d'évaluation
                    String deleteARepondsql = "DELETE FROM a_repondu_evaluation WHERE id_etudiant = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteARepondsql)) {
                        stmt.setInt(1, this.id);
                        stmt.executeUpdate();
                    }

                    // Supprimer l'enregistrement étudiant
                    String deleteEtudiantSql = "DELETE FROM etudiant WHERE id_utilisateur = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteEtudiantSql)) {
                        stmt.setInt(1, this.id);
                        stmt.executeUpdate();
                    }
                }

                // Si le rôle passe à étudiant, ajouter à la table étudiant
                if (!"etudiant".equalsIgnoreCase(ancienRole) && "etudiant".equalsIgnoreCase(this.role)) {
                    // Vérifier que l'enregistrement n'existe pas déjà
                    String checkEtudiantSql = "SELECT id_utilisateur FROM etudiant WHERE id_utilisateur = ?";
                    boolean etudiantExists = false;
                    try (PreparedStatement stmt = conn.prepareStatement(checkEtudiantSql)) {
                        stmt.setInt(1, this.id);
                        try (java.sql.ResultSet rs = stmt.executeQuery()) {
                            etudiantExists = rs.next();
                        }
                    }

                    // Ajouter si n'existe pas
                    if (!etudiantExists) {
                        String insertEtudiantSql = "INSERT INTO etudiant (id_utilisateur, ine, id_specialite) VALUES (?, ?, NULL)";
                        try (PreparedStatement stmt = conn.prepareStatement(insertEtudiantSql)) {
                            stmt.setInt(1, this.id);
                            stmt.setString(2, ""); // INE vide par défaut
                            stmt.executeUpdate();
                        }
                    }
                }

                // Si le rôle passe de professeur à autre chose, réinitialiser les matières
                if ("professeur".equalsIgnoreCase(ancienRole) && !"professeur".equalsIgnoreCase(this.role)) {
                    String updateMatiereSql = "UPDATE matiere SET id_prof = NULL WHERE id_prof = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateMatiereSql)) {
                        stmt.setInt(1, this.id);
                        stmt.executeUpdate();
                    }
                }

                // Mettre à jour l'utilisateur
                String updateUtilisateurSql = "UPDATE utilisateur SET nom = ?, prenom = ?, email = ?, date_naissance = ?, role = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateUtilisateurSql)) {
                    stmt.setString(1, this.nom);
                    stmt.setString(2, this.prenom);
                    stmt.setString(3, this.email);
                    stmt.setDate(4, java.sql.Date.valueOf(this.dateNaissance));
                    stmt.setString(5, this.role);
                    stmt.setInt(6, this.id);

                    boolean success = stmt.executeUpdate() > 0;
                    if (success) {
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
            int idUtilisateur = model.Lien.validerLien(token);
            if (idUtilisateur == -1) {
                request.setAttribute("error", "Lien invalide ou expiré");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }

            if (motDePasse == null || motDePasse.isEmpty()) {
                request.setAttribute("error", "Le mot de passe est obligatoire");
                request.setAttribute("token", token);
                request.setAttribute("idUtilisateur", idUtilisateur);
                request.getRequestDispatcher("/WEB-INF/views/complete-profil.jsp").forward(request, response);
                return;
            }

            if (!motDePasse.equals(confirmPassword)) {
                request.setAttribute("error", "Les mots de passe ne correspondent pas");
                request.setAttribute("token", token);
                request.setAttribute("idUtilisateur", idUtilisateur);
                request.getRequestDispatcher("/WEB-INF/views/complete-profil.jsp").forward(request, response);
                return;
            }

            Utilisateur utilisateur = Utilisateur.trouverParId(idUtilisateur);
            if (utilisateur != null) {
                if (utilisateur.completerProfil(motDePasse)) {
                    model.Lien.marquerCommeUtilise(token);
                    request.setAttribute("success", "Profil complété avec succès ! Vous pouvez maintenant vous connecter.");
                    request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Erreur lors de la mise à jour du profil");
                    request.setAttribute("token", token);
                    request.setAttribute("idUtilisateur", idUtilisateur);
                    request.getRequestDispatcher("/WEB-INF/views/complete-profil.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Modifier son propre profil (utilisateur connecté)
     */
    public static void modifierMonProfil(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        Utilisateur currentUser = (Utilisateur) request.getSession().getAttribute("utilisateur");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/app/login");
            return;
        }

        // Déterminer quel utilisateur modifier
        Utilisateur targetUser = currentUser;
        String idUtilisateurParam = request.getParameter("idUtilisateur");
        boolean isAdminModifyingOther = false;
        
        if (idUtilisateurParam != null && !idUtilisateurParam.isEmpty()) {
            // Vérifier que l'utilisateur courant est admin
            if (util.Role.estAdmin(request.getSession(false))) {
                int targetidUtilisateur = Integer.parseInt(idUtilisateurParam);
                targetUser = Utilisateur.trouverParId(targetidUtilisateur);
                if (targetUser == null) {
                    request.setAttribute("error", "Utilisateur introuvable");
                    request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
                    return;
                }
                isAdminModifyingOther = true;
            }
        }

        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String dateNaissanceStr = request.getParameter("dateNaissance");
        String motDePasse = request.getParameter("motDePasse");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // Validation des champs obligatoires
            if (nom == null || nom.trim().isEmpty() || prenom == null || prenom.trim().isEmpty() ||
                email == null || email.trim().isEmpty() || dateNaissanceStr == null || dateNaissanceStr.isEmpty()) {
                request.setAttribute("error", "Tous les champs requis doivent être remplis");
                request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
                return;
            }

            // Validation du mot de passe si fourni
            if (motDePasse != null && !motDePasse.trim().isEmpty()) {
                if (motDePasse.length() < 6) {
                    request.setAttribute("error", "Le mot de passe doit contenir au moins 6 caractères");
                    request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
                    return;
                }
                
                if (confirmPassword == null || !motDePasse.equals(confirmPassword)) {
                    request.setAttribute("error", "Les mots de passe ne correspondent pas");
                    request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
                    return;
                }
            }

            // Vérifier que l'email n'existe pas ailleurs (sauf pour l'utilisateur cible)
            Utilisateur userWithEmail = Utilisateur.trouverParemail(email);
            if (userWithEmail != null && userWithEmail.getId() != targetUser.getId()) {
                request.setAttribute("error", "Cet email est déjà utilisé");
                if (isAdminModifyingOther) {
                    request.setAttribute("utilisateurVu", targetUser);
                }
                request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
                return;
            }

            // Mettre à jour les informations
            targetUser.setNom(nom.trim());
            targetUser.setPrenom(prenom.trim());
            targetUser.setemail(email.trim());
            targetUser.setDateNaissance(java.time.LocalDate.parse(dateNaissanceStr));
            
            // Mettre à jour le mot de passe si fourni et si on modifie son propre profil
            if (!isAdminModifyingOther && motDePasse != null && !motDePasse.trim().isEmpty()) {
                targetUser.mettreAJourMotDePasse(motDePasse);
            }
            
            // Sauvegarder
            targetUser.save();
            
            // Mettre à jour la session si on modifie son propre profil
            if (!isAdminModifyingOther) {
                request.getSession().setAttribute("utilisateur", targetUser);
            }
            
            request.setAttribute("success", "Profil mis à jour avec succès !");
            if (isAdminModifyingOther) {
                request.setAttribute("utilisateurVu", targetUser);
            }
            request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
            
        } catch (java.time.format.DateTimeParseException e) {
            request.setAttribute("error", "Format de date invalide");
            if (isAdminModifyingOther) {
                request.setAttribute("utilisateurVu", targetUser);
            }
            request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur base de données: " + e.getMessage());
            if (isAdminModifyingOther) {
                request.setAttribute("utilisateurVu", targetUser);
            }
            request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Erreur: " + e.getMessage());
            if (isAdminModifyingOther) {
                request.setAttribute("utilisateurVu", targetUser);
            }
            request.getRequestDispatcher("/WEB-INF/views/moncompte.jsp").forward(request, response);
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
        return trouverIneEtudiantParId(this.id).orElse("");
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

            Utilisateur utilisateur = Utilisateur.trouverParId(id);
            if (utilisateur != null) {
                String ancienRole = utilisateur.getRole();
                
                utilisateur.setNom(nom);
                utilisateur.setPrenom(prenom);
                utilisateur.setemail(email);
                utilisateur.setRole(role);
                
                // Si le rôle a changé, utiliser updateWithRoleChange()
                if (!ancienRole.equalsIgnoreCase(role)) {
                    utilisateur.updateWithRoleChange(ancienRole);
                } else {
                    utilisateur.save();
                }
                
                util.Json.envoyerJsonSuccess(response, "Utilisateur modifié avec succès", request.getContextPath() + "/app/admin");
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
            Utilisateur utilisateur = Utilisateur.trouverParId(id);
            if (utilisateur != null) {
                utilisateur.supprimer();
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

    public static void retirerEtudiantDeSpecialite(String email, int idspecialite) throws SQLException {
        Utilisateur etudiant = Utilisateur.trouverParemail(email);
        if (etudiant == null) {
            throw new SQLException("Étudiant non trouvé");
        }
        
        Connection conn = DatabaseManager.obtenirConnexion();
        try {
            int idEtudiant = etudiant.id;
            
            // 1. Supprimer les réponses d'évaluation pour cette spécialité
            String deleteReponsesSql = "DELETE FROM reponse_evaluation WHERE id_matiere IN " +
                    "(SELECT id FROM matiere WHERE id_specialite = ?) " +
                    "AND id IN (SELECT id FROM reponse_evaluation WHERE id_evaluation IN " +
                    "(SELECT id_evaluation FROM a_repondu_evaluation WHERE id_etudiant = ? AND id_matiere IN " +
                    "(SELECT id FROM matiere WHERE id_specialite = ?)))";
            try (PreparedStatement stmt = conn.prepareStatement(deleteReponsesSql)) {
                stmt.setInt(1, idspecialite);
                stmt.setInt(2, idEtudiant);
                stmt.setInt(3, idspecialite);
                stmt.executeUpdate();
            }
            
            // 2. Supprimer les a_repondu_evaluation pour cette spécialité
            String deleteAReponduSql = "DELETE FROM a_repondu_evaluation WHERE id_etudiant = ? AND id_matiere IN " +
                    "(SELECT id FROM matiere WHERE id_specialite = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(deleteAReponduSql)) {
                stmt.setInt(1, idEtudiant);
                stmt.setInt(2, idspecialite);
                stmt.executeUpdate();
            }
            
            // 3. Supprimer les notes des examens des matières de la spécialité
            String deleteNotesSql = "DELETE FROM note WHERE id_etudiant = ? AND id_examen IN " +
                    "(SELECT id FROM examen WHERE id_matiere IN " +
                    "(SELECT id FROM matiere WHERE id_specialite = ?))";
            try (PreparedStatement stmt = conn.prepareStatement(deleteNotesSql)) {
                stmt.setInt(1, idEtudiant);
                stmt.setInt(2, idspecialite);
                stmt.executeUpdate();
            }
            
            // 4. Mettre à jour l'association étudiant-spécialité (mettre à NULL)
            String updateEtudiantSql = "UPDATE etudiant SET id_specialite = NULL WHERE id_utilisateur = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateEtudiantSql)) {
                stmt.setInt(1, idEtudiant);
                stmt.executeUpdate();
            }
        } finally {
            // La connexion sera fermée par le pool
        }
    }

    public static void retirerEtudiantSpecialite(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!util.Role.estAdmin(request.getSession(false))) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Accès refusé");
            return;
        }

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        
        try {
            String email = request.getParameter("email");
            String idspecialiteStr = request.getParameter("idspecialite");
            
            if (email == null || email.isEmpty() || idspecialiteStr == null || idspecialiteStr.isEmpty()) {
                if (isAjax) {
                    response.setContentType("text/plain");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("Email et ID spécialité requis");
                } else {
                    response.sendRedirect(request.getContextPath() + "/app/admin");
                }
                return;
            }
            
            int idspecialite = Integer.parseInt(idspecialiteStr);
            retirerEtudiantDeSpecialite(email, idspecialite);
            
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.sendRedirect(request.getContextPath() + "/app/gestion/specialite/details?specId=" + idspecialite);
            }
        } catch (NumberFormatException e) {
            if (isAjax) {
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("ID spécialité invalide");
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin");
            }
        } catch (SQLException e) {
            if (isAjax) {
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Erreur lors de la suppression de l'étudiant : " + e.getMessage());
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin");
            }
        }
    }

    public static void ajouterEtudiantSpecialite(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!util.Role.estAdmin(request.getSession(false))) {
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("Accès refusé");
            return;
        }

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        
        try {
            String email = request.getParameter("email");
            String idspecialiteStr = request.getParameter("idspecialite");
            
            if (email == null || email.isEmpty() || idspecialiteStr == null || idspecialiteStr.isEmpty()) {
                if (isAjax) {
                    response.setContentType("text/plain");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("Email et ID spécialité requis");
                } else {
                    response.sendRedirect(request.getContextPath() + "/app/admin");
                }
                return;
            }
            
            int idspecialite = Integer.parseInt(idspecialiteStr);
            Utilisateur utilisateur = Utilisateur.trouverParemail(email);
            
            if (utilisateur == null) {
                if (isAjax) {
                    response.setContentType("text/plain");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("Utilisateur non trouvé");
                } else {
                    response.sendRedirect(request.getContextPath() + "/app/gestion/specialite/details?specId=" + idspecialite);
                }
                return;
            }
            
            if (!utilisateur.getRole().equalsIgnoreCase("etudiant")) {
                if (isAjax) {
                    response.setContentType("text/plain");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("L'utilisateur n'est pas un étudiant");
                } else {
                    response.sendRedirect(request.getContextPath() + "/app/gestion/specialite/details?specId=" + idspecialite);
                }
                return;
            }
            
            String sql = "UPDATE etudiant SET id_specialite = ? WHERE id_utilisateur = ?";
            try (Connection conn = DatabaseManager.obtenirConnexion();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idspecialite);
                stmt.setInt(2, utilisateur.id);
                stmt.executeUpdate();
            }
            
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.sendRedirect(request.getContextPath() + "/app/gestion/specialite/details?specId=" + idspecialite);
            }
        } catch (NumberFormatException e) {
            if (isAjax) {
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("ID spécialité invalide");
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin");
            }
        } catch (SQLException e) {
            if (isAjax) {
                response.setContentType("text/plain");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Erreur lors de l'ajout de l'étudiant : " + e.getMessage());
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin");
            }
        }
    }
}