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

    /**
     * Trouver les notes d'un étudiant
     */
    public static List<Note> trouverParEtudiant(int idEtudiant) throws SQLException {
        List<Note> liste = new ArrayList<>();
        String sql = "SELECT id_etudiant, id_examen, note, date FROM note WHERE id_etudiant = ? ORDER BY date";

        try (Connection conn = DatabaseManager.obtenirConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEtudiant);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                liste.add(creerDepuisResultSet(rs));
            }
        }
        return liste;
    }

    // ==================== Méthodes de calcul de statistiques ====================

    /**
     * Calculer toutes les statistiques des notes d'un étudiant
     */
    public static java.util.Map<String, Object> calculerStatistiquesEtudiant(int idEtudiant) throws SQLException {
        java.util.Map<String, Object> statistiques = new java.util.HashMap<>();
        
        List<Note> toutesLesNotes = trouverParEtudiant(idEtudiant);
        
        if (toutesLesNotes == null || toutesLesNotes.isEmpty()) {
            statistiques.put("moyenneGenerale", 0.0);
            statistiques.put("statistiquesSemestres", new java.util.TreeMap<Integer, java.util.Map<String, Object>>());
            statistiques.put("moyennesMatieres", new java.util.HashMap<String, Double>());
            statistiques.put("meilleureMatiere", null);
            statistiques.put("meilleureMoyenne", 0.0);
            statistiques.put("pireMatiere", null);
            statistiques.put("pireMoyenne", 0.0);
            statistiques.put("matieresSem1", new ArrayList<java.util.Map<String, Object>>());
            statistiques.put("matieresSem2", new ArrayList<java.util.Map<String, Object>>());
            statistiques.put("groupesSem1", new java.util.LinkedHashMap<String, List<java.util.Map<String, Object>>>());
            statistiques.put("groupesSem2", new java.util.LinkedHashMap<String, List<java.util.Map<String, Object>>>());
            statistiques.put("moyenneMatieresSem1", new java.util.LinkedHashMap<String, Double>());
            statistiques.put("moyenneMatieresSem2", new java.util.LinkedHashMap<String, Double>());
            statistiques.put("moyenneSem1", 0.0);
            statistiques.put("moyenneSem2", 0.0);
            return statistiques;
        }
        
        // Calculer les statistiques par semestre et matière
        java.util.Map<Integer, java.util.Map<String, Object>> statistiquesSemestres = new java.util.TreeMap<>();
        java.util.Map<String, List<Double>> notesParMatiere = new java.util.HashMap<>();
        List<java.util.Map<String, Object>> matieresSem1 = new ArrayList<>();
        List<java.util.Map<String, Object>> matieresSem2 = new ArrayList<>();
        // Regroupement des examens par matière et semestre
        java.util.Map<String, List<java.util.Map<String, Object>>> groupesSem1 = new java.util.LinkedHashMap<>();
        java.util.Map<String, List<java.util.Map<String, Object>>> groupesSem2 = new java.util.LinkedHashMap<>();
        // Accumulateurs pour moyennes par matière par semestre
        java.util.Map<String, List<Double>> notesParMatiereSem1 = new java.util.HashMap<>();
        java.util.Map<String, List<Double>> notesParMatiereSem2 = new java.util.HashMap<>();
        
        double sommeTotal = 0;
        int compteurTotal = 0;
        
        for (Note note : toutesLesNotes) {
            Examen examen = Examen.trouverParId(note.getIdExamen());
            if (examen == null) continue;
            
            Matiere matiere = Matiere.trouverParId(examen.getId_matiere());
            if (matiere == null) continue;
            
            String nomMatiere = matiere.getNom();
            double noteValeur = note.getValeur();
            
            // Déterminer le semestre depuis la matière
            int semestre = matiere.getSemestre();
            
            // Accumuler les notes par matière
            notesParMatiere.computeIfAbsent(nomMatiere, k -> new ArrayList<>()).add(noteValeur);
            
            // Accumuler les données par semestre
            statistiquesSemestres.computeIfAbsent(semestre, k -> {
                java.util.Map<String, Object> donneesSem = new java.util.HashMap<>();
                donneesSem.put("notes", new ArrayList<Double>());
                donneesSem.put("moyenne", 0.0);
                return donneesSem;
            });
            
            @SuppressWarnings("unchecked")
            List<Double> notesSem = (List<Double>) statistiquesSemestres.get(semestre).get("notes");
            notesSem.add(noteValeur);
            
            sommeTotal += noteValeur;
            compteurTotal++;
        }
        
        // Calculer les moyennes
        double moyenneGenerale = compteurTotal > 0 ? sommeTotal / compteurTotal : 0.0;
        
        for (Integer sem : statistiquesSemestres.keySet()) {
            java.util.Map<String, Object> donneesSem = statistiquesSemestres.get(sem);
            @SuppressWarnings("unchecked")
            List<Double> notes = (List<Double>) donneesSem.get("notes");
            double moy = notes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            donneesSem.put("moyenne", moy);
        }
        
        // Trouver meilleure et pire matière
        String meilleureMatiere = null;
        double meilleureMoyenne = 0.0;
        String pireMatiere = null;
        double pireMoyenne = 20.0;
        
        for (java.util.Map.Entry<String, List<Double>> entree : notesParMatiere.entrySet()) {
            double moy = entree.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            if (moy > meilleureMoyenne) {
                meilleureMoyenne = moy;
                meilleureMatiere = entree.getKey();
            }
            if (moy < pireMoyenne) {
                pireMoyenne = moy;
                pireMatiere = entree.getKey();
            }
        }
        
        // Construire les listes de matières par semestre
        for (Note note : toutesLesNotes) {
            Examen examen = Examen.trouverParId(note.getIdExamen());
            if (examen == null) continue;
            
            Matiere matiere = Matiere.trouverParId(examen.getId_matiere());
            if (matiere == null) continue;
            
            String nomMatiere = matiere.getNom();
            int semestre = matiere.getSemestre();
            java.util.Map<String, Object> matiere_obj = new java.util.LinkedHashMap<>();
            
            matiere_obj.put("nom", nomMatiere);
            matiere_obj.put("note", (double) note.getValeur());
            matiere_obj.put("minimum", calculerNoteMinimum(examen.getId()));
            matiere_obj.put("maximum", calculerNoteMaximum(examen.getId()));
            matiere_obj.put("moyenneGroupe", calculerMoyenneGroupe(examen.getId()));
            matiere_obj.put("estMeilleure", nomMatiere.equals(meilleureMatiere));

            // Construire l'entrée pour l'examen
            java.util.Map<String, Object> entreeExamen = new java.util.LinkedHashMap<>();
            entreeExamen.put("nomExamen", examen.getNom());
            entreeExamen.put("note", (double) note.getValeur());
            entreeExamen.put("minimum", calculerNoteMinimum(examen.getId()));
            entreeExamen.put("maximum", calculerNoteMaximum(examen.getId()));
            entreeExamen.put("moyenneGroupe", calculerMoyenneGroupe(examen.getId()));
            
            if (semestre == 1) {
                matieresSem1.add(matiere_obj);
                groupesSem1.computeIfAbsent(nomMatiere, k -> new ArrayList<>()).add(entreeExamen);
                notesParMatiereSem1.computeIfAbsent(nomMatiere, k -> new ArrayList<>()).add((double) note.getValeur());
            } else {
                matieresSem2.add(matiere_obj);
                groupesSem2.computeIfAbsent(nomMatiere, k -> new ArrayList<>()).add(entreeExamen);
                notesParMatiereSem2.computeIfAbsent(nomMatiere, k -> new ArrayList<>()).add((double) note.getValeur());
            }
        }
        
        // Calculer le classement dans la spécialité
        java.util.Map<String, Object> classement = calculerClassementDansSpecialite(idEtudiant);
        
        // Calculer les moyennes par matière par semestre
        java.util.Map<String, Double> moyenneMatieresSem1 = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<String, List<Double>> e : notesParMatiereSem1.entrySet()) {
            double moy = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            moyenneMatieresSem1.put(e.getKey(), moy);
        }
        java.util.Map<String, Double> moyenneMatieresSem2 = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<String, List<Double>> e : notesParMatiereSem2.entrySet()) {
            double moy = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            moyenneMatieresSem2.put(e.getKey(), moy);
        }

        // Moyennes des semestres calculées à partir des moyennes par matière
        double moyenneSem1DepuisMatieres = moyenneMatieresSem1.isEmpty()
            ? 0.0
            : moyenneMatieresSem1.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double moyenneSem2DepuisMatieres = moyenneMatieresSem2.isEmpty()
            ? 0.0
            : moyenneMatieresSem2.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        statistiques.put("moyenneGenerale", moyenneGenerale);
        statistiques.put("statistiquesSemestres", statistiquesSemestres);
        statistiques.put("moyennesMatieres", notesParMatiere);
        statistiques.put("meilleureMatiere", meilleureMatiere);
        statistiques.put("meilleureMoyenne", meilleureMoyenne);
        statistiques.put("pireMatiere", pireMatiere);
        statistiques.put("pireMoyenne", pireMoyenne);
        statistiques.put("matieresSem1", matieresSem1);
        statistiques.put("matieresSem2", matieresSem2);
        statistiques.put("groupesSem1", groupesSem1);
        statistiques.put("groupesSem2", groupesSem2);
        statistiques.put("moyenneMatieresSem1", moyenneMatieresSem1);
        statistiques.put("moyenneMatieresSem2", moyenneMatieresSem2);
        statistiques.put("moyenneSem1", moyenneSem1DepuisMatieres);
        statistiques.put("moyenneSem2", moyenneSem2DepuisMatieres);
        statistiques.put("classementDansSpecialite", classement.get("classement"));
        statistiques.put("totalEtudiantsDansSpecialite", classement.get("total"));
        
        return statistiques;
    }
    
    /**
     * Calculer la note minimale pour un examen
     */
    private static double calculerNoteMinimum(int idExamen) throws SQLException {
        List<Note> notes = trouverParExamen(idExamen);
        if (notes == null || notes.isEmpty()) return 0.0;
        return notes.stream()
            .mapToDouble(Note::getValeur)
            .min()
            .orElse(0.0);
    }
    
    /**
     * Calculer la note maximale pour un examen
     */
    private static double calculerNoteMaximum(int idExamen) throws SQLException {
        List<Note> notes = trouverParExamen(idExamen);
        if (notes == null || notes.isEmpty()) return 0.0;
        return notes.stream()
            .mapToDouble(Note::getValeur)
            .max()
            .orElse(0.0);
    }
    
    /**
     * Calculer la moyenne du groupe pour un examen
     */
    private static double calculerMoyenneGroupe(int idExamen) throws SQLException {
        List<Note> notes = trouverParExamen(idExamen);
        if (notes == null || notes.isEmpty()) return 0.0;
        return notes.stream()
            .mapToDouble(Note::getValeur)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Calculer le classement d'un étudiant dans sa spécialité
     */
    private static java.util.Map<String, Object> calculerClassementDansSpecialite(int idEtudiant) throws SQLException {
        java.util.Map<String, Object> classement = new java.util.HashMap<>();
        
        try {
            Utilisateur etudiant = Utilisateur.trouverParId(idEtudiant);
            if (etudiant == null) {
                classement.put("classement", 0);
                classement.put("total", 0);
                return classement;
            }
            
            int idSpecialite = etudiant.getIdSpecialite();
            
            List<Utilisateur> etudiantsSpecialite = Utilisateur.trouverEtudiantsParSpecialite(idSpecialite);
            if (etudiantsSpecialite == null || etudiantsSpecialite.isEmpty()) {
                classement.put("classement", 0);
                classement.put("total", 0);
                return classement;
            }
            
            // Calculer les moyennes pour tous les étudiants de la spécialité
            List<Double> moyennes = new ArrayList<>();
            double moyenneEtudiantActuel = 0.0;
            
            for (Utilisateur user : etudiantsSpecialite) {
                List<Note> notesUtilisateur = trouverParEtudiant(user.getId());
                if (notesUtilisateur == null || notesUtilisateur.isEmpty()) continue;
                
                double moy = notesUtilisateur.stream()
                    .mapToDouble(Note::getValeur)
                    .average()
                    .orElse(0.0);
                
                moyennes.add(moy);
                if (user.getId() == idEtudiant) {
                    moyenneEtudiantActuel = moy;
                }
            }
            
            // Trier par ordre décroissant pour obtenir le classement
            moyennes.sort((a, b) -> Double.compare(b, a));
            int rang = moyennes.indexOf(moyenneEtudiantActuel) + 1;
            
            classement.put("classement", rang);
            classement.put("total", moyennes.size());
        } catch (Exception e) {
            classement.put("classement", 0);
            classement.put("total", 0);
        }
        
        return classement;
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
                
                request.setAttribute("notes", model.Note.trouverParExamen(idExam));
                request.setAttribute("examen", model.Examen.trouverParId(idExam));
            }
            return "/WEB-INF/views/listeNotes.jsp";
        } catch (Exception e) {
            request.setAttribute("error", "Erreur : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
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
