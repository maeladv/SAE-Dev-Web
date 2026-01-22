<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Utilisateur" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Debug Roles</title>
    <style>
        table { border-collapse: collapse; margin: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        .role-value { font-weight: bold; color: red; }
    </style>
</head>
<body>
    <h1>Liste des rôles dans la base de données</h1>
    <table>
        <tr>
            <th>ID</th>
            <th>Email</th>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Rôle (texte exact)</th>
            <th>Rôle (bytes)</th>
        </tr>
        <%
            try {
                List<Utilisateur> utilisateurs = Utilisateur.trouverTousLesUtilisateurs();
                for (Utilisateur u : utilisateurs) {
                    String role = u.getRole();
                    String roleBytes = "";
                    if (role != null) {
                        for (char c : role.toCharArray()) {
                            roleBytes += (int)c + " ";
                        }
                    }
        %>
        <tr>
            <td><%= u.getId() %></td>
            <td><%= u.getEmail() %></td>
            <td><%= u.getNom() %></td>
            <td><%= u.getPrenom() %></td>
            <td class="role-value">'<%= role %>'</td>
            <td><%= roleBytes %></td>
        </tr>
        <%
                }
            } catch (Exception e) {
                out.println("<tr><td colspan='6'>Erreur: " + e.getMessage() + "</td></tr>");
            }
        %>
    </table>
</body>
</html>
