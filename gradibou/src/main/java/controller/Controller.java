package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Utilisateur;
import util.DatabaseManager;

@WebServlet("/app/*")
public class Controller extends HttpServlet {
    
    @Override
    public void init() throws ServletException {
        try {
            DatabaseManager.init();
            DatabaseManager.creerTables();
            System.out.println("Base de données initialisée");
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Erreur d'initialisation BD", e);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String path = request.getPathInfo();
        
        if (path == null || path.isEmpty()) {
            path = "/";
        }

        String view = null;
        
        switch (path) {
            case "/":
                view = "/WEB-INF/views/index.jsp";
                break;
            case "/login":
                // si l'utilisateur est connecté, on redirige vers sa page d'accueil
                if (request.getSession(false) != null && request.getSession(false).getAttribute("user") != null) {
                    Utilisateur utilisateur = (Utilisateur) request.getSession(false).getAttribute("user");
                    response.sendRedirect(request.getContextPath() + "/app/" + utilisateur.getRole());
                    return;
                }

                view = "/WEB-INF/views/login.jsp";
                break;
            case "/admin":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = "/WEB-INF/views/admin.jsp";
                break;
            case "/admin/specialites":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = afficherSpecialites(request);
                break;
            case "/admin/matieres":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = afficherMatieres(request);
                break;
            case "/admin/examens":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = afficherExamens(request);
                break;
            case "/admin/notes":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = afficherNotes(request);
                break;
            case "/complete-profil":
                String token = request.getParameter("token");
                if (token == null || token.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                try {
                    int userId = model.Lien.validerLien(token);
                    if (userId == -1) {
                        request.setAttribute("error", "Lien invalide ou expiré");
                        view = "/WEB-INF/views/error.jsp";
                    } else {
                        request.setAttribute("token", token);
                        request.setAttribute("userId", userId);
                        view = "/WEB-INF/views/complete-profil.jsp";
                    }
                } catch (Exception e) {
                    request.setAttribute("error", "Erreur: " + e.getMessage());
                    view = "/WEB-INF/views/error.jsp";
                }
                break;
            case "/forgot-password":
                view = "/WEB-INF/views/forgot-password.jsp";
                break;
            case "/admin/creer-compte":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = "/WEB-INF/views/creerCompte.jsp";
                break;
            case "/admin/creer-specialite":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = "/WEB-INF/views/creerSpecialite.jsp";
                break;
            case "/admin/creer-matiere":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    request.setAttribute("specialites", model.Specialite.trouverToutes());
                    request.setAttribute("professeurs", model.Utilisateur.trouverTousLesProfesseurs());
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement des données: " + e.getMessage());
                }
                view = "/WEB-INF/views/creerMatiere.jsp";
                break;
            case "/admin/creer-examen":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    request.setAttribute("matieres", model.Matiere.trouverToutes());
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement des matières: " + e.getMessage());
                }
                view = "/WEB-INF/views/creerExamen.jsp";
                break;
            case "/admin/creer-note":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    request.setAttribute("examens", model.Examen.trouverTous());
                    request.setAttribute("etudiants", model.Utilisateur.trouverTousLesEtudiants());
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement des données: " + e.getMessage());
                }
                view = "/WEB-INF/views/creerNote.jsp";
                break;
            case "/admin/creer-evaluation":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = "/WEB-INF/views/creerEvaluation.jsp";
                break;
            case "/etudiant/evaluations":
                if (!estEtudiant(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    Utilisateur etudiant = (Utilisateur) request.getSession().getAttribute("user");
                    request.setAttribute("evaluations", obtenirEvaluationsDisponibles(etudiant.getId()));
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement des évaluations: " + e.getMessage());
                }
                view = "/WEB-INF/views/evaluations.jsp";
                break;
            case "/etudiant/repondre-evaluation":
                if (!estEtudiant(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    String evalIdStr = request.getParameter("evaluationId");
                    String matiereIdStr = request.getParameter("matiereId");
                    
                    if (evalIdStr != null && matiereIdStr != null) {
                        int evalId = Integer.parseInt(evalIdStr);
                        int matiereId = Integer.parseInt(matiereIdStr);
                        
                        request.setAttribute("evaluation", model.Evaluation.trouverParId(evalId));
                        request.setAttribute("matiere", model.Matiere.trouverParId(matiereId));
                        request.setAttribute("evaluationId", evalId);
                        request.setAttribute("matiereId", matiereId);
                    }
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement de l'évaluation: " + e.getMessage());
                }
                view = "/WEB-INF/views/repondreEvaluation.jsp";
                break;
            case "/admin/resultats-evaluations":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    request.setAttribute("evaluations", model.Evaluation.trouverToutes());
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement des évaluations: " + e.getMessage());
                }
                view = "/WEB-INF/views/resultatEvaluations.jsp";
                break;
                
            case "/admin/resultats-evaluation":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    String evalIdStr = request.getParameter("evaluationId");
                    if (evalIdStr != null && !evalIdStr.isEmpty()) {
                        int evalId = Integer.parseInt(evalIdStr);
                        model.Evaluation evaluation = model.Evaluation.trouverParId(evalId);
                        
                        if (evaluation != null) {
                            request.setAttribute("evaluation", evaluation);
                            
                            // Statistiques globales
                            int tauxGlobal = model.Reponse_Evaluation.calculerTauxReponseGlobal(evalId);
                            double moyenneGlobale = model.Reponse_Evaluation.calculerMoyenneGeneraleGlobale(evalId);
                            
                            request.setAttribute("tauxGlobal", tauxGlobal);
                            request.setAttribute("moyenneGlobale", moyenneGlobale);
                            
                            // Spécialités avec plus/moins de réponses
                            int[] specialitePlusMoins = model.Reponse_Evaluation.recupererIdSpecialitesAvecPlusEtMoinsDeResponses(evalId);
                            java.util.Map<String, Object> specialitePlusInfo = new java.util.HashMap<>();
                            if (specialitePlusMoins[0] > 0) {
                                model.Specialite specPlus = model.Specialite.trouverParId(specialitePlusMoins[0]);
                                specialitePlusInfo.put("plusId", specialitePlusMoins[0]);
                                specialitePlusInfo.put("plusNom", specPlus != null ? specPlus.getNom() : "Inconnue");
                            }
                            if (specialitePlusMoins[1] > 0) {
                                model.Specialite specMoins = model.Specialite.trouverParId(specialitePlusMoins[1]);
                                specialitePlusInfo.put("moinsId", specialitePlusMoins[1]);
                                specialitePlusInfo.put("moinsNom", specMoins != null ? specMoins.getNom() : "Inconnue");
                            }
                            request.setAttribute("specialitePlusMoins", specialitePlusInfo);
                            
                            // Statistiques par spécialité
                            java.util.Map<String, Object> specialiteStats = new java.util.HashMap<>();
                            java.util.List<model.Specialite> specialites = model.Specialite.trouverToutes();
                            for (model.Specialite spec : specialites) {
                                int taux = model.Reponse_Evaluation.calculerTauxReponseParSpecialite(evalId, spec.getId());
                                double moyenne = model.Reponse_Evaluation.calculerMoyenneGeneraleParSpecialite(evalId, spec.getId());
                                
                                java.util.Map<String, Object> stats = new java.util.HashMap<>();
                                stats.put("tauxReponse", taux);
                                stats.put("moyenne", moyenne);
                                specialiteStats.put(spec.getNom(), stats);
                            }
                            request.setAttribute("specialiteStats", specialiteStats);
                            
                            // Statistiques par matière
                            java.util.List<java.util.Map<String, Object>> matiereStats = new java.util.ArrayList<>();
                            java.util.List<model.Matiere> matieres = model.Matiere.trouverToutes();
                            for (model.Matiere mat : matieres) {
                                int taux = model.Reponse_Evaluation.calculerTauxReponseParMatiere(evalId, mat.getId());
                                double qualiteSupport = model.Reponse_Evaluation.calculerMoyenneQualiteSupportParMatiere(evalId, mat.getId());
                                double qualiteEquipe = model.Reponse_Evaluation.calculerMoyenneQualiteEquipeParMatiere(evalId, mat.getId());
                                double qualiteMateriel = model.Reponse_Evaluation.calculerMoyenneQualiteMaterielParMatiere(evalId, mat.getId());
                                double pertinenceExamen = model.Reponse_Evaluation.calculerMoyennePertinenceExamenParMatiere(evalId, mat.getId());
                                double utilitePourFormation = model.Reponse_Evaluation.calculerProportionOuiUtilitePourFormationParMatiere(evalId, mat.getId());
                                
                                java.util.Map<String, Object> stats = new java.util.HashMap<>();
                                stats.put("matiereNom", mat.getNom());
                                stats.put("tauxReponse", taux);
                                stats.put("qualiteSupport", qualiteSupport);
                                stats.put("qualiteEquipe", qualiteEquipe);
                                stats.put("qualiteMateriel", qualiteMateriel);
                                stats.put("pertinenceExamen", pertinenceExamen);
                                stats.put("utilitePourFormation", utilitePourFormation);
                                
                                matiereStats.add(stats);
                            }
                            request.setAttribute("matiereStats", matiereStats);
                        } else {
                            request.setAttribute("error", "Évaluation non trouvée");
                        }
                    } else {
                        request.setAttribute("error", "ID évaluation manquant");
                    }
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur BD: " + e.getMessage());
                }
                view = "/WEB-INF/views/detailsResultatEvaluation.jsp";
                break;
            case "/logout":
                request.getSession().invalidate();
                try {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            default:
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
        }

        if (view != null) {
            try {
                request.getRequestDispatcher(view).forward(request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean estAdmin(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object userObj = session.getAttribute("user");
        if (userObj instanceof Utilisateur) {
            Utilisateur utilisateur = (Utilisateur) userObj;
            return "admin".equalsIgnoreCase(utilisateur.getRole());
        }
        return false;
    }

    private boolean estEtudiant(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object userObj = session.getAttribute("user");
        if (userObj instanceof Utilisateur) {
            Utilisateur utilisateur = (Utilisateur) userObj;
            return "etudiant".equalsIgnoreCase(utilisateur.getRole());
        }
        return false;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        
        try {
            switch (path) {
                case "/login":
                    gererConnexion(request, response);
                    break;
                case "/admin/creer-utilisateur":
                    creationUtilisateurParAdmin(request, response);
                    break;
                case "/admin/creer-specialite":
                    creationSpecialiteParAdmin(request, response);
                    break;
                case "/admin/creer-matiere":
                    creationMatiereParAdmin(request, response);
                    break;
                case "/admin/creer-examen":
                    creationExamenParAdmin(request, response);
                    break;
                case "/admin/creer-note":
                    creationNoteParAdmin(request, response);
                    break;
                case "/admin/creer-evaluation":
                    creationEvaluationParAdmin(request, response);
                    break;
                case "/etudiant/repondre-evaluation":
                    etudiantRepondreEvaluation(request, response);
                    break;
                case "/admin/maj-mdp":
                    creerLienPourMAJMotDePasse(request, response);
                    break;
                case "/complete-profil":
                    completerProfil(request, response);
                    break;
                case "/admin/supprimer-specialite":
                    supprimerSpecialite(request, response);
                    break;
                case "/admin/supprimer-matiere":
                    supprimerMatiere(request, response);
                    break;
                case "/admin/supprimer-examen":
                    supprimerExamen(request, response);
                    break;
                case "/admin/supprimer-note":
                    supprimerNote(request, response);
                    break;
                case "/admin/modifier-specialite":
                    modifierSpecialite(request, response);
                    break;
                case "/admin/modifier-matiere":
                    modifierMatiere(request, response);
                    break;
                case "/admin/modifier-examen":
                    modifierExamen(request, response);
                    break;
                case "/admin/modifier-note":
                    modifierNote(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void gererConnexion(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException, Exception {
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        if (email == null || email.isEmpty() || motDePasse == null || motDePasse.isEmpty()) {
            request.setAttribute("error", "email et mot de passe requis");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        Utilisateur utilisateur = Utilisateur.trouverParemailEtMotDePasse(email, motDePasse);
        if (utilisateur != null) {
            request.getSession().setAttribute("user", utilisateur);
            response.sendRedirect(request.getContextPath() + "/app/" + utilisateur.getRole());
        } else {
            request.setAttribute("error", "email ou mot de passe incorrect");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    private void creationUtilisateurParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        if (!estAdmin(request.getSession(false))) {
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
            envoyerJsonError(response, "Nom, prénom, email, date de naissance et rôle requis", 400);
            return;
        }

        if ("etudiant".equalsIgnoreCase(role) && (ine == null || ine.isEmpty())) {
            envoyerJsonError(response, "L'INE est obligatoire pour les étudiants", 400);
            return;
        }

        try {
            if (Utilisateur.emailExiste(email)) {
                envoyerJsonError(response, "Cet email est déjà utilisé", 409);
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
                envoyerJsonError(response, "Erreur lors de la création de l'utilisateur", 500);
            }
        } catch (java.time.format.DateTimeParseException e) {
            envoyerJsonError(response, "Format de date invalide (utiliser YYYY-MM-DD)", 400);
        } catch (Exception e) {
            envoyerJsonError(response, "Erreur: " + e.getMessage(), 500);
        }
    }

    private void completerProfil(HttpServletRequest request, HttpServletResponse response) 
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

    private void creationSpecialiteParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        if (!estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String tag = request.getParameter("tag");
        String anneeStr = request.getParameter("annee");
        String nom = request.getParameter("nom");

        if (tag == null || tag.isEmpty() || anneeStr == null || anneeStr.isEmpty() || nom == null || nom.isEmpty()) {
            request.setAttribute("error", "Tous les champs sont requis");
            request.getRequestDispatcher("/WEB-INF/views/creerSpecialite.jsp").forward(request, response);
            return;
        }

        try {
            int annee = Integer.parseInt(anneeStr);
            model.Specialite spec = new model.Specialite(tag, annee, nom);
            
            if (spec.save()) {
                request.setAttribute("success", "Spécialité créée avec succès");
            } else {
                request.setAttribute("error", "Erreur lors de la création de la spécialité");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "L'année doit être un nombre valide");
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/creerSpecialite.jsp").forward(request, response);
    }

    private void creationMatiereParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (!estAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            request.setAttribute("specialites", model.Specialite.trouverToutes());
            request.setAttribute("professeurs", model.Utilisateur.trouverTousLesProfesseurs());
        } catch (Exception e) {
             e.printStackTrace();
             request.setAttribute("error", "Erreur lors du chargement des listes: " + e.getMessage());
        }

        // Traitement du formulaire (POST)
        String nom = request.getParameter("nom");
        String semestreStr = request.getParameter("semestre");
        String specialiteIdStr = request.getParameter("specialiteId");
        String profIdStr = request.getParameter("profId");

        try {
            // Check if all fields (including profId) are present
            if (nom == null || nom.isEmpty() || semestreStr == null || semestreStr.isEmpty() || 
                specialiteIdStr == null || specialiteIdStr.isEmpty() ||
                profIdStr == null || profIdStr.isEmpty()) {
                request.setAttribute("error", "Tous les champs sont requis, y compris le professeur.");
                request.getRequestDispatcher("/WEB-INF/views/creerMatiere.jsp").forward(request, response);
                return;
            }

            int semestre = Integer.parseInt(semestreStr);
            int specialiteId = Integer.parseInt(specialiteIdStr);
            int profId = Integer.parseInt(profIdStr);

            model.Matiere matiere = new model.Matiere(nom, semestre, specialiteId, profId);
            
            if (matiere.save()) {
                request.setAttribute("success", "Matière créée avec succès");
            } else {
                request.setAttribute("error", "Erreur lors de la création de la matière");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Format numérique invalide");
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/creerMatiere.jsp").forward(request, response);
    }

    private void creationExamenParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/app/login");
            return;
        }

        if (!estAdmin(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            request.setAttribute("matieres", model.Matiere.trouverToutes());
        } catch (SQLException e) {
             e.printStackTrace();
             request.setAttribute("error", "Erreur lors du chargement des matières: " + e.getMessage());
        }

        String nom = request.getParameter("nom");
        String coefficientStr = request.getParameter("coefficient");
        String matiereIdStr = request.getParameter("matiereId");

        try {
            if (nom == null || nom.isEmpty() || coefficientStr == null || coefficientStr.isEmpty() || 
                matiereIdStr == null || matiereIdStr.isEmpty()) {
                request.setAttribute("error", "Tous les champs sont requis.");
                request.getRequestDispatcher("/WEB-INF/views/creerExamen.jsp").forward(request, response);
                return;
            }

            int coefficient = Integer.parseInt(coefficientStr);
            int matiereId = Integer.parseInt(matiereIdStr);

            model.Examen examen = new model.Examen(nom, coefficient, matiereId);
            
            if (examen.save()) {
                request.setAttribute("success", "Examen créé avec succès");
            } else {
                request.setAttribute("error", "Erreur lors de la création de l'examen");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Format numérique invalide");
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/creerExamen.jsp").forward(request, response);
    }

    private void creationNoteParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/app/login");
            return;
        }

        if (!estAdmin(session)) {
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

    public void creerLienPourMAJMotDePasse(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {

        String emailUtilisateur = request.getParameter("email");
        if (emailUtilisateur == null || emailUtilisateur.isEmpty()) {
            envoyerJsonError(response, "email utilisateur requis", 400);
            return;
        }

        Utilisateur utilisateur = Utilisateur.trouverParemail(emailUtilisateur);
        if (utilisateur == null) {
            envoyerJsonError(response, "Utilisateur non trouvé", 404);
            return;
        }

        try {
            int userId = utilisateur.getId();

            String token = model.Lien.creerLien(userId, 1); // Lien valide 1 jour
            String lienMDP = request.getContextPath() + "/app/complete-profil?token=" + token;

            envoyerJsonSuccess(response, "Lien créé avec succès", lienMDP);
            // PAS SAFE, EN ATTENTE DE MISE EN PLACE DE CONNEXION SMTP
        } catch (NumberFormatException e) {
            envoyerJsonError(response, "ID utilisateur invalide", 400);
        } catch (Exception e) {
            envoyerJsonError(response, "Erreur: " + e.getMessage(), 500);
        }
    }

    private String afficherSpecialites(HttpServletRequest request) {
        try {
            request.setAttribute("specialites", model.Specialite.trouverToutes());
            return "/WEB-INF/views/listeSpecialites.jsp";
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur lors du chargement des spécialités : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
        }
    }

    private String afficherMatieres(HttpServletRequest request) {
        try {
            String idSpecStr = request.getParameter("specId");
            if (idSpecStr != null && !idSpecStr.isEmpty()) {
                int idSpec = Integer.parseInt(idSpecStr);
                request.setAttribute("matieres", model.Matiere.trouverParSpecialite(idSpec));
                request.setAttribute("specialite", model.Specialite.trouverParId(idSpec));
            } else {
                request.setAttribute("matieres", model.Matiere.trouverToutes());
                request.setAttribute("specialite", null);
            }
            request.setAttribute("professeurs", model.Utilisateur.trouverTousLesProfesseurs());
            return "/WEB-INF/views/listeMatieres.jsp";
        } catch (Exception e) {
            request.setAttribute("error", "Erreur : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
        }
    }

    private String afficherExamens(HttpServletRequest request) {
        try {
            String idMatStr = request.getParameter("matId");
            if (idMatStr != null && !idMatStr.isEmpty()) {
                int idMat = Integer.parseInt(idMatStr);
                request.setAttribute("examens", model.Examen.trouverParMatiere(idMat));
                request.setAttribute("matiere", model.Matiere.trouverParId(idMat));
            } else {
                request.setAttribute("examens", model.Examen.trouverTous());
            }
            return "/WEB-INF/views/listeExamens.jsp";
        } catch (Exception e) {
            request.setAttribute("error", "Erreur : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
        }
    }

    private String afficherNotes(HttpServletRequest request) {
        try {
            String idExamStr = request.getParameter("examId");
            if (idExamStr != null && !idExamStr.isEmpty()) {
                int idExam = Integer.parseInt(idExamStr);
                request.setAttribute("notes", model.Note.trouverParExamen(idExam));
                request.setAttribute("examen", model.Examen.trouverParId(idExam));
            }
            return "/WEB-INF/views/listeNotes.jsp";
        } catch (Exception e) {
            request.setAttribute("error", "Erreur : " + e.getMessage());
            return "/WEB-INF/views/error.jsp";
        }
    }

    private void supprimerSpecialite(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            model.Specialite s = model.Specialite.trouverParId(id);
            if (s != null) {
                s.supprimer();
            }
            response.sendRedirect(request.getContextPath() + "/app/admin/specialites");
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void supprimerMatiere(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            model.Matiere m = model.Matiere.trouverParId(id);
            if (m != null) {
                m.supprimer();
            }
            // Redirection intelligente : on essaie de revenir à la liste filtrée si on a l'info
            String specId = request.getParameter("specId");
            if (specId != null && !specId.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/app/admin/matieres?specId=" + specId);
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin/specialites");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void supprimerExamen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            model.Examen e = model.Examen.trouverParId(id);
            int matId = -1;
            if (e != null) {
                matId = e.getId_matiere();
                e.supprimer();
            }
            if (matId != -1) {
                response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matId);
            } else {
                response.sendRedirect(request.getContextPath() + "/app/admin/specialites");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void supprimerNote(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            int etudiantId = Integer.parseInt(request.getParameter("etudiantId"));
            int examenId = Integer.parseInt(request.getParameter("examenId"));
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

    private void modifierSpecialite(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!estAdmin(request.getSession(false))) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN);
             return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            String tag = request.getParameter("tag");
            int annee = Integer.parseInt(request.getParameter("annee"));
            
            model.Specialite s = model.Specialite.trouverParId(id);
            if (s != null) {
                s.setNom(nom);
                s.setTag(tag);
                s.setAnnee(annee);
                s.save();
            }
            response.sendRedirect(request.getContextPath() + "/app/admin/specialites");
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void modifierMatiere(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!estAdmin(request.getSession(false))) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN);
             return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            int semestre = Integer.parseInt(request.getParameter("semestre"));
            int coefficient = Integer.parseInt(request.getParameter("coefficient"));
            int profId = Integer.parseInt(request.getParameter("profId"));
            
            model.Matiere m = model.Matiere.trouverParId(id);
            if (m != null) {
                m.setNom(nom);
                m.setSemestre(semestre);
                m.setProfId(profId);
                m.save();
            }
            String specId = request.getParameter("specId");
            if (specId != null && !specId.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/app/admin/matieres?specId=" + specId);
            } else {
                 response.sendRedirect(request.getContextPath() + "/app/admin/specialites");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void modifierExamen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!estAdmin(request.getSession(false))) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN);
             return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nom = request.getParameter("nom");
            int coefficient = Integer.parseInt(request.getParameter("coefficient"));
             // Date n'est pas modifiable dans cette version simplifiée ou on garde l'existante
            
            model.Examen e = model.Examen.trouverParId(id);
            if (e != null) {
                e.setNom(nom);
                e.setCoefficient(coefficient);
                e.save();
            }
            int matId = e != null ? e.getId_matiere() : -1;
            response.sendRedirect(request.getContextPath() + "/app/admin/examens?matId=" + matId);
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la modification : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void modifierNote(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (!estAdmin(request.getSession(false))) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN);
             return;
        }
        try {
            int etudiantId = Integer.parseInt(request.getParameter("etudiantId"));
            int examenId = Integer.parseInt(request.getParameter("examenId"));
            int valeur = Integer.parseInt(request.getParameter("note"));
            
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

    private void envoyerJsonSuccess(HttpServletResponse response, String message, String lien) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(String.format(
            "{\"success\": true, \"message\": \"%s\", \"lien\": \"%s\"}", 
            message, lien
        ));
    }

    private void envoyerJsonError(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getWriter().write(String.format(
            "{\"success\": false, \"message\": \"%s\"}", 
            message
        ));
    }

    private void creationEvaluationParAdmin(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        if (!estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String dateDebutStr = request.getParameter("date_debut");
        String dateFinStr = request.getParameter("date_fin");
        String semestreStr = request.getParameter("semestre");

        try {
            if (dateDebutStr == null || dateDebutStr.isEmpty() || dateFinStr == null || dateFinStr.isEmpty() || 
                semestreStr == null || semestreStr.isEmpty()) {
                request.setAttribute("error", "Tous les champs sont requis.");
                request.getRequestDispatcher("/WEB-INF/views/creerEvaluation.jsp").forward(request, response);
                return;
            }

            int semestre = Integer.parseInt(semestreStr);
            
            // Convertir les dates du format datetime-local
            java.time.LocalDateTime dateDebut = java.time.LocalDateTime.parse(dateDebutStr);
            java.time.LocalDateTime dateFin = java.time.LocalDateTime.parse(dateFinStr);

            model.Evaluation evaluation = new model.Evaluation(dateDebut, dateFin, semestre);
            evaluation.save();
            
            request.setAttribute("success", "Évaluation créée avec succès (ID: " + evaluation.getId() + ")");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Format numérique invalide");
        } catch (java.time.format.DateTimeParseException e) {
            request.setAttribute("error", "Format de date invalide");
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
        }
        
        request.getRequestDispatcher("/WEB-INF/views/creerEvaluation.jsp").forward(request, response);
    }

    private java.util.List<java.util.Map<String, Object>> obtenirEvaluationsDisponibles(int idEtudiant) throws SQLException {
        java.util.List<java.util.Map<String, Object>> evaluations = new java.util.ArrayList<>();
        
        // Récupérer la spécialité de l'étudiant
        Utilisateur etudiant = Utilisateur.trouverParId(idEtudiant);
        if (etudiant == null || etudiant.getIdSpecialite() <= 0) {
            return evaluations;
        }

        int idSpecialite = etudiant.getIdSpecialite();

        // Récupérer toutes les évaluations et les matières de la spécialité
        String sql = "SELECT DISTINCT e.id, e.date_debut, e.date_fin, e.semestre, m.id as matiere_id, m.nom as matiere_nom " +
                 "FROM evaluation e, matiere m " +
                 "WHERE m.id_specialite = ? " +
                 "AND m.semestre = e.semestre " +
                 "ORDER BY e.date_fin DESC";
        
        try (java.sql.Connection conn = util.DatabaseManager.obtenirConnexion();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idSpecialite);
            java.sql.ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                java.util.Map<String, Object> eval = new java.util.HashMap<>();
                int evalId = rs.getInt("id");
                int matiereId = rs.getInt("matiere_id");
                
                eval.put("evaluation_id", evalId);
                eval.put("matiere_id", matiereId);
                eval.put("matiere_nom", rs.getString("matiere_nom"));
                eval.put("date_debut", rs.getObject("date_debut"));
                eval.put("date_fin", rs.getObject("date_fin"));
                eval.put("semestre", rs.getInt("semestre"));
                
                // Vérifier si l'étudiant a déjà répondu
                boolean aRepondu = model.A_Repondu_Evaluation.aRepondu(idEtudiant, matiereId, evalId);
                
                // Vérifier si l'évaluation est toujours ouverte
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime dateFin = rs.getObject("date_fin", java.time.LocalDateTime.class);
                
                String status;
                if (aRepondu) {
                    status = "answered";
                } else if (now.isAfter(dateFin)) {
                    status = "closed";
                } else {
                    status = "open";
                }
                
                eval.put("status", status);
                
                // Calculer le taux de réponse
                try {
                    int tauxReponse = model.Reponse_Evaluation.calculerTauxReponseParMatiere(evalId, matiereId);
                    eval.put("taux_reponse", tauxReponse);
                } catch (Exception e) {
                    eval.put("taux_reponse", 0);
                }
                
                evaluations.add(eval);
            }
        }
        
        return evaluations;
    }

    private void etudiantRepondreEvaluation(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!estEtudiant(session)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Utilisateur etudiant = (Utilisateur) session.getAttribute("user");
        
        String evaluationIdStr = request.getParameter("evaluationId");
        String matiereIdStr = request.getParameter("matiereId");
        String qualiteSupportStr = request.getParameter("qualite_support");

        try {
            if (evaluationIdStr == null || evaluationIdStr.isEmpty() || matiereIdStr == null || matiereIdStr.isEmpty()) {
                request.setAttribute("error", "Paramètres manquants");
                request.getRequestDispatcher("/WEB-INF/views/evaluations.jsp").forward(request, response);
                return;
            }

            int evaluationId = Integer.parseInt(evaluationIdStr);
            int matiereId = Integer.parseInt(matiereIdStr);

            // Vérifier si c'est un GET (affichage du formulaire) ou POST (soumission)
            if (qualiteSupportStr == null) {
                // GET - Afficher le formulaire
                request.setAttribute("evaluation", model.Evaluation.trouverParId(evaluationId));
                request.setAttribute("matiere", model.Matiere.trouverParId(matiereId));
                request.setAttribute("evaluationId", evaluationId);
                request.setAttribute("matiereId", matiereId);
                request.getRequestDispatcher("/WEB-INF/views/repondreEvaluation.jsp").forward(request, response);
            } else {
                // POST - Traiter la soumission
                String qualiteEquipeStr = request.getParameter("qualite_equipe");
                String qualiteMaterielStr = request.getParameter("qualite_materiel");
                String pertinenceExamenStr = request.getParameter("pertinence_examen");
                String tempsParSemaineStr = request.getParameter("temps_par_semaine");
                String utiliteStr = request.getParameter("utilite_pour_formation");
                String commentaires = request.getParameter("commentaires");

                // Validation des champs obligatoires
                if (qualiteSupportStr == null || qualiteSupportStr.isEmpty() ||
                    qualiteEquipeStr == null || qualiteEquipeStr.isEmpty() ||
                    qualiteMaterielStr == null || qualiteMaterielStr.isEmpty() ||
                    pertinenceExamenStr == null || pertinenceExamenStr.isEmpty() ||
                    tempsParSemaineStr == null || tempsParSemaineStr.isEmpty() ||
                    utiliteStr == null || utiliteStr.isEmpty()) {
                    request.setAttribute("error", "Tous les champs obligatoires doivent être remplis");
                    request.setAttribute("evaluation", model.Evaluation.trouverParId(evaluationId));
                    request.setAttribute("matiere", model.Matiere.trouverParId(matiereId));
                    request.setAttribute("evaluationId", evaluationId);
                    request.setAttribute("matiereId", matiereId);
                    request.getRequestDispatcher("/WEB-INF/views/repondreEvaluation.jsp").forward(request, response);
                    return;
                }

                // Créer la réponse d'évaluation
                model.Reponse_Evaluation reponse = new model.Reponse_Evaluation(
                    Integer.parseInt(qualiteSupportStr),
                    Integer.parseInt(qualiteEquipeStr),
                    Integer.parseInt(qualiteMaterielStr),
                    Integer.parseInt(pertinenceExamenStr),
                    Integer.parseInt(tempsParSemaineStr),
                    Integer.parseInt(utiliteStr),
                    commentaires,
                    matiereId,
                    evaluationId
                );
                
                reponse.insert();

                // Marquer que l'étudiant a répondu
                model.A_Repondu_Evaluation aRepondu = new model.A_Repondu_Evaluation(
                    etudiant.getId(),
                    matiereId,
                    evaluationId
                );
                aRepondu.insert();

                request.setAttribute("success", "Votre réponse a été enregistrée avec succès !");
                request.getRequestDispatcher("/WEB-INF/views/repondreEvaluation.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Format numérique invalide");
            request.getRequestDispatcher("/WEB-INF/views/evaluations.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Erreur BD: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/evaluations.jsp").forward(request, response);
        }
    }
}