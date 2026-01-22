package controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            case "/admin/specialites":
                if (!Role.estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = Specialite.afficherSpecialites(request);
                break;
            case "/admin/matieres":
                if (!Role.estAdmin(request.getSession(false))) {
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
                if (!estEtudiant(request.getSession(false))) {
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
                if (!Role.estAdmin(request.getSession(false))) {
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
}