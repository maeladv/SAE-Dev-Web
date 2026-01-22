package model;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DatabaseManager;
import util.Json;

public class Lien {
    private int id;
    private String tokenHash;
    private LocalDate dateUtilisation;
    private LocalDate dateExpiration;
    private int idUtilisateur;

    public Lien() {}

    public Lien(String tokenHash, LocalDate dateExpiration, int idUtilisateur) {
        this.tokenHash = tokenHash;
        this.dateExpiration = dateExpiration;
        this.idUtilisateur = idUtilisateur;
    }

    // Générer un token aléatoire sécurisé
    public static String genererToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    // Hasher le token
    private static String hashToken(String token) throws java.security.NoSuchAlgorithmException {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(token.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    // Créer et sauvegarder un nouveau lien
    public static String creerLien(int idUtilisateur, int joursValidite) throws SQLException, java.security.NoSuchAlgorithmException {
        String token = genererToken();
        String tokenHash = hashToken(token);
        LocalDate expiration = LocalDate.now().plusDays(joursValidite);

        String sql = "INSERT INTO lien (token_hash, date_expiration, id_utilisateur) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tokenHash);
            stmt.setDate(2, java.sql.Date.valueOf(expiration));
            stmt.setInt(3, idUtilisateur);
            stmt.executeUpdate();
            return token;
        }
    }

    // Vérifier si le lien est valide et retourner l'ID utilisateur
    public static int validerLien(String token) throws SQLException, java.security.NoSuchAlgorithmException {
        String tokenHash = hashToken(token);
        String sql = "SELECT id_utilisateur, date_expiration FROM lien " +
                     "WHERE token_hash = ? AND date_utilisation IS NULL";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tokenHash);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDate expiration = rs.getDate("date_expiration").toLocalDate();
                if (LocalDate.now().isBefore(expiration) || LocalDate.now().isEqual(expiration)) {
                    return rs.getInt("id_utilisateur");
                }
            }
        }
        return -1;
    }

    // Marquer le lien comme utilisé
    public static void marquerCommeUtilise(String token) throws SQLException, java.security.NoSuchAlgorithmException {
        String tokenHash = hashToken(token);
        String sql = "UPDATE lien SET date_utilisation = ? WHERE token_hash = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setString(2, tokenHash);
            stmt.executeUpdate();
        }
    }

    // ================ Méthodes pour le controllers ================

    public static void creerLienPourMAJMotDePasse(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {

        String emailUtilisateur = request.getParameter("email");
        if (emailUtilisateur == null || emailUtilisateur.isEmpty()) {
            Json.envoyerJsonError(response, "email utilisateur requis", 400);
            return;
        }

        Utilisateur utilisateur = Utilisateur.trouverParemail(emailUtilisateur);
        if (utilisateur == null) {
            Json.envoyerJsonError(response, "Utilisateur non trouvé", 404);
            return;
        }

        try {
            int userId = utilisateur.getId();

            String token = model.Lien.creerLien(userId, 1); // Lien valide 1 jour
            String lienMDP = request.getContextPath() + "/app/complete-profil?token=" + token;

            Json.envoyerJsonSuccess(response, "Lien créé avec succès", lienMDP);
            // PAS SAFE, EN ATTENTE DE MISE EN PLACE DE CONNEXION SMTP
        } catch (NumberFormatException e) {
            Json.envoyerJsonError(response, "ID utilisateur invalide", 400);
        } catch (Exception e) {
            Json.envoyerJsonError(response, "Erreur: " + e.getMessage(), 500);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public LocalDate getDateUtilisation() { return dateUtilisation; }
    public void setDateUtilisation(LocalDate dateUtilisation) { this.dateUtilisation = dateUtilisation; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }
}