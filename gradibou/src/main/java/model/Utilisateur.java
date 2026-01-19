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

    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, LocalDate dateNaissance, String motDePasse, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    // Hasher le mot de passe
    private static String hacherMotDePasse(String motDePasse) {
        return org.mindrot.jbcrypt.BCrypt.hashpw(motDePasse, org.mindrot.jbcrypt.BCrypt.gensalt(12));
    }

    // Sauvegarder l'utilisateur en BD
    public boolean save() throws SQLException {
        String sql = "INSERT INTO utilisateur (nom, prenom, mail, date_naissance, mot_de_passe, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, this.nom);
            stmt.setString(2, this.prenom);
            stmt.setString(3, this.email);
            stmt.setDate(4, java.sql.Date.valueOf(this.dateNaissance));
            stmt.setString(5, hacherMotDePasse(this.motDePasse));
            stmt.setString(6, this.role);

            int rowsInserted = stmt.executeUpdate();
            
            if (rowsInserted > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
                return true;
            }
        }
        return false;
    }

    // Vérifier si un email existe
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

    // Trouver un utilisateur par email et mot de passe
    public static Utilisateur trouverParEmailEtMotDePasse(String email, String motDePasse) 
            throws SQLException {
        String sql = "SELECT id, nom, prenom, mail, date_naissance, mot_de_passe, role FROM utilisateur " +
                    "WHERE mail = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String motDePasseHache = rs.getString("mot_de_passe");
                // Vérifier avec Bcrypt
                if (org.mindrot.jbcrypt.BCrypt.checkpw(motDePasse, motDePasseHache)) {
                    Utilisateur utilisateur = new Utilisateur();
                    utilisateur.id = rs.getInt("id");
                    utilisateur.nom = rs.getString("nom");
                    utilisateur.prenom = rs.getString("prenom");
                    utilisateur.email = rs.getString("mail");
                    utilisateur.dateNaissance = rs.getDate("date_naissance").toLocalDate();
                    utilisateur.role = rs.getString("role");
                    return utilisateur;
                }
            }
        }
        return null;
    }

    // Trouver un utilisateur par ID
    public static Utilisateur trouverParId(int id) throws SQLException {
        String sql = "SELECT id, nom, prenom, mail, date_naissance, role FROM utilisateur WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.id = rs.getInt("id");
                utilisateur.nom = rs.getString("nom");
                utilisateur.prenom = rs.getString("prenom");
                utilisateur.email = rs.getString("mail");
                utilisateur.dateNaissance = rs.getDate("date_naissance").toLocalDate();
                utilisateur.role = rs.getString("role");
                return utilisateur;
            }
        }
        return null;
    }

    // Mettre à jour l'utilisateur
    public boolean mettreAJour() throws SQLException {
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
    
    public static Utilisateur creerEnAttente(String nom, String prenom, String email, LocalDate dateNaissance, String role, String ine) throws SQLException {
        String sql = "INSERT INTO utilisateur (nom, prenom, mail, mot_de_passe, role, date_naissance) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, "");
            stmt.setString(5, role);
            stmt.setDate(6, java.sql.Date.valueOf(dateNaissance));

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.id = rs.getInt(1);
                utilisateur.nom = nom;
                utilisateur.prenom = prenom;
                utilisateur.role = role;
                
                // Si étudiant, créer l'entrée dans la table etudiant avec l'INE (id_specialite nullable)
                if ("etudiant".equalsIgnoreCase(role) && ine != null && !ine.isEmpty()) {
                    String sqlEtudiant = "INSERT INTO etudiant (id_utilisateur, ine) VALUES (?, ?)";
                    try (PreparedStatement stmtEtudiant = conn.prepareStatement(sqlEtudiant)) {
                        stmtEtudiant.setInt(1, utilisateur.id);
                        stmtEtudiant.setString(2, ine);
                        stmtEtudiant.executeUpdate();
                    }
                }
                
                return utilisateur;
            }
        }
        return null;
    }

    // Méthode pour compléter le profil
    public boolean completerProfil(String motDePasse) throws SQLException {
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hacherMotDePasse(motDePasse));
            stmt.setInt(2, this.id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Supprimer l'utilisateur
    public boolean supprimer() throws SQLException {
        String sql = "DELETE FROM utilisateur WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Getters et Setters
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