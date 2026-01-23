<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Evaluation" %>
<%@ page import="model.Specialite" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Locale" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Résultats Matière</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/resultats-matiere.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
    <%
        Matiere matiere = (Matiere) request.getAttribute("matiere");
        Evaluation evaluation = (Evaluation) request.getAttribute("evaluation");
        Specialite specialite = (Specialite) request.getAttribute("specialite");
        Integer tauxReponse = (Integer) request.getAttribute("tauxReponse");
        Double qualiteSupport = (Double) request.getAttribute("qualiteSupport");
        Double qualiteEquipe = (Double) request.getAttribute("qualiteEquipe");
        Double qualiteMateriel = (Double) request.getAttribute("qualiteMateriel");
        Double pertinenceExamen = (Double) request.getAttribute("pertinenceExamen");
        Integer satisfactionUtilite = (Integer) request.getAttribute("satisfactionUtilite");
        Map<String, Integer> repartition = (Map<String, Integer>) request.getAttribute("repartitionOuiNon");
        double[] proportionsTemps = (double[]) request.getAttribute("proportionsTemps");
        String[] commentaires = (String[]) request.getAttribute("commentaires");
        
        if (qualiteSupport == null) qualiteSupport = 0.0;
        if (qualiteEquipe == null) qualiteEquipe = 0.0;
        if (qualiteMateriel == null) qualiteMateriel = 0.0;
        if (pertinenceExamen == null) pertinenceExamen = 0.0;
        if (satisfactionUtilite == null) satisfactionUtilite = 0;
        if (tauxReponse == null) tauxReponse = 0;
        if (proportionsTemps == null) proportionsTemps = new double[5];
        
        int ouiCount = (repartition != null && repartition.get("oui") != null) ? repartition.get("oui") : 0;
        int nonCount = (repartition != null && repartition.get("non") != null) ? repartition.get("non") : 0;
        
        // Calculer la note générale (moyenne de toutes les notes /5)
        double noteGenerale = (qualiteSupport + qualiteEquipe + qualiteMateriel + pertinenceExamen) / 4.0;
    %>
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>

    <main class="matiere-main">
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (matiere != null && specialite != null) { %>
        
        <!-- En-tête avec titre de la spécialité -->
        <div class="spec-header">
            <div class="spec-info">
                <p class="spec-label">SPÉCIALITÉ</p>
                <h1 class="spec-title"><%= specialite.getNom() %></h1>
                <div class="spec-badges">
                    <span class="badge-specialite">ICY</span>
                    <span class="badge-year">3A</span>
                </div>
            </div>
        </div>

        <!-- Ligne matière avec bouton -->
        <div class="matiere-row">
            <span class="semester-badge">S1</span>
            <h2 class="matiere-name"><%= matiere.getNom() %></h2>
            <a href="<%= request.getContextPath() %>/app/admin/resultats-specialite?id_evaluation=<%= evaluation.getId() %>&id_specialite=<%= matiere.getSpecialiteId() %>" 
               class="btn-download">Télécharger les résultats</a>
        </div>

        <!-- Première ligne de stats (3 cartes) -->
        <div class="stats-row-1">
            <!-- Carte 1: Taux de réponse -->
            <div class="stat-card">
                <div class="stat-big-value">
                    <span class="stat-number"><%= tauxReponse %></span>
                    <span class="stat-unit">%</span>
                </div>
                <p class="stat-label">Taux de réponse</p>
            </div>

            <!-- Carte 2: Graphique de satisfaction -->
            <div class="stat-card stat-chart">
                <canvas id="satisfactionChart" width="159" height="122"></canvas>
                <p class="stat-label">Taux de satisfaction Général</p>
            </div>

            <!-- Carte 3: Note générale -->
            <div class="stat-card">
                <div class="stat-big-value">
                    <span class="stat-number"><%= String.format(Locale.US, "%.1f", noteGenerale) %></span>
                    <span class="stat-unit">/5</span>
                </div>
                <p class="stat-label">Note générale</p>
            </div>
        </div>

        <!-- Deuxième ligne de stats (4 cartes) -->
        <div class="stats-row-2">
            <div class="stat-card">
                <div class="stat-big-value">
                    <span class="stat-number"><%= String.format(Locale.US, "%.1f", qualiteSupport) %></span>
                    <span class="stat-unit">/5</span>
                </div>
                <p class="stat-label">Qualité des supports pédagogiques</p>
            </div>

            <div class="stat-card">
                <div class="stat-big-value">
                    <span class="stat-number"><%= String.format(Locale.US, "%.1f", qualiteEquipe) %></span>
                    <span class="stat-unit">/5</span>
                </div>
                <p class="stat-label">Qualité de l'équipe pédagogique</p>
            </div>

            <div class="stat-card">
                <div class="stat-big-value">
                    <span class="stat-number"><%= String.format(Locale.US, "%.1f", qualiteMateriel) %></span>
                    <span class="stat-unit">/5</span>
                </div>
                <p class="stat-label">Qualité du matériel mis-à-disposition</p>
            </div>

            <div class="stat-card">
                <div class="stat-big-value">
                    <span class="stat-number"><%= String.format(Locale.US, "%.1f", pertinenceExamen) %></span>
                    <span class="stat-unit">/5</span>
                </div>
                <p class="stat-label">Pertinence des évaluations</p>
            </div>
        </div>

        <!-- Section temps + graphique répartition -->
        <div class="stats-row-3">
            <!-- Colonne gauche: Temps par semaine -->
            <div class="temps-section">
                <p class="temps-title">Temps par semaine consacré à l'enseignement</p>
                <div class="temps-item">
                    <p class="temps-label">&lt;1h</p>
                    <div class="temps-value">
                        <span class="temps-number"><%= Math.round(proportionsTemps[0] * 100) %></span>
                        <span class="temps-percent">%</span>
                    </div>
                </div>
                <div class="temps-item">
                    <p class="temps-label">1h à 2h</p>
                    <div class="temps-value">
                        <span class="temps-number"><%= Math.round(proportionsTemps[1] * 100) %></span>
                        <span class="temps-percent">%</span>
                    </div>
                </div>
                <div class="temps-item">
                    <p class="temps-label">2h à 4h</p>
                    <div class="temps-value">
                        <span class="temps-number"><%= Math.round(proportionsTemps[2] * 100) %></span>
                        <span class="temps-percent">%</span>
                    </div>
                </div>
                <div class="temps-item">
                    <p class="temps-label">4h à 6h</p>
                    <div class="temps-value">
                        <span class="temps-number"><%= Math.round(proportionsTemps[3] * 100) %></span>
                        <span class="temps-percent">%</span>
                    </div>
                </div>
                <div class="temps-item">
                    <p class="temps-label">&gt;6h</p>
                    <div class="temps-value">
                        <span class="temps-number"><%= Math.round(proportionsTemps[4] * 100) %></span>
                        <span class="temps-percent">%</span>
                    </div>
                </div>
            </div>

            <!-- Colonne droite: Graphique répartition OUI/NON -->
            <div class="repartition-card">
                <canvas id="repartitionChart" width="294" height="294"></canvas>
                <p class="repartition-label">Pensez-vous que l'enseignement apporte quelque chose d'utile à votre formation</p>
            </div>
        </div>

        <!-- Commentaires -->
        <div class="commentaires-section">
            <h3 class="commentaires-title">Commentaires des étudiants (<%= commentaires != null ? commentaires.length : 0 %>)</h3>
            <% if (commentaires != null && commentaires.length > 0) { %>
                <% for (String commentaire : commentaires) { %>
                <div class="commentaire-card">
                    <p><%= commentaire %></p>
                </div>
                <% } %>
            <% } else { %>
                <p class="no-comments">Aucun commentaire disponible.</p>
            <% } %>
        </div>

        <% } else { %>
        <p>Aucune donnée disponible</p>
        <% } %>
    </main>
</div>

<script>
    // Graphique de satisfaction (doughnut 180°)
    const ctxSatisfaction = document.getElementById('satisfactionChart');
    if (ctxSatisfaction) {
        const satisfactionPercent = <%= satisfactionUtilite %>;
        
        new Chart(ctxSatisfaction, {
            type: 'doughnut',
            data: {
                labels: ['Satisfaction', 'Reste'],
                datasets: [{
                    data: [satisfactionPercent, 100 - satisfactionPercent],
                    backgroundColor: ['#FA467E', '#E0E0E0'],
                    borderWidth: 0
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

    // Graphique de répartition OUI/NON (pie avec légende et %)
    const ctxRepartition = document.getElementById('repartitionChart');
    if (ctxRepartition) {
        const ouiCount = <%= ouiCount %>;
        const nonCount = <%= nonCount %>;
        const total = ouiCount + nonCount;
        const ouiPercent = total > 0 ? Math.round((ouiCount / total) * 100) : 0;
        const nonPercent = total > 0 ? Math.round((nonCount / total) * 100) : 0;
        
        new Chart(ctxRepartition, {
            type: 'pie',
            data: {
                labels: ['OUI', 'NON'],
                datasets: [{
                    data: [ouiCount, nonCount],
                    backgroundColor: [
                        'rgba(119, 164, 100, 1)', // Vert
                        'rgba(189, 82, 90, 1)'     // Rouge
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'bottom',
                        labels: {
                            generateLabels: function(chart) {
                                return [
                                    {
                                        text: 'OUI (' + ouiPercent + '%)',
                                        fillStyle: 'rgba(119, 164, 100, 1)',
                                        strokeStyle: 'rgba(119, 164, 100, 1)',
                                        lineWidth: 0
                                    },
                                    {
                                        text: 'NON (' + nonPercent + '%)',
                                        fillStyle: 'rgba(189, 82, 90, 1)',
                                        strokeStyle: 'rgba(189, 82, 90, 1)',
                                        lineWidth: 0
                                    }
                                ];
                            }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return context.label + ': ' + context.parsed + ' (' + 
                                       (total > 0 ? Math.round((context.parsed / total) * 100) : 0) + '%)';
                            }
                        }
                    }
                }
            }
        });
    }
</script>
</body>
</html>
