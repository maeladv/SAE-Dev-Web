<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Évaluations des enseignements</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/evaluations.css">
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>

    <% if (request.getAttribute("error") != null) { %>
        <div class="eva-message"><div class="error"><%= request.getAttribute("error") %></div></div>
    <% } %>
    <% if (request.getAttribute("success") != null) { %>
        <div class="eva-message"><div class="success"><%= request.getAttribute("success") %></div></div>
    <% } %>

    <main class="eva-main">
        <section class="eva-hero">
            <h1>Espace d’évaluation des enseignements</h1>
            <p class="subtitle">Retrouvez ici les évaluations disponibles et leurs échéances</p>
        </section>

        <div class="eva-banner">
            Les évaluations des enseignements sont strictement anonymes. Veuillez rester cordiaux et constructifs dans vos retours.
        </div>

        <section class="eva-list">
            <%
                java.util.List<Map<String, Object>> evaluations = 
                    (java.util.List<Map<String, Object>>) request.getAttribute("evaluations");
                if (evaluations != null && !evaluations.isEmpty()) {
                    for (Map<String, Object> eval : evaluations) {
                        String status = (String) eval.get("status");
                        boolean canAnswer = "open".equals(status);
                        boolean answered = "answered".equals(status);
            %>
            <div class="eva-item">
                <div class="eva-sem">S<%= eval.get("semestre") %></div>
                <div class="eva-title"><%= ((String)eval.get("matiere_nom")).toUpperCase() %></div>
                <div class="eva-actions">
                    <% if (canAnswer) { %>
                        <form method="get" action="<%= request.getContextPath() %>/app/etudiant/repondre-evaluation" style="margin:0;">
                            <input type="hidden" name="evaluationId" value="<%= eval.get("evaluation_id") %>">
                            <input type="hidden" name="matiereId" value="<%= eval.get("matiere_id") %>">
                            <button type="submit" class="btn btn-primary">Évaluer →</button>
                        </form>
                    <% } else if (answered) { %>
                        <button class="btn-answered" disabled aria-label="Évaluation déjà envoyée">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                                <path fill-rule="evenodd" d="M16.704 5.29a1 1 0 0 1 .006 1.414l-7.25 7.333a1 1 0 0 1-1.422.012l-3.25-3.188a1 1 0 0 1 1.404-1.424l2.53 2.482 6.541-6.612a1 1 0 0 1 1.441-.017Z" clip-rule="evenodd"/>
                            </svg>
                            Déjà répondu
                        </button>
                    <% } else { %>
                        <button class="btn btn-disabled" disabled>Fermée</button>
                    <% } %>
                </div>
            </div>
            <%      }
                } else { %>
                <div class="no-evaluations">Aucune évaluation disponible pour le moment.</div>
            <% } %>
        </section>
    </main>
</div>
</body>
</html>
