<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Specialite" %>
<%@ page import="model.Evaluation" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Locale" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Résultats Spécialité</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/resultats-specialite.css">
    <%
        Specialite spec = (Specialite) request.getAttribute("specialite");
        Evaluation eval = (Evaluation) request.getAttribute("evaluation");
        String evaStatus = (String) request.getAttribute("evaStatus");
        Integer tauxReponseSpec = (Integer) request.getAttribute("tauxReponseSpec");
        Integer satisfactionSpec = (Integer) request.getAttribute("satisfactionSpec");
        Double moyenneSpec = (Double) request.getAttribute("moyenneSpec");
        String matiereBest = (String) request.getAttribute("matiereBest");
        String matiereWorst = (String) request.getAttribute("matiereWorst");
        Double moyenneBest = (Double) request.getAttribute("moyenneBest");
        Double moyenneWorst = (Double) request.getAttribute("moyenneWorst");
        List<Map<String, Object>> matieresAvecRetours = (List<Map<String, Object>>) request.getAttribute("matieresAvecRetours");
        
        if (moyenneSpec == null) moyenneSpec = 0.0;
        if (moyenneBest == null) moyenneBest = 0.0;
        if (moyenneWorst == null) moyenneWorst = 0.0;
    %>
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>

    <main class="spec-main">
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (spec != null) { %>
        <!-- En-tête avec titre et bouton -->
        <div class="spec-header-section">
            <div class="spec-title-block">
                <p class="spec-label">SPÉCIALITÉ</p>
                <h1 class="spec-title"><%= spec.getNom() %></h1>
                <div class="spec-badges">
                    <span class="badge-tag"><%= spec.getTag() %></span>
                    <span class="badge-year"><%= spec.getAnnee() %>A</span>
                </div>
            </div>
            <% if (eval != null) { %>
                <a class="btn btn-primary download-btn" href="#">
                    Télécharger tous les résultats
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <path d="M8 11L4 7h8l-4 4z" fill="white"/>
                        <path d="M14 14H2v-2h12v2z" fill="white"/>
                    </svg>
                </a>
            <% } %>
        </div>

        <!-- Grille de statistiques - 4 cartes -->
        <div class="stats-grid">
            <!-- Carte 1: Taux de réponse -->
            <div class="stat-card-white">
                <div class="stat-big-number"><%= tauxReponseSpec != null ? tauxReponseSpec : 0 %><span class="percent-symbol">%</span></div>
                <div class="stat-caption">Taux de réponse</div>
            </div>
            
            <!-- Carte 2: Matières best/worst -->
            <div class="stat-card-white stat-matieres">
                <div class="matiere-block">
                    <div class="matiere-label">Matière la mieux notée</div>
                    <div class="matiere-name-small"><%= matiereBest != null ? matiereBest.toUpperCase() : "RÉGLEMENTATION GÉNÉRALE SUR LA PROTECTION DES DONNÉES" %></div>
                    <div class="matiere-score"><%= String.format(Locale.FRANCE, "%.1f", moyenneBest) %><span class="score-unit">/5</span></div>
                </div>
                <div class="matiere-separator"></div>
                <div class="matiere-block">
                    <div class="matiere-label">Matière la moins bien notée</div>
                    <div class="matiere-name-small"><%= matiereWorst != null ? matiereWorst.toUpperCase() : "ANALYSE APPLIQUÉE" %></div>
                    <div class="matiere-score"><%= String.format(Locale.FRANCE, "%.1f", moyenneWorst) %><span class="score-unit">/5</span></div>
                </div>
            </div>

            <!-- Carte 3: Satisfaction avec graphique -->
            <div class="stat-card-white stat-with-flag">
                <canvas id="satisfactionSpecChart" class="eva-chart" width="159" height="122" aria-label="Taux de satisfaction"></canvas>
                <div class="stat-caption">Taux de satisfaction Général</div>
            </div>

            <!-- Carte 4: Note générale -->
            <div class="stat-card-white">
                <div class="stat-big-number"><%= String.format(Locale.FRANCE, "%.1f", moyenneSpec) %><span class="score-unit">/5</span></div>
                <div class="stat-caption">Note générale</div>
            </div>
        </div>
        <% } %>

        <!-- Liste des matières -->
        <section class="matieres-section">
            <% if (matieresAvecRetours != null && !matieresAvecRetours.isEmpty()) { %>
                <div class="matieres-list">
                <% for (Map<String, Object> m : matieresAvecRetours) { %>
                    <div class="matiere-item">
                        <span class="semester-badge">S1</span>
                        <span class="matiere-name"><%= m.get("nom") %></span>
                        <span class="matiere-score-display"><%= String.format(Locale.FRANCE, "%.1f", (Double) m.get("moyenne")) %>/5</span>
                        <a class="btn btn-primary btn-sm" href="#">
                            Voir les résultats
                            <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                                <path d="M4 2l4 4-4 4" stroke="white" stroke-width="2"/>
                            </svg>
                        </a>
                    </div>
                <% } %>
                </div>
            <% } else { %>
                <div class="empty-message">Aucun retour pour cette spécialité sur cette évaluation.</div>
            <% } %>
        </section>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
    // Graphique de satisfaction pour la spécialité
    const moyenneSpec = <%= moyenneSpec != null ? moyenneSpec : 0.0 %>;
    const satisfactionPercent = Math.round((moyenneSpec / 5.0) * 100);
    
    const ctx = document.getElementById('satisfactionSpecChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Satisfaction', 'Reste'],
                datasets: [{
                    data: [satisfactionPercent, 100 - satisfactionPercent],
                    backgroundColor: ['#FA467E', '#E0E0E0'],
                    borderWidth: 0,
                }]
            },
            options: {
                responsive: false,
                cutout: '60%',
                rotation: -90,
                circumference: 180,
                plugins: {
                    legend: { display: false },
                    tooltip: { enabled: false }
                }
            }
        });
    }
</script>
</body>
</html>
