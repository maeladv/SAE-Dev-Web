package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DatabaseManager;

public class Matiere {
    private int id;
    private String nom;
    private int semestre;
    private int specialiteId;
    private int profId;
    private String specialiteTag; // Champ transient pour l'affichage
    private double moyenne = -1.0; // Champ transient pour l'affichage de la moyenne pondérée
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
     * Trouver tous les professeurs
     */
    public static List<Matiere> trouverToutes() throws SQLException {
        List<Matiere> liste = new ArrayList<>();
        String sql = "SELECT id, nom, semestre, id_specialite, id_prof FROM matiere ORDER BY nom";

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                liste.add(creerDepuisResultSet(rs));
            }
        }
        return liste;
    }

    /** avec leur moyenne pondérée globale
     * Moyenne Matière = Somme(Moyenne Examen * Coeff Examen) / Somme(Coeff Examen)
     */
    public static List<Matiere> trouverParProfId(int profId) throws SQLException {
        List<Matiere> liste = new ArrayList<>();
        // Requête complexe pour calculer la moyenne pondérée par matière
        String sql = "SELECT m.id, m.nom, m.semestre, m.id_specialite, m.id_prof, s.tag as specialite_tag, " +
                     "COALESCE(SUM(CASE WHEN exam_stats.avg_note IS NOT NULL THEN e.coefficient ELSE 0 END), 0) as total_coeff, " +
                     "COALESCE(SUM(CASE WHEN exam_stats.avg_note IS NOT NULL THEN exam_stats.avg_note * e.coefficient ELSE 0 END), 0) as weighted_sum " +
                     "FROM matiere m " +
                     "LEFT JOIN specialite s ON m.id_specialite = s.id " +
                     "LEFT JOIN examen e ON m.id = e.id_matiere " +
                     "LEFT JOIN ( " +
                     "    SELECT id_examen, AVG(note) as avg_note " +
                     "    FROM note " +
                     "    GROUP BY id_examen " +
                     ") exam_stats ON e.id = exam_stats.id_examen " +
                     "WHERE m.id_prof = ? " +
                     "GROUP BY m.id, m.nom, m.semestre, m.id_specialite, m.id_prof, s.tag " +
                     "ORDER BY m.nom";

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Matiere m = new Matiere();
                m.id = rs.getInt("id");
                m.nom = rs.getString("nom");
                m.semestre = rs.getInt("semestre");
                m.specialiteId = rs.getInt("id_specialite");
                m.profId = rs.getInt("id_prof");
                m.persisted = true;
                
                try {
                     m.setSpecialiteTag(rs.getString("specialite_tag"));
                } catch (SQLException e) {
                    // Ignorer
                }
                
                // Calcul de la moyenne
                double totalCoeff = rs.getDouble("total_coeff");
                double weightedSum = rs.getDouble("weighted_sum");
                
                if (totalCoeff > 0) {
                    m.setMoyenne(weightedSum / totalCoeff);
                } else {
                    m.setMoyenne(-1.0);
                }

                liste.add(m);
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

        try (Connection conn = DatabaseManager.obtenirConnexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, specialiteId);
            ResultSet rs = stmt.executeQuery();
            
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
        String sql = "INSERT INTO matiere (nom, semestre, id_specialite, id_prof) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

     //Mettre à jour une spécialité existante
    private boolean update() throws SQLException {
        String sql = "UPDATE matiere SET nom = ?, semestre = ?, id_specialite = ?, id_prof = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nom);
            stmt.setInt(2, this.semestre);
            stmt.setInt(3, this.specialiteId);
            stmt.setInt(4, this.profId);
            stmt.setInt(5, this.id);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Supprimer la spécialité
     */   
    public boolean supprimer() throws SQLException {
        if (!persisted) {
            return false;
        }

        String sql = "DELETE FROM matiere WHERE id = ?";

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
     * Hydrater un objet depuis un ResultSet
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
    public String getSpecialiteTag() {
        return specialiteTag;
    }
    public void setSpecialiteTag(String specialiteTag) {
        this.specialiteTag = specialiteTag;
    }
    public double getMoyenne() {
        return moyenne;
    }
    public void setMoyenne(double moyenne) {
        this.moyenne = moyenne;
    }
}