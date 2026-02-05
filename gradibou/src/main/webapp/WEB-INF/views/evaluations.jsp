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

    <%
        java.util.List<Map<String, Object>> evaluations =
            (java.util.List<Map<String, Object>>) request.getAttribute("evaluations");
        java.util.List<Map<String, Object>> openEvaluations = new java.util.ArrayList<>();
        java.util.List<Map<String, Object>> answeredEvaluations = new java.util.ArrayList<>();
        if (evaluations != null) {
            for (Map<String, Object> e : evaluations) {
                Object st = e.get("status");
                if (st == null) continue;
                String s = st.toString();
                if ("open".equals(s)) {
                    openEvaluations.add(e);
                } else if ("answered".equals(s)) {
                    answeredEvaluations.add(e);
                }
            }
        }

        String sessionSubtitle = "Aucune session d'évaluation en cours";
        if (!openEvaluations.isEmpty()) {
            Map<String, Object> firstOpen = openEvaluations.get(0);
            Object sem = firstOpen.get("semestre");
            Object dateFin = firstOpen.get("date_fin");
            sessionSubtitle = "Évaluations en cours pour le SEMESTRE " + sem + " - Vous avez jusqu'au " + dateFin;
        }
    %>

    <main class="eva-main">
        <section class="eva-hero">
            <h1>Espace d’évaluation des enseignements</h1>
            <p class="subtitle"><%= sessionSubtitle %></p>
        </section>

        <div class="eva-banner">
            Les évaluations des enseignements sont strictement anonymes. Veuillez rester cordiaux et constructifs dans vos retours.
        </div>

        <section class="eva-list">
            <%
                if (!openEvaluations.isEmpty()) {
                    for (Map<String, Object> eval : openEvaluations) {
            %>
            <div class="eva-item">
                <div class="eva-sem">S<%= eval.get("semestre") %></div>
                <div class="eva-title"><%= ((String)eval.get("matiere_nom")).toUpperCase() %></div>
                <div class="eva-actions">
                    <form method="get" action="<%= request.getContextPath() %>/app/etudiant/repondre-evaluation" style="margin:0;">
                        <input type="hidden" name="idEvaluation" value="<%= eval.get("evaluation_id") %>">
                        <input type="hidden" name="idmatiere" value="<%= eval.get("matiere_id") %>">
                        <button type="submit" class="btn btn-primary">Évaluer →</button>
                    </form>
                </div>
            </div>
            <%
                    }
                    for (Map<String, Object> eval : answeredEvaluations) {
            %>
            <div class="eva-item">
                <div class="eva-sem">S<%= eval.get("semestre") %></div>
                <div class="eva-title"><%= ((String)eval.get("matiere_nom")).toUpperCase() %></div>
                <div class="eva-actions">
                    <button class="btn btn-success" disabled aria-label="Évaluation déjà envoyée">
                        Déjà répondu
                    </button>
                </div>
            </div>
            <%
                    }
                } else {
            %>
                <div class="no-evaluations">Aucune évaluation en cours pour le moment.</div>
            <%
                }
            %>
        </section>
    </main>
</div>
</body>
</html>
