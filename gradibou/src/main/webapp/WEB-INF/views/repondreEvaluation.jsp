<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Evaluation" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Répondre à une Évaluation</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/repondre-evaluation.css">
    <%-- IMPORTANT: Design Figma converti pour notre stack JSP + CSS tokens --%>
    <% Evaluation eval = (Evaluation) request.getAttribute("evaluation"); %>
    <% Matiere matiere = (Matiere) request.getAttribute("matiere"); %>
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>

    <% if (request.getAttribute("success") != null) { %>
        <div class="re-message"><div class="success"><%= request.getAttribute("success") %></div></div>
        <div class="content-centered"><a href="<%= request.getContextPath() %>/app/etudiant/evaluations" class="btn btn-secondary">Retour aux évaluations</a></div>
    <% } else { %>
    <main class="re-main">
        <% if (eval != null && matiere != null) { %>
        <section class="re-header-card">
            <div class="sem-chip">S<%= eval.getSemestre() %></div>
            <h1 class="re-title"><%= matiere.getNom().toUpperCase() %></h1>
        </section>

        <% if (request.getAttribute("error") != null) { %>
            <div class="re-message"><div class="error"><%= request.getAttribute("error") %></div></div>
        <% } %>

        <form class="re-form" method="post" action="<%= request.getContextPath() %>/app/etudiant/repondre-evaluation">
            <input type="hidden" name="evaluationId" value="<%= request.getAttribute("evaluationId") %>">
            <input type="hidden" name="matiereId" value="<%= request.getAttribute("matiereId") %>">

            <div class="re-group">
                <label class="re-label">Qualité des supports pédagogiques</label>
                <div class="pill-group">
                    <% for (int i = 0; i <= 5; i++) { %>
                        <input class="pill-input" type="radio" id="qs-<%= i %>" name="qualite_support" value="<%= i %>">
                        <label class="pill" for="qs-<%= i %>"><%= i %></label>
                    <% } %>
                </div>
            </div>

            <div class="re-group">
                <label class="re-label">Qualité de l'équipe pédagogique</label>
                <div class="pill-group">
                    <% for (int i = 0; i <= 5; i++) { %>
                        <input class="pill-input" type="radio" id="qe-<%= i %>" name="qualite_equipe" value="<%= i %>">
                        <label class="pill" for="qe-<%= i %>"><%= i %></label>
                    <% } %>
                </div>
            </div>

            <div class="re-group">
                <label class="re-label">Qualité du matériel mis à disposition</label>
                <div class="pill-group">
                    <% for (int i = 0; i <= 5; i++) { %>
                        <input class="pill-input" type="radio" id="qm-<%= i %>" name="qualite_materiel" value="<%= i %>">
                        <label class="pill" for="qm-<%= i %>"><%= i %></label>
                    <% } %>
                </div>
            </div>

            <div class="re-group">
                <label class="re-label">Pertinence des examens</label>
                <div class="pill-group">
                    <% for (int i = 0; i <= 5; i++) { %>
                        <input class="pill-input" type="radio" id="px-<%= i %>" name="pertinence_examen" value="<%= i %>">
                        <label class="pill" for="px-<%= i %>"><%= i %></label>
                    <% } %>
                </div>
            </div>

            <div class="re-group">
                <label class="re-label">Temps par semaine consacré à l’enseignement</label>
                <div class="pill-list">
                    <input class="pill-input" type="radio" id="ts-1" name="temps_par_semaine" value="1">
                    <label class="pill pill-block" for="ts-1">&lt;1h</label>
                    <input class="pill-input" type="radio" id="ts-2" name="temps_par_semaine" value="2">
                    <label class="pill pill-block" for="ts-2">1h à 2h</label>
                    <input class="pill-input" type="radio" id="ts-3" name="temps_par_semaine" value="3">
                    <label class="pill pill-block" for="ts-3">2h à 4h</label>
                    <input class="pill-input" type="radio" id="ts-4" name="temps_par_semaine" value="4">
                    <label class="pill pill-block" for="ts-4">4h à 6h</label>
                    <input class="pill-input" type="radio" id="ts-5" name="temps_par_semaine" value="5">
                    <label class="pill pill-block" for="ts-5">&gt;6h</label>
                </div>
            </div>

            <div class="re-group">
                <label class="re-label">Pensez-vous que l’enseignement apporte quelque chose d’utile à votre formation</label>
                <div class="pill-group">
                    <input class="pill-input" type="radio" id="uf-oui" name="utilite_pour_formation" value="1">
                    <label class="pill" for="uf-oui">Oui</label>
                    <input class="pill-input" type="radio" id="uf-non" name="utilite_pour_formation" value="0">
                    <label class="pill" for="uf-non">Non</label>
                </div>
            </div>

            <div class="re-group">
                <label class="re-label" for="commentaires">Vous pouvez rajouter un commentaire global</label>
                <textarea id="commentaires" class="input-field" name="commentaires" placeholder="Ne renseignez pas d’informations personnelles"></textarea>
                <div class="form-feedback" style="display:none;">Ce champ est invalide</div>
            </div>

            <div class="content-centered">
                <button type="submit" class="btn btn-primary re-submit">Envoyer ma réponse →</button>
            </div>
        </form>
        <% } %>
        <div class="content-centered"><a href="<%= request.getContextPath() %>/app/etudiant/evaluations" class="btn btn-secondary">Retour aux évaluations</a></div>
    </main>
    <% } %>
</div>
</body>
</html>
