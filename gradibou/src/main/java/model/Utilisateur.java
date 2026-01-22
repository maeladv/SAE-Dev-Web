package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import util.DatabaseManager;

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
     * Trouver les étudiants inscrits dans les spécialités où le professeur enseigne.
     */
    public static List<Utilisateur> trouverEtudiantsParProfesseur(int profId) throws SQLException {
        List<Utilisateur> liste = new ArrayList<>();
        // On récupère les étudiants dont la spécialité correspond à l'une des matières enseignées par le prof.
        // On utilise DISTINCT pour éviter les doublons si le prof enseigne plusieurs matières dans la même spé.
        String sql = "SELECT DISTINCT u.id, u.nom, u.prenom, u.email, u.date_naissance, u.mot_de_passe, u.role " +
                     "FROM utilisateur u " +
                     "JOIN etudiant e ON u.id = e.id_utilisateur " +
                     "JOIN specialite s ON e.id_specialite = s.id " +
                     "JOIN matiere m ON s.id = m.id_specialite " +
                     "WHERE m.id_prof = ? AND u.role = 'etudiant' " +
                     "ORDER BY u.nom, u.prenom";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profId);
            ResultSet rs = stmt.executeQuery();
            
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
}