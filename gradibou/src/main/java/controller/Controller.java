package controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Evaluation;
import model.Examen;
import model.Lien;
import model.Matiere;
import model.Note;
import model.Reponse_Evaluation;
import model.Specialite;
import model.Utilisateur;
import util.DatabaseManager;
import util.Role;

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
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    // Charger la liste de tous les utilisateurs pour le tableau
                    java.util.List<Utilisateur> utilisateurs = Utilisateur.trouverTousLesUtilisateurs();
                    request.setAttribute("utilisateurs", utilisateurs);
                } catch (SQLException e) {
                    System.err.println("Erreur lors du chargement des utilisateurs: " + e.getMessage());
                    request.setAttribute("error", "Erreur lors du chargement de la liste des utilisateurs");
                }
                view = "/WEB-INF/views/admin.jsp";
                break;
            case "/gestion/specialites":
                jakarta.servlet.http.HttpSession session = request.getSession(false);
                if (!Role.estAdmin(session) && !Role.estProfesseur(session)) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = Specialite.afficherSpecialites(request);
                break;
            case "/admin/specialite-details":
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    String specIdParam = request.getParameter("specId");
                    if (specIdParam != null && !specIdParam.isEmpty()) {
                        int specId = Integer.parseInt(specIdParam);
                        Specialite spec = Specialite.trouverParId(specId);
                        request.setAttribute("specialite", spec);
                        java.util.List<Matiere> matieres = Matiere.trouverParSpecialite(specId);
                        request.setAttribute("matieres", matieres);
                        java.util.List<Utilisateur> students = Utilisateur.trouverEtudiantsParSpecialite(specId);
                        request.setAttribute("students", students);
                    }
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement: " + e.getMessage());
                }
                view = "/WEB-INF/views/admin-specialite-details.jsp";
                break;
            case "/gestion/specialite/details":
                HttpSession sess = request.getSession(false);
                if (!Role.estAdmin(sess) && !Role.estProfesseur(sess)) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = Matiere.afficherMatieres(request);
                break;
            case "/admin/examens":
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = Examen.afficherExamens(request);
                break;
            case "/admin/notes":
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = Note.afficherNotes(request);
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
            case "/professeur":
                System.out.println("DEBUG: Route /professeur atteinte");
                jakarta.servlet.http.HttpSession profSession = request.getSession(false);
                if (profSession != null) {
                    Utilisateur profUser = (Utilisateur) profSession.getAttribute("user");
                    System.out.println("DEBUG: User in session: " + (profUser != null ? profUser.getemail() : "null"));
                    System.out.println("DEBUG: Role in session: " + (profUser != null ? "'" + profUser.getRole() + "'" : "null"));
                    System.out.println("DEBUG: estProfesseur result: " + Role.estProfesseur(profSession));
                }
                if (!Role.estProfesseur(request.getSession(false))) {
                    System.out.println("DEBUG: Redirection vers /login car estProfesseur=false");
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                // Rediriger vers la page de gestion des spécialités
                System.out.println("DEBUG: Redirection vers /gestion/specialites");
                response.sendRedirect(request.getContextPath() + "/app/gestion/specialites");
                return;
            case "/admin/creer-compte":
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                // Rediriger vers la page de gestion des spécialités
                System.out.println("DEBUG: Redirection vers /gestion/specialites");
                response.sendRedirect(request.getContextPath() + "/app/gestion/specialites");
                return;
            case "/etudiant":
                if (!Role.estEtudiant(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                // Rediriger vers la page des évaluations
                response.sendRedirect(request.getContextPath() + "/app/etudiant/evaluations");
                return;
            case "/admin/creer-specialite":
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = "/WEB-INF/views/creerSpecialite.jsp";
                break;
            case "/admin/creer-matiere":
                if (!Role.estAdmin(request.getSession(false))) {
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
                if (!Role.estAdmin(request.getSession(false))) {
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
                if (!Role.estAdmin(request.getSession(false))) {
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
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = "/WEB-INF/views/creerEvaluation.jsp";
                break;
            case "/etudiant/evaluations":
                if (!Role.estEtudiant(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    Utilisateur etudiant = (Utilisateur) request.getSession().getAttribute("user");
                    request.setAttribute("evaluations", Evaluation.obtenirEvaluationsDisponibles(etudiant.getId()));
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement des évaluations: " + e.getMessage());
                }
                view = "/WEB-INF/views/evaluations.jsp";
                break;
            case "/etudiant/repondre-evaluation":
                if (!Role.estEtudiant(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                // Déléguer au handler commun qui gère aussi le GET avec validations
                try {
                    Reponse_Evaluation.etudiantRepondreEvaluation(request, response);
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur BD: " + e.getMessage());
                    try {
                        request.getRequestDispatcher("/WEB-INF/views/evaluations.jsp").forward(request, response);
                    } catch (ServletException ex) {
                        ex.printStackTrace();
                    }
                }
                return;
            case "/admin/resultats-evaluations":
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                try {
                    // Liste des évaluations
                    java.util.List<model.Evaluation> evaluations = model.Evaluation.trouverToutes();
                    
                    // Déterminer le statut de chaque évaluation (programmée/en cours/terminée)
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    java.util.List<java.util.Map<String, Object>> evalStatusList = new java.util.ArrayList<>();
                    for (model.Evaluation e : evaluations) {
                        java.util.Map<String, Object> evalStatus = new java.util.HashMap<>();
                        evalStatus.put("eval", e);
                        
                        String status = "finished";
                        if (e != null) {
                            if (now.isBefore(e.getDate_debut())) {
                                status = "scheduled";
                            } else if (now.isAfter(e.getDate_debut()) && now.isBefore(e.getDate_fin())) {
                                status = "ongoing";
                            }
                        }
                        
                        evalStatus.put("status", status);
                        evalStatus.put("isOngoing", status.equals("ongoing"));
                        evalStatusList.add(evalStatus);
                    }
                    request.setAttribute("evaluations", evaluations);
                    request.setAttribute("evalStatusList", evalStatusList);

                    // Choisir l'évaluation courante: celle ouverte, sinon la plus récente
                    Integer currentEvalId = null;
                    String evalIdParam = request.getParameter("evaluationId");
                    if (evalIdParam != null && !evalIdParam.isEmpty()) {
                        try {
                            currentEvalId = Integer.parseInt(evalIdParam);
                        } catch (NumberFormatException e) {
                            // fallback à la logique par défaut
                        }
                    }
                    if (currentEvalId == null) {
                        for (model.Evaluation e : evaluations) {
                            if (e != null && now.isAfter(e.getDate_debut()) && now.isBefore(e.getDate_fin())) {
                                currentEvalId = e.getId();
                                break;
                            }
                        }
                    }
                    if (currentEvalId == null && !evaluations.isEmpty()) {
                        currentEvalId = evaluations.get(0).getId();
                    }
                    
                    request.setAttribute("currentEvalId", currentEvalId);

                    // Déterminer quelle évaluation afficher dans l'encadré de programmation
                    // Priorité: 1) EVE en cours, 2) EVE programmé (prochain), 3) rien (champs vides)
                    model.Evaluation displayedEval = null;
                    String displayedEvaStatus = null;
                    
                    // 1. Chercher un EVE en cours
                    for (model.Evaluation e : evaluations) {
                        if (e != null && now.isAfter(e.getDate_debut()) && now.isBefore(e.getDate_fin())) {
                            displayedEval = e;
                            displayedEvaStatus = "ongoing";
                            break;
                        }
                    }
                    
                    // 2. Si pas d'EVE en cours, chercher le prochain EVE programmé
                    if (displayedEval == null) {
                        for (model.Evaluation e : evaluations) {
                            if (e != null && now.isBefore(e.getDate_debut())) {
                                displayedEval = e;
                                displayedEvaStatus = "scheduled";
                                break; // On prend le premier (le plus proche car trié par date_debut ASC)
                            }
                        }
                    }
                    
                    // 3. Si ni en cours ni programmé, on laisse displayedEval = null
                    if (displayedEval == null) {
                        displayedEvaStatus = "none";
                    }
                    
                    request.setAttribute("displayedEval", displayedEval);
                    request.setAttribute("displayedEvaStatus", displayedEvaStatus);

                    if (currentEvalId != null) {
                        // Charger l'évaluation courante pour déterminer son statut (pour les stats)
                        model.Evaluation currentEval = model.Evaluation.trouverParId(currentEvalId);
                        request.setAttribute("currentEval", currentEval);
                        
                        // Déterminer le statut : scheduled (futur), ongoing (en cours), finished (passé)
                        String evaStatus = "finished"; // par défaut
                        if (currentEval != null) {
                            if (now.isBefore(currentEval.getDate_debut())) {
                                evaStatus = "scheduled";
                            } else if (now.isAfter(currentEval.getDate_debut()) && now.isBefore(currentEval.getDate_fin())) {
                                evaStatus = "ongoing";
                            }
                        }
                        request.setAttribute("evaStatus", evaStatus);
                        
                        // Taux de réponse global
                        int responseRate = model.Reponse_Evaluation.calculerTauxReponseGlobal(currentEvalId);
                        request.setAttribute("responseRate", responseRate);

                        // Spécialités la plus/moins investie (par taux de réponse)
                        int[] plusMoins = model.Reponse_Evaluation.recupererIdSpecialitesAvecPlusEtMoinsDeResponses(currentEvalId);
                        java.util.Map<String, Object> mostInvested = new java.util.HashMap<>();
                        java.util.Map<String, Object> leastInvested = new java.util.HashMap<>();
                        if (plusMoins[0] > 0) {
                            model.Specialite specPlus = model.Specialite.trouverParId(plusMoins[0]);
                            int tauxPlus = model.Reponse_Evaluation.calculerTauxReponseParSpecialite(currentEvalId, plusMoins[0]);
                            mostInvested.put("tag", specPlus != null ? specPlus.getTag() : "-");
                            mostInvested.put("rate", tauxPlus);
                        }
                        if (plusMoins[1] > 0) {
                            model.Specialite specMoins = model.Specialite.trouverParId(plusMoins[1]);
                            int tauxMoins = model.Reponse_Evaluation.calculerTauxReponseParSpecialite(currentEvalId, plusMoins[1]);
                            leastInvested.put("tag", specMoins != null ? specMoins.getTag() : "-");
                            leastInvested.put("rate", tauxMoins);
                        }
                        request.setAttribute("mostInvested", mostInvested);
                        request.setAttribute("leastInvested", leastInvested);

                        // Satisfaction générale (moyenne /5 convertie en pourcentage)
                        double moyenneGlobale = model.Reponse_Evaluation.calculerMoyenneGeneraleGlobale(currentEvalId);
                        int satisfactionPercent = (int) Math.round((moyenneGlobale / 5.0) * 100.0);
                        request.setAttribute("satisfactionPercent", satisfactionPercent);

                        // Retours les plus négatifs/positifs (par moyenne générale de spécialité)
                        int[] posNeg = model.Reponse_Evaluation.recupererSpecialitePositiveEtNegative(currentEvalId);
                        java.util.Map<String, Object> positiveReturn = new java.util.HashMap<>();
                        java.util.Map<String, Object> negativeReturn = new java.util.HashMap<>();
                        if (posNeg[0] > 0) {
                            model.Specialite specPos = model.Specialite.trouverParId(posNeg[0]);
                            double avgPos = model.Reponse_Evaluation.calculerMoyenneGeneraleParSpecialite(currentEvalId, posNeg[0]);
                            positiveReturn.put("tag", specPos != null ? specPos.getTag() : "-");
                            positiveReturn.put("avg", avgPos);
                        }
                        if (posNeg[1] > 0) {
                            model.Specialite specNeg = model.Specialite.trouverParId(posNeg[1]);
                            double avgNeg = model.Reponse_Evaluation.calculerMoyenneGeneraleParSpecialite(currentEvalId, posNeg[1]);
                            negativeReturn.put("tag", specNeg != null ? specNeg.getTag() : "-");
                            negativeReturn.put("avg", avgNeg);
                        }
                        request.setAttribute("positiveReturn", positiveReturn);
                        request.setAttribute("negativeReturn", negativeReturn);

                        // Spécialité avec le plus de réponses texte
                        int specMostCommentsId = model.Reponse_Evaluation.recupererSpecialiteAvecPlusDeCommentaires(currentEvalId);
                        if (specMostCommentsId > 0) {
                            model.Specialite specMC = model.Specialite.trouverParId(specMostCommentsId);
                            request.setAttribute("mostTextResponsesTag", specMC != null ? specMC.getTag() : "-");
                        }
                    } else {
                        // Valeurs par défaut si aucune évaluation
                        request.setAttribute("responseRate", 0);
                        request.setAttribute("satisfactionPercent", 0);
                        request.setAttribute("mostInvested", java.util.Collections.emptyMap());
                        request.setAttribute("leastInvested", java.util.Collections.emptyMap());
                        request.setAttribute("positiveReturn", java.util.Collections.emptyMap());
                        request.setAttribute("negativeReturn", java.util.Collections.emptyMap());
                        request.setAttribute("mostTextResponsesTag", "-");
                    }
                } catch (SQLException e) {
                    request.setAttribute("error", "Erreur lors du chargement des évaluations: " + e.getMessage());
                }
                view = "/WEB-INF/views/resultatEvaluations.jsp";
                break;
                
            case "/admin/resultats-evaluation":
                if (!Role.estAdmin(request.getSession(false))) {
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
            case "/admin/get-reset-link":
                if (!Role.estAdmin(request.getSession(false))) {
                    util.Json.envoyerJsonError(response, "Accès refusé", HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                try {
                    String userIdStr = request.getParameter("userId");
                    if (userIdStr == null || userIdStr.isEmpty()) {
                        util.Json.envoyerJsonError(response, "userId requis", HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                    int userId = Integer.parseInt(userIdStr);
                    Utilisateur user = Utilisateur.trouverParId(userId);
                    if (user == null) {
                        util.Json.envoyerJsonError(response, "Utilisateur non trouvé", HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    // Créer un lien de réinitialisation valide pour 7 jours
                    String resetToken = model.Lien.creerLien(userId, 7);
                    String resetUrl = request.getScheme() + "://" + request.getServerName() + 
                                    (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
                                    request.getContextPath() + "/app/complete-profil?token=" + resetToken;
                    
                    // Retourner le JSON avec le lien
                    response.setContentType("application/json;charset=UTF-8");
                    java.io.PrintWriter out = response.getWriter();
                    out.print("{\"success\": true, \"link\": \"" + resetUrl.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
                    out.flush();
                } catch (NumberFormatException e) {
                    util.Json.envoyerJsonError(response, "userId invalide", HttpServletResponse.SC_BAD_REQUEST);
                } catch (Exception e) {
                    util.Json.envoyerJsonError(response, "Erreur: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                return;
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
                    Utilisateur.creationUtilisateurParAdmin(request, response);
                    break;
                case "/admin/creer-specialite":
                    Specialite.creationSpecialiteParAdmin(request, response);
                    break;
                case "/admin/creer-matiere":
                    Matiere.creationMatiereParAdmin(request, response);
                    break;
                case "/admin/creer-examen":
                    Examen.creationExamenParAdmin(request, response);
                    break;
                case "/admin/creer-note":
                    Note.creationNoteParAdmin(request, response);
                    break;
                case "/admin/creer-evaluation":
                    Evaluation.creationEvaluationParAdmin(request, response);
                    break;
                case "/etudiant/repondre-evaluation":
                    Reponse_Evaluation.etudiantRepondreEvaluation(request, response);
                    break;
                case "/admin/maj-mdp":
                    Lien.creerLienPourMAJMotDePasse(request, response);
                    break;
                case "/complete-profil":
                    Utilisateur.completerProfil(request, response);
                    break;
                case "/admin/supprimer-specialite":
                    Specialite.supprimerSpecialite(request, response);
                    break;
                case "/admin/supprimer-matiere":
                    Matiere.supprimerMatiere(request, response);
                    break;
                case "/admin/supprimer-examen":
                    Examen.supprimerExamen(request, response);
                    break;
                case "/admin/supprimer-note":
                    Note.supprimerNote(request, response);
                    break;
                case "/admin/modifier-specialite":
                    Specialite.modifierSpecialite(request, response);
                    break;
                case "/admin/modifier-matiere":
                    Matiere.modifierMatiere(request, response);
                    break;
                case "/admin/modifier-examen":
                    Examen.modifierExamen(request, response);
                    break;
                case "/admin/modifier-note":
                    Note.modifierNote(request, response);
                    break;
                case "/admin/modifier-utilisateur":
                    Utilisateur.modifierUtilisateur(request, response);
                    break;
                case "/admin/supprimer-utilisateur":
                    Utilisateur.supprimerUtilisateur(request, response);
                    break;
                case "/admin/retirer-etudiant":
                    Utilisateur.retirerEtudiantSpecialite(request, response);
                    break;
                case "/admin/ajouter-etudiant":
                    Utilisateur.ajouterEtudiantSpecialite(request, response);
                    break;
                case "/admin/resultats-evaluations/program":
                    programmerEvaluation(request, response);
                    break;
                case "/admin/resultats-evaluations/cancel-program":
                    annulerProgrammationEvaluation(request, response);
                    break;
                case "/admin/resultats-evaluations/end-evaluation":
                    mettreFinEvaluation(request, response);
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
            String role = utilisateur.getRole();
            System.out.println("DEBUG LOGIN: User=" + utilisateur.getemail() + ", Role='" + role + "', Redirecting to: /app/" + role);
            response.sendRedirect(request.getContextPath() + "/app/" + role);
        } else {
            request.setAttribute("error", "email ou mot de passe incorrect");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    private void programmerEvaluation(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        Integer evaluationId = null;
        try {
            evaluationId = Integer.parseInt(request.getParameter("evaluationId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "evaluationId invalide");
            return;
        }

        String dateDebut = request.getParameter("date_debut");
        String timeDebut = request.getParameter("time_debut");
        String dateFin = request.getParameter("date_fin");
        String timeFin = request.getParameter("time_fin");
        String semestreStr = request.getParameter("semestre");

        if (dateDebut == null || dateDebut.isEmpty() || timeDebut == null || timeDebut.isEmpty() ||
            dateFin == null || dateFin.isEmpty() || timeFin == null || timeFin.isEmpty() ||
            semestreStr == null || semestreStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/app/admin/resultats-evaluations?evaluationId=" + evaluationId);
            return;
        }

        try {
            // Construire les LocalDateTime
            java.time.LocalDate localDateDebut = java.time.LocalDate.parse(dateDebut);
            java.time.LocalTime localTimeDebut = java.time.LocalTime.parse(timeDebut);
            java.time.LocalDateTime dateTimeDebut = java.time.LocalDateTime.of(localDateDebut, localTimeDebut);

            java.time.LocalDate localDateFin = java.time.LocalDate.parse(dateFin);
            java.time.LocalTime localTimeFin = java.time.LocalTime.parse(timeFin);
            java.time.LocalDateTime dateTimeFin = java.time.LocalDateTime.of(localDateFin, localTimeFin);

            int semestre = Integer.parseInt(semestreStr);

            // Récupérer l'évaluation et la mettre à jour
            model.Evaluation eval = model.Evaluation.trouverParId(evaluationId);
            if (eval != null) {
                eval.setDate_debut(dateTimeDebut);
                eval.setDate_fin(dateTimeFin);
                eval.setSemestre(semestre);
                eval.save();
                System.out.println("DEBUG: Evaluation " + evaluationId + " programmée pour " + dateTimeDebut + " - " + dateTimeFin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/app/admin/resultats-evaluations?evaluationId=" + evaluationId);
    }

    private void annulerProgrammationEvaluation(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        Integer evaluationId = null;
        try {
            evaluationId = Integer.parseInt(request.getParameter("evaluationId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "evaluationId invalide");
            return;
        }

        try {
            // Récupérer l'évaluation et réinitialiser les dates
            model.Evaluation eval = model.Evaluation.trouverParId(evaluationId);
            if (eval != null) {
                // Réinitialiser à des valeurs par défaut (fin du monde)
                java.time.LocalDateTime defaultDate = java.time.LocalDateTime.of(
                    java.time.LocalDate.of(2099, 12, 31),
                    java.time.LocalTime.of(23, 59)
                );
                eval.setDate_debut(defaultDate);
                eval.setDate_fin(defaultDate);
                eval.save();
                System.out.println("DEBUG: Programmation de l'évaluation " + evaluationId + " annulée");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/app/admin/resultats-evaluations?evaluationId=" + evaluationId);
    }

    private void mettreFinEvaluation(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        Integer evaluationId = null;
        try {
            evaluationId = Integer.parseInt(request.getParameter("evaluationId"));
            System.out.println("DEBUG mettreFinEvaluation: evaluationId = " + evaluationId);
        } catch (NumberFormatException e) {
            System.out.println("DEBUG mettreFinEvaluation: ERROR parsing evaluationId");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("evaluationId invalide");
            return;
        }

        try {
            // Récupérer l'évaluation et mettre à jour la date de fin à une date passée (maintenant -1 minute)
            model.Evaluation eval = model.Evaluation.trouverParId(evaluationId);
            System.out.println("DEBUG mettreFinEvaluation: eval retrieved = " + (eval != null ? "YES" : "NULL"));
            if (eval != null) {
                java.time.LocalDateTime endTime = java.time.LocalDateTime.now().minusMinutes(1);
                eval.setDate_fin(endTime);
                eval.save();
                System.out.println("DEBUG: Fin de l'évaluation " + evaluationId + " mise à jour à " + endTime);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("OK");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Evaluation non trouvée");
            }
        } catch (Exception e) {
            System.out.println("DEBUG mettreFinEvaluation: EXCEPTION " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erreur serveur: " + e.getMessage());
        }
    }
}