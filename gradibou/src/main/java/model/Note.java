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

public class Note {
    private int id_etudiant;
    private int id_examen;
    private int valeur;
    private LocalDate date;
    private boolean persisted = false;

    // Constructeurs
    public Note() {}

    public Note(int valeur, int id_examen, int id_etudiant) {
        this.valeur = valeur;
        this.id_examen = id_examen;
        this.id_etudiant = id_etudiant;
    }

    // ==================== Méthodes de recherche ====================

    /**
     * Trouver une note par ID
     */
    public static Note trouverParIdEtudiantExamen(int id_etudiant, int id_examen) throws SQLException {
        String sql = "SELECT id_etudiant, id_examen, note, date FROM note WHERE id_etudiant = ? AND id_examen = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_etudiant);
            stmt.setInt(2, id_examen);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return creerDepuisResultSet(rs);
            }
        }
        return null;
    }

    /**
     * Trouver toutes les notes
     */
    public static List<Note> trouverToutes() throws SQLException {
        List<Note> liste = new ArrayList<>();
        String sql = "SELECT id_etudiant, id_examen, note, date FROM note ORDER BY date";
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
     * Trouver les notes d'un examen
     */
    public static List<Note> trouverParExamen(int idExamen) throws SQLException {
        List<Note> liste = new ArrayList<>();
        String sql = "SELECT id_etudiant, id_examen, note, date FROM note WHERE id_examen = ? ORDER BY id_etudiant";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idExamen);
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
        String sql = "INSERT INTO note (id_etudiant, id_examen, note, date) VALUES (?, ?, ?, CURRENT_DATE)";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id_etudiant);
            stmt.setInt(2, this.id_examen);
            stmt.setInt(3, this.valeur);

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
        String sql = "UPDATE note SET id_etudiant = ?, id_examen = ?, note = ?, date = CURRENT_DATE WHERE id_etudiant = ? AND id_examen = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id_etudiant);
            stmt.setInt(2, this.id_examen);
            stmt.setInt(3, this.valeur);
            stmt.setInt(4, this.id_etudiant);
            stmt.setInt(5, this.id_examen);

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

        String sql = "DELETE FROM note WHERE id_etudiant = ? AND id_examen = ?";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id_etudiant);
            stmt.setInt(2, this.id_examen);
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

        Note fresh = trouverParIdEtudiantExamen(this.id_etudiant, this.id_examen);
        if (fresh != null) {
            this.id_etudiant = fresh.id_etudiant;
            this.id_examen = fresh.id_examen;
            this.valeur = fresh.valeur;
            this.date = fresh.date;
            return true;
        }
        return false;
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Creer un objet depuis un ResultSet
     */
    private static Note creerDepuisResultSet(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.id_etudiant = rs.getInt("id_etudiant");
        note.id_examen = rs.getInt("id_examen");
        note.valeur = rs.getInt("note");
        note.date = rs.getDate("date").toLocalDate();
        note.persisted = true;
        return note;
    }

    // ================ Méthodes pour le controllers ================

    public static void creationNoteParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/app/login");
            return;
        }

        if (!Role.estAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            request.setAttribute("examens", model.Examen.trouverTous());
            request.setAttribute("etudiants", model.Utilisateur.trouverTousLesEtudiants());
        } catch (SQLException e) {
             e.printStackTrace();
             request.setAttribute("error", "Erreur lors du chargement des listes: " + e.getMessage());
        }

        String examenIdStr = request.getParameter("examenId");
        String etudiantIdStr = request.getParameter("etudiantId");
        String noteStr = request.getParameter("note");

        try {
            if (examenIdStr == null || examenIdStr.isEmpty() || etudiantIdStr == null || etudiantIdStr.isEmpty() || 
                noteStr == null || noteStr.isEmpty()) {
                request.setAttribute("error", "Tous les champs sont requis.");
                request.getRequestDispatcher("/WEB-INF/views/creerNote.jsp").forward(request, response);
                return;
            }

            int examenId = Integer.parseInt(examenIdStr);
            int etudiantId = Integer.parseInt(etudiantIdStr);
            int noteVal = Integer.parseInt(noteStr);

            model.Note note = new model.Note(noteVal, examenId, etudiantId);
            
            if (note.save()) {
                request.setAttribute("success", "Note attribuée avec succès");
            } else {
                request.setAttribute("error", "Erreur lors de l'enregistrement de la note");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Format numérique invalide");
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/creerNote.jsp").forward(request, response);
    }

    public static String afficherNotes(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            boolean isAdmin = Role.estAdmin(session);
            boolean isProfesseur = Role.estProfesseur(session);
            
            String idExamStr = request.getParameter("examId");
            if (idExamStr != null && !idExamStr.isEmpty()) {
                int idExam = Integer.parseInt(idExamStr);
                
                // Vérifier que le professeur a accès à cet examen (via la matière)
                if (isProfesseur && !isAdmin) {
                    Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
                    if (currentUser != null) {
                        Examen examen = model.Examen.trouverParId(idExam);
                        if (examen != null) {
                            int matId = examen.getId_matiere();
                            Matiere matiere = model.Matiere.trouverParId(matId);
                            if (matiere == null || matiere.getProfId() != currentUser.getId()) {
                                request.setAttribute("error", "Accès refusé : vous n'avez pas accès à cet examen");
                                return "/WEB-INF/views/error.jsp";
                            }
                        }
                    }
                }
                
                Examen examen = model.Examen.trouverParId(idExam);
                request.setAttribute("examen", examen);
                request.setAttribute("notes", model.Note.trouverParExamen(idExam));
                
                // Charger tous les étudiants
                List<Utilisateur> etudiants = model.Utilisateur.trouverTousLesEtudiants();
                request.setAttribute("etudiants", etudiants);
            }
            return "/WEB-INF/views/creerNote.jsp";
        } catch (Exception e) {
            request.setAttribute("error", "Erreur : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
        }
    }

    public static void sauvegarderNotes(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/app/login");
            return;
        }

        boolean isAdmin = Role.estAdmin(session);
        boolean isProfesseur = Role.estProfesseur(session);
        
        if (!isAdmin && !isProfesseur) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"success\": false, \"message\": \"Accès refusé\"}");
            return;
        }

        try {
            String examenIdStr = request.getParameter("examId");
            if (examenIdStr == null || examenIdStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"message\": \"ID d'examen manquant\"}");
                return;
            }

            int examenId = Integer.parseInt(examenIdStr);
            
            // Vérifier que le professeur a accès à cet examen
            if (isProfesseur && !isAdmin) {
                Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
                if (currentUser != null) {
                    Examen examen = model.Examen.trouverParId(examenId);
                    if (examen != null) {
                        int matId = examen.getId_matiere();
                        Matiere matiere = model.Matiere.trouverParId(matId);
                        if (matiere == null || matiere.getProfId() != currentUser.getId()) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"success\": false, \"message\": \"Accès refusé à cet examen\"}");
                            return;
                        }
                    }
                }
            }

            int notesEnregistrees = 0;
            
            // Récupérer tous les paramètres qui commencent par "note_"
            java.util.Map<String, String[]> params = request.getParameterMap();
            for (String paramName : params.keySet()) {
                if (paramName.startsWith("note_")) {
                    String etudiantIdStr = paramName.substring(5); // Extraire l'ID après "note_"
                    String noteValStr = request.getParameter(paramName);
                    
                    if (noteValStr != null && !noteValStr.trim().isEmpty()) {
                        try {
                            int etudiantId = Integer.parseInt(etudiantIdStr);
                            double noteValDouble = Double.parseDouble(noteValStr);
                            
                            // Convertir en entier (multiplier par 100 pour garder 2 décimales)
                            int noteVal = (int) Math.round(noteValDouble * 100);
                            
                            // Vérifier si une note existe déjà pour cet étudiant et cet examen
                            Note noteExistante = Note.trouverParIdEtudiantExamen(etudiantId, examenId);
                            
                            if (noteExistante != null) {
                                // Mettre à jour la note existante
                                noteExistante.setValeur(noteVal);
                                if (noteExistante.save()) {
                                    notesEnregistrees++;
                                }
                            } else {
                                // Créer une nouvelle note
                                Note nouvelleNote = new Note(noteVal, examenId, etudiantId);
                                if (nouvelleNote.save()) {
                                    notesEnregistrees++;
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Ignorer les valeurs invalides
                            System.err.println("Valeur de note invalide pour l'étudiant " + etudiantIdStr + ": " + noteValStr);
                        }
                    }
                }
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true, \"message\": \"" + notesEnregistrees + " note(s) enregistrée(s) avec succès\"}");
            
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"Format numérique invalide\"}");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Erreur BD: " + e.getMessage() + "\"}");
        }
    }

    public static void supprimerNote(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        boolean isAdmin = Role.estAdmin(session);
        boolean isProfesseur = Role.estProfesseur(session);
        
        if (!isAdmin && !isProfesseur) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            int etudiantId = Integer.parseInt(request.getParameter("etudiantId"));
            int examenId = Integer.parseInt(request.getParameter("examenId"));
            
            // Vérifier que le professeur a accès à cet examen
            if (isProfesseur && !isAdmin) {
                Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
                if (currentUser != null) {
                    Examen examen = model.Examen.trouverParId(examenId);
                    if (examen != null) {
                        int matId = examen.getId_matiere();
                        Matiere matiere = model.Matiere.trouverParId(matId);
                        if (matiere == null || matiere.getProfId() != currentUser.getId()) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }
                    }
                }
            }
            
            model.Note n = model.Note.trouverParIdEtudiantExamen(etudiantId, examenId);
            if (n != null) {
                n.supprimer();
            }
            response.sendRedirect(request.getContextPath() + "/app/admin/notes?examId=" + examenId);
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    public static void modifierNote(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        boolean isAdmin = Role.estAdmin(session);
        boolean isProfesseur = Role.estProfesseur(session);
        
        if (!isAdmin && !isProfesseur) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        try {
            int etudiantId = Integer.parseInt(request.getParameter("etudiantId"));
            int examenId = Integer.parseInt(request.getParameter("examenId"));
            int valeur = Integer.parseInt(request.getParameter("note"));
            
            // Vérifier que le professeur a accès à cet examen
            if (isProfesseur && !isAdmin) {
                Utilisateur currentUser = (Utilisateur) session.getAttribute("user");
                if (currentUser != null) {
                    Examen examen = model.Examen.trouverParId(examenId);
                    if (examen != null) {
                        int matId = examen.getId_matiere();
                        Matiere matiere = model.Matiere.trouverParId(matId);
                        if (matiere == null || matiere.getProfId() != currentUser.getId()) {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }
                    }
                }
            }
            
            model.Note n = model.Note.trouverParIdEtudiantExamen(etudiantId, examenId);
            if (n != null) {
                n.setValeur(valeur);
                n.save();
            }
            response.sendRedirect(request.getContextPath() + "/app/admin/notes?examId=" + examenId);
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Vérifier si l'objet est persisté en base de données
     */
    public boolean estPersiste() {
        return persisted;
    }

    // Getters et Setters
    public int getIdEtudiant() {
        return id_etudiant;
    }
    public void setIdEtudiant(int id_etudiant) {
        this.id_etudiant = id_etudiant;
    }
    public int getIdExamen() {
        return id_examen;
    }
    public void setIdExamen(int id_examen) {
        this.id_examen = id_examen;
    }
    public int getValeur() {
        return valeur;
    }
    public void setValeur(int valeur) {
        this.valeur = valeur;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
