package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import util.DatabaseManager;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDate dateNaissance;
    private String motDePasse;
    private String role;
    private boolean persisted = false; // Track if the record exists in DB

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
        String sql = "SELECT id, nom, prenom, mail, date_naissance, mot_de_passe, role FROM utilisateur WHERE id = ?";

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
    public static Utilisateur trouverParEmail(String email) throws SQLException {
        String sql = "SELECT id, nom, prenom, mail, date_naissance, mot_de_passe, role FROM utilisateur WHERE mail = ?";

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
    public static Utilisateur trouverParEmailEtMotDePasse(String email, String motDePasse) throws SQLException {
        Utilisateur utilisateur = trouverParEmail(email);
        if (utilisateur != null) {
            if (org.mindrot.jbcrypt.BCrypt.checkpw(motDePasse, utilisateur.motDePasse)) {
                return utilisateur;
            }
        }
        return null;
    }

    /**
     * Vérifier si un email existe
     */
    public static boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE mail = ?";

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
        String sql = "INSERT INTO utilisateur (nom, prenom, mail, date_naissance, mot_de_passe, role) " +
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
        String sql = "UPDATE utilisateur SET nom = ?, prenom = ?, mail = ?, date_naissance = ?, role = ? " +
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
     * Supprimer l'utilisateur
     */
    public boolean supprimer() throws SQLException {
        if (!persisted) {
            return false;
        }

        String sql = "DELETE FROM utilisateur WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                this.persisted = false;
            }
            return success;
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
     * Hydrater un objet depuis un ResultSet
     */
    private static Utilisateur creerDepuisResultSet(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.id = rs.getInt("id");
        utilisateur.nom = rs.getString("nom");
        utilisateur.prenom = rs.getString("prenom");
        utilisateur.email = rs.getString("mail");
        utilisateur.dateNaissance = rs.getDate("date_naissance").toLocalDate();
        utilisateur.motDePasse = rs.getString("mot_de_passe");
        utilisateur.role = rs.getString("role");
        utilisateur.persisted = true;
        return utilisateur;
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

    // ==================== GETTERS & SETTERS ====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}