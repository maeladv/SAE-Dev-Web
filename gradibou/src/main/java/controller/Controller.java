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
                view = "/WEB-INF/views/login.jsp";
                break;
            case "/admin":
                if (!estAdmin(request.getSession(false))) {
                    response.sendRedirect(request.getContextPath() + "/app/login");
                    return;
                }
                view = "/WEB-INF/views/admin.jsp";
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

        try {
            request.getRequestDispatcher(view).forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
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
                case "/admin/maj-mdp":
                    creerLienPourMAJMotDePasse(request, response);
                    break;
                case "/complete-profil":
                    completerProfil(request, response);
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
            request.setAttribute("error", "Email et mot de passe requis");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        Utilisateur utilisateur = Utilisateur.trouverParEmailEtMotDePasse(email, motDePasse);
        if (utilisateur != null) {
            request.getSession().setAttribute("user", utilisateur);
            response.sendRedirect(request.getContextPath() + "/app/" + utilisateur.getRole());
        } else {
            request.setAttribute("error", "Email ou mot de passe incorrect");
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
        String coefficientStr = request.getParameter("coefficient");
        String specialiteIdStr = request.getParameter("specialiteId");
        String profIdStr = request.getParameter("profId");

        try {
            // Check if all fields (including profId) are present
            if (nom == null || nom.isEmpty() || semestreStr == null || semestreStr.isEmpty() || 
                coefficientStr == null || coefficientStr.isEmpty() || specialiteIdStr == null || specialiteIdStr.isEmpty() ||
                profIdStr == null || profIdStr.isEmpty()) {
                request.setAttribute("error", "Tous les champs sont requis, y compris le professeur.");
                request.getRequestDispatcher("/WEB-INF/views/creerMatiere.jsp").forward(request, response);
                return;
            }

            int semestre = Integer.parseInt(semestreStr);
            int coefficient = Integer.parseInt(coefficientStr);
            int specialiteId = Integer.parseInt(specialiteIdStr);
            int profId = Integer.parseInt(profIdStr);

            model.Matiere matiere = new model.Matiere(nom, semestre, coefficient, specialiteId, profId);
            
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

    public void creerLienPourMAJMotDePasse(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        if (!estAdmin(request.getSession(false))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String mailUtilisateur = request.getParameter("email");
        if (mailUtilisateur == null || mailUtilisateur.isEmpty()) {
            envoyerJsonError(response, "Email utilisateur requis", 400);
            return;
        }

        Utilisateur utilisateur = Utilisateur.trouverParEmail(mailUtilisateur);
        if (utilisateur == null) {
            envoyerJsonError(response, "Utilisateur non trouvé", 404);
            return;
        }

        try {
            int userId = utilisateur.getId();

            String token = model.Lien.creerLien(userId, 1); // Lien valide 1 jour
            String lienMDP = request.getContextPath() + "/app/complete-profil?token=" + token;

            envoyerJsonSuccess(response, "Lien créé avec succès", lienMDP);
        } catch (NumberFormatException e) {
            envoyerJsonError(response, "ID utilisateur invalide", 400);
        } catch (Exception e) {
            envoyerJsonError(response, "Erreur: " + e.getMessage(), 500);
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
}