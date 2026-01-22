<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Locale" %>
<%@ page import="model.Evaluation" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Évaluation des enseignements - Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/resultats-evaluations.css">
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>

    <main class="re-admin-main">
        <section class="eva-program-section">
            <div class="eva-program-header">
                <h1 class="eva-program-title">Evaluation des enseignements</h1>
                <div class="eva-actions-row">
                    <button class="btn btn-tertiary btn-with-icon">
                        <img src="<%= request.getContextPath() %>/static/icons/black/trash.svg" alt="">
                        Supprimer les données
                    </button>
                    <button class="btn btn-tertiary btn-with-icon">
                        <img src="<%= request.getContextPath() %>/static/icons/black/file-export.svg" alt="">
                        Exporter tout
                    </button>
                    <div class="eva-status-badge stopped">
                        <img src="<%= request.getContextPath() %>/static/icons/white/pause.svg" alt="">
                        La campagne est à l'arrêt
                    </div>
                </div>
            </div>

            <form class="eva-form">
                <div class="eva-form-row">
                    <div class="eva-input-group">
                        <label class="eva-input-label">Date de Début</label>
                        <input type="date" class="eva-input" placeholder="--/--/----">
                    </div>
                    <div class="eva-input-group">
                        <label class="eva-input-label">Heure de Début</label>
                        <input type="time" class="eva-input" placeholder="--:--">
                    </div>
                </div>

                <div class="eva-form-row">
                    <div class="eva-input-group">
                        <label class="eva-input-label">Date de Fin</label>
                        <input type="date" class="eva-input" placeholder="--/--/----">
                    </div>
                    <div class="eva-input-group">
                        <label class="eva-input-label">Heure de Fin</label>
                        <input type="time" class="eva-input" placeholder="--:--">
                    </div>
                </div>

                <div class="eva-input-group">
                    <label class="eva-input-label">Semestre à évaluer</label>
                    <select class="eva-select">
                        <option value="1">Semestre 1</option>
                        <option value="2">Semestre 2</option>
                    </select>
                </div>

                <div style="display: flex; justify-content: center;">
                    <button type="submit" class="btn btn-primary btn-with-icon" style="width: 216px;">
                        <img src="<%= request.getContextPath() %>/static/icons/white/calendar-check.svg" alt="">
                        Programmer
                    </button>
                </div>
            </form>
        </section>

        <div class="eva-stats-grid">
            <div class="eva-stat-card eva-stat-large">
                <div>
                    <span class="eva-stat-value-big"><%= request.getAttribute("responseRate") != null ? (Integer) request.getAttribute("responseRate") : 0 %></span><span class="eva-stat-unit">%</span>
                </div>
                <div class="eva-stat-label">Taux de réponse</div>
            </div>

            <div class="eva-stat-card eva-stat-double">
                <div class="eva-stat-item">
                    <div class="eva-stat-info">
                        <div class="eva-stat-title">Spécialité la plus investie</div>
                        <div class="spe-badge icy"><%
                            Map most = (Map) request.getAttribute("mostInvested");
                            out.print(most != null && most.get("tag") != null ? most.get("tag") : "-");
                        %></div>
                    </div>
                    <div>
                        <span class="eva-stat-value-medium"><%
                            Map most2 = (Map) request.getAttribute("mostInvested");
                            Integer ratePlus = (most2 != null && most2.get("rate") != null) ? (Integer) most2.get("rate") : 0;
                            out.print(ratePlus);
                        %></span><span class="eva-stat-unit">%</span>
                    </div>
                </div>
                <div class="eva-stat-item">
                    <div class="eva-stat-info">
                        <div class="eva-stat-title">Spécialité la moins investie</div>
                        <div class="spe-badge avm"><%
                            Map least = (Map) request.getAttribute("leastInvested");
                            out.print(least != null && least.get("tag") != null ? least.get("tag") : "-");
                        %></div>
                    </div>
                    <div>
                        <span class="eva-stat-value-medium"><%
                            Map least2 = (Map) request.getAttribute("leastInvested");
                            Integer rateMoins = (least2 != null && least2.get("rate") != null) ? (Integer) least2.get("rate") : 0;
                            out.print(rateMoins);
                        %></span><span class="eva-stat-unit">%</span>
                    </div>
                </div>
            </div>

            <div class="eva-stat-card eva-stat-image-card">
                <canvas id="satisfactionChart" class="eva-chart" width="159" height="122" aria-label="Taux de satisfaction"></canvas>
                <div class="eva-stat-title">Taux de satisfaction Général</div>
            </div>

            <div class="eva-stat-card eva-stat-double">
                <div class="eva-stat-item">
                    <div class="eva-stat-info">
                        <div class="eva-stat-title">Retours les plus négatifs</div>
                        <div class="spe-badge icy"><%
                            Map neg = (Map) request.getAttribute("negativeReturn");
                            out.print(neg != null && neg.get("tag") != null ? neg.get("tag") : "-");
                        %></div>
                    </div>
                    <div>
                        <span class="eva-stat-value-medium"><%
                            Map neg2 = (Map) request.getAttribute("negativeReturn");
                            Double avgNeg = (neg2 != null && neg2.get("avg") != null) ? (Double) neg2.get("avg") : 0.0;
                            out.print(String.format(Locale.FRANCE, "%.1f", avgNeg));
                        %></span><span class="eva-stat-unit">/5</span>
                    </div>
                </div>
                <div class="eva-stat-item">
                    <div class="eva-stat-info">
                        <div class="eva-stat-title">Retours les plus positifs</div>
                        <div class="spe-badge me"><%
                            Map pos = (Map) request.getAttribute("positiveReturn");
                            out.print(pos != null && pos.get("tag") != null ? pos.get("tag") : "-");
                        %></div>
                    </div>
                    <div>
                        <span class="eva-stat-value-medium"><%
                            Map pos2 = (Map) request.getAttribute("positiveReturn");
                            Double avgPos = (pos2 != null && pos2.get("avg") != null) ? (Double) pos2.get("avg") : 0.0;
                            out.print(String.format(Locale.FRANCE, "%.1f", avgPos));
                        %></span><span class="eva-stat-unit">/5</span>
                    </div>
                </div>
            </div>

            <div class="eva-stat-card">
                <div class="eva-stat-info">
                    <div class="eva-stat-title">Spécialité avec le plus de réponses texte</div>
                    <div class="spe-badge icy"><%
                        String tagMostText = (String) request.getAttribute("mostTextResponsesTag");
                        out.print(tagMostText != null ? tagMostText : "-");
                    %></div>
                </div>
            </div>
        </div>

        <div class="eva-specialty-list">
            <div class="eva-specialty-item">
                <div class="spe-badge icy">ICY</div>
                <div class="year-badge">3A</div>
                <div class="eva-specialty-name">Informatique et Cybersécurité</div>
                <div class="eva-specialty-score">1,8/5</div>
                <a href="<%= request.getContextPath() %>/app/admin/resultats-evaluation?evaluationId=1" class="btn btn-primary eva-specialty-btn">
                    Voir les résultats
                    <img src="<%= request.getContextPath() %>/static/icons/white/caret-right.svg" alt="">
                </a>
            </div>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
    // Valeur de satisfaction (0-100). TODO: brancher sur données backend.
    const satisfaction = <%= request.getAttribute("satisfactionPercent") != null ? (Integer) request.getAttribute("satisfactionPercent") : 0 %>; // %
    const ctx = document.getElementById('satisfactionChart');
    if (ctx) {
        const chart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Satisfaction', 'Reste'],
                datasets: [{
                    data: [satisfaction, 100 - satisfaction],
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
    <style>
        body { 
            font-family: Arial, sans-serif; 
            max-width: 1200px; 
            margin: 50px auto; 
            padding: 20px;
            background-color: #f5f5f5;
        }
        .card { 
            border: 1px solid #ddd; 
            padding: 20px; 
            border-radius: 6px; 
            box-shadow: 0 1px 3px rgba(0,0,0,0.1); 
            margin-bottom: 20px;
            background-color: #fff;
        }
        h1, h2 { color: #333; }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        table thead {
            background-color: #f8f9fa;
        }
        table th, table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        table th {
            font-weight: bold;
            background-color: #f8f9fa;
        }
        table tr:hover {
            background-color: #f9f9f9;
        }
        .btn {
            display: inline-block;
            padding: 8px 14px;
            background-color: #0d6efd;
            color: #fff;
            text-decoration: none;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        .btn:hover {
            background-color: #0b5ed7;
        }
        .btn-secondary {
            background-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        .stats-badge {
            display: inline-block;
            padding: 5px 10px;
            background-color: #e7f3ff;
            border-left: 4px solid #2196F3;
            border-radius: 3px;
            margin: 5px 0;
        }
        .no-evaluations {
            text-align: center;
            color: #999;
            padding: 40px 20px;
        }
        .back-button {
            display: inline-block;
            margin-top: 15px;
            padding: 10px 14px;
            background: #6c757d;
            color: #fff;
            text-decoration: none;
            border-radius: 4px;
        }
        .back-button:hover {
            background: #5a6268;
        }
    </style>
</head>
<body>
    <h1>Résultats des Évaluations</h1>
    
    <div class="card">
        <h2>Liste des évaluations</h2>
        
        <%
            List<Evaluation> evaluations = (List<Evaluation>) request.getAttribute("evaluations");
            
            if (evaluations != null && !evaluations.isEmpty()) {
        %>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Date de début</th>
                        <th>Date de fin</th>
                        <th>Semestre</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (Evaluation eval : evaluations) {
                    %>
                    <tr>
                        <td><%= eval.getId() %></td>
                        <td><%= eval.getDate_debut() %></td>
                        <td><%= eval.getDate_fin() %></td>
                        <td><%= eval.getSemestre() %></td>
                        <td>
                            <a href="<%= request.getContextPath() %>/app/admin/resultats-evaluation?evaluationId=<%= eval.getId() %>" class="btn">
                                Voir les résultats
                            </a>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        <%
            } else {
        %>
            <div class="no-evaluations">
                <p>Aucune évaluation disponible.</p>
            </div>
        <%
            }
        %>
    </div>

    <a href="<%= request.getContextPath() %>/app/admin" class="back-button">Retour au tableau de bord</a>
</body>
</html>
