package util;

import jakarta.servlet.http.HttpSession;
import model.Utilisateur;

public class Role {
    public static boolean estAdmin(HttpSession session) {
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

    public static boolean estEtudiant(HttpSession session) {
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

    public static boolean estProfesseur(HttpSession session) {
        if (session == null) {
            return false;
        }
        Object userObj = session.getAttribute("user");
        if (userObj instanceof Utilisateur) {
            Utilisateur utilisateur = (Utilisateur) userObj;
            return "professeur".equalsIgnoreCase(utilisateur.getRole());
        }
        return false;
    }
}
