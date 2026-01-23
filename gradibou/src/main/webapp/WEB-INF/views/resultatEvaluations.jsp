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
        <!-- Sélecteur d'évaluation -->
        <div class="eva-selector-container">
            <%
                java.util.List<java.util.Map<String, Object>> evalStatusList = (java.util.List<java.util.Map<String, Object>>) request.getAttribute("evalStatusList");
                Integer currentEvalId = (Integer) request.getAttribute("currentEvalId");
                
                if (evalStatusList != null && !evalStatusList.isEmpty()) {
                    for (java.util.Map<String, Object> statusMap : evalStatusList) {
                        model.Evaluation eval = (model.Evaluation) statusMap.get("eval");
                        String status = (String) statusMap.get("status");
                        boolean isOngoing = (Boolean) statusMap.get("isOngoing");
                        boolean isSelected = currentEvalId != null && currentEvalId == eval.getId();
                        String statusClass = isOngoing ? "in-progress" : "";
                        String selectedClass = isSelected ? "selected" : "";
            %>
                <a href="<%= request.getContextPath() %>/app/admin/resultats-evaluations?evaluationId=<%= eval.getId() %>" 
                   class="eva-selector-card <%= statusClass %> <%= selectedClass %>">
                    <div class="eva-selector-date">
                        <%= java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(eval.getDate_debut()) %>
                    </div>
                    <div class="eva-selector-status">
                        <% if (status.equals("ongoing")) { %>
                            <span class="eva-status-badge-small ongoing">En cours</span>
                        <% } else if (status.equals("scheduled")) { %>
                            <span class="eva-status-badge-small scheduled">Programmée</span>
                        <% } else { %>
                            <span class="eva-status-badge-small finished">Terminée</span>
                        <% } %>
                    </div>
                    <div class="eva-selector-semester">Sem. <%= eval.getSemestre() %></div>
                </a>
            <%
                    }
                }
            %>
        </div>

        <section class="eva-program-section">
            <div class="eva-program-header">
                <h1 class="eva-program-title">Evaluation des enseignements</h1>
                <!-- DEBUG -->
                <div style="font-size: 12px; color: #999;">
                    currentEvalId: <%= request.getAttribute("currentEvalId") %>
                </div>
                <!-- /DEBUG -->
                <%
                    // Déclaration des variables pour l'affichage du formulaire
                    String displayedEvaStatus = (String) request.getAttribute("displayedEvaStatus");
                    model.Evaluation displayedEval = (model.Evaluation) request.getAttribute("displayedEval");
                %>
                <div class="eva-actions-row">
                    <button class="btn btn-tertiary btn-with-icon">
                        <img src="<%= request.getContextPath() %>/static/icons/black/trash.svg" alt="">
                        Supprimer les données
                    </button>
                    <button class="btn btn-tertiary btn-with-icon">
                        <img src="<%= request.getContextPath() %>/static/icons/black/file-export.svg" alt="">
                        Exporter tout
                    </button>
                    <%
                        if (displayedEvaStatus != null && displayedEvaStatus.equals("scheduled")) {
                    %>
                    <div class="eva-status-badge scheduled">
                        <img src="<%= request.getContextPath() %>/static/icons/white/calendar-check.svg" alt="">
                        Programmée
                    </div>
                    <%
                        } else if (displayedEvaStatus != null && displayedEvaStatus.equals("ongoing")) {
                    %>
                    <div class="eva-status-badge ongoing">
                        <img src="<%= request.getContextPath() %>/static/icons/white/pause.svg" alt="">
                        En cours
                    </div>
                    <%
                        } else {
                    %>
                    <div class="eva-status-badge stopped">
                        <img src="<%= request.getContextPath() %>/static/icons/white/pause.svg" alt="">
                        Terminée
                    </div>
                    <%
                        }
                    %>
                </div>
            </div>

            <form class="eva-form" id="programForm" method="POST" action="<%= request.getContextPath() %>/app/admin/resultats-evaluations/program" style="display: none;">
                <input type="hidden" name="evaluationId" value="<%= displayedEval != null ? displayedEval.getId() : "" %>">
                <input type="hidden" name="date_debut" id="hidden_date_debut" value="">
                <input type="hidden" name="time_debut" id="hidden_time_debut" value="">
                <input type="hidden" name="date_fin" id="hidden_date_fin" value="">
                <input type="hidden" name="time_fin" id="hidden_time_fin" value="">
                <input type="hidden" name="semestre" id="hidden_semestre" value="">
            </form>

            <form class="eva-form" id="mainForm">
                <%
                    boolean isDisabled = displayedEvaStatus != null && (displayedEvaStatus.equals("scheduled") || displayedEvaStatus.equals("ongoing"));
                    boolean showFormData = displayedEval != null;
                %>
                
                <div class="eva-form-row">
                    <div class="eva-input-group">
                        <label class="eva-input-label">Date de Début</label>
                        <input type="date" class="eva-input" id="input_date_debut" placeholder="--/--/----" 
                            <% if (isDisabled) { %>disabled<% } %>
                            <% if (showFormData && displayedEval != null) { %>value="<%= displayedEval.getDate_debut().toLocalDate() %>"<% } %>>
                    </div>
                    <div class="eva-input-group">
                        <label class="eva-input-label">Heure de Début</label>
                        <input type="time" class="eva-input" id="input_time_debut" placeholder="--:--" 
                            <% if (isDisabled) { %>disabled<% } %>
                            <% if (showFormData && displayedEval != null) { %>value="<%= displayedEval.getDate_debut().toLocalTime() %>"<% } %>>
                    </div>
                </div>

                <div class="eva-form-row">
                    <div class="eva-input-group">
                        <label class="eva-input-label">Date de Fin</label>
                        <input type="date" class="eva-input" id="input_date_fin" placeholder="--/--/----" 
                            <% if (isDisabled) { %>disabled<% } %>
                            <% if (showFormData && displayedEval != null) { %>value="<%= displayedEval.getDate_fin().toLocalDate() %>"<% } %>>
                    </div>
                    <div class="eva-input-group">
                        <label class="eva-input-label">Heure de Fin</label>
                        <input type="time" class="eva-input" id="input_time_fin" placeholder="--:--" 
                            <% if (isDisabled) { %>disabled<% } %>
                            <% if (showFormData && displayedEval != null) { %>value="<%= displayedEval.getDate_fin().toLocalTime() %>"<% } %>>
                    </div>
                </div>

                <div class="eva-input-group">
                    <label class="eva-input-label">Semestre à évaluer</label>
                    <select class="eva-select" id="input_semestre" <% if (isDisabled) { %>disabled<% } %>>
                        <option value="1" <% if (showFormData && displayedEval != null && displayedEval.getSemestre() == 1) { %>selected<% } %>>Semestre 1</option>
                        <option value="2" <% if (showFormData && displayedEval != null && displayedEval.getSemestre() == 2) { %>selected<% } %>>Semestre 2</option>
                    </select>
                </div>

                <div style="display: flex; justify-content: center; gap: 12px;">
                    <% if (displayedEvaStatus != null && displayedEvaStatus.equals("scheduled") && displayedEval != null) { %>
                        <button type="button" class="btn btn-primary btn-with-icon" onclick="annulerProgrammationEvaluation(<%= displayedEval.getId() %>)">
                            <img src="<%= request.getContextPath() %>/static/icons/white/trash.svg" alt="">
                            Annuler la programmation
                        </button>
                        <button type="button" class="btn btn-danger btn-with-icon" onclick="supprimerEvaluation(<%= displayedEval.getId() %>)">
                            <img src="<%= request.getContextPath() %>/static/icons/white/trash.svg" alt="">
                            Supprimer l'évaluation
                        </button>
                    <% } else if (displayedEvaStatus != null && displayedEvaStatus.equals("ongoing") && displayedEval != null) { %>
                        <button type="button" class="btn btn-primary btn-with-icon" onclick="mettreFinEvaluation(<%= displayedEval.getId() %>)">
                            <img src="<%= request.getContextPath() %>/static/icons/white/pause.svg" alt="">
                            Mettre fin à la période d'évaluation
                        </button>
                    <% } else { %>
                        <button type="button" class="btn btn-primary btn-with-icon" onclick="submitProgramForm()">
                            <img src="<%= request.getContextPath() %>/static/icons/white/calendar-check.svg" alt="">
                            Programmer
                        </button>
                    <% } %>
                </div>
            </form>

            <script>
                function submitProgramForm() {
                    document.getElementById('hidden_date_debut').value = document.getElementById('input_date_debut').value;
                    document.getElementById('hidden_time_debut').value = document.getElementById('input_time_debut').value;
                    document.getElementById('hidden_date_fin').value = document.getElementById('input_date_fin').value;
                    document.getElementById('hidden_time_fin').value = document.getElementById('input_time_fin').value;
                    document.getElementById('hidden_semestre').value = document.getElementById('input_semestre').value;
                    document.getElementById('programForm').submit();
                }

                function mettreFinEvaluation(evaluationId) {
                    if (!confirm('Êtes-vous sûr de vouloir mettre fin à cette évaluation ?')) {
                        return;
                    }

                    fetch('<%= request.getContextPath() %>/app/admin/resultats-evaluations/end-evaluation', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: 'evaluationId=' + evaluationId
                    })
                    .then(response => {
                        if (!response.ok) {
                            return response.text().then(text => {
                                throw new Error('Erreur ' + response.status + ': ' + text);
                            });
                        }
                        return response.text();
                    })
                    .then(() => {
                        window.location.reload();
                    })
                    .catch(error => {
                        alert('Erreur lors de la mise à jour: ' + error.message);
                        console.error('Erreur:', error);
                    });
                }

                function annulerProgrammationEvaluation(evaluationId) {
                    if (!confirm('Êtes-vous sûr de vouloir annuler la programmation de cette évaluation ?')) {
                        return;
                    }

                    fetch('<%= request.getContextPath() %>/app/admin/resultats-evaluations/cancel-program', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: 'evaluationId=' + evaluationId
                    })
                    .then(response => {
                        if (!response.ok) {
                            return response.text().then(text => {
                                throw new Error('Erreur ' + response.status + ': ' + text);
                            });
                        }
                        return response.text();
                    })
                    .then(() => {
                        window.location.reload();
                    })
                    .catch(error => {
                        alert('Erreur lors de l\'annulation: ' + error.message);
                        console.error('Erreur:', error);
                    });
                }

                function supprimerEvaluation(evaluationId) {
                    if (!confirm('ATTENTION : Êtes-vous sûr de vouloir SUPPRIMER définitivement cette évaluation et toutes ses données ? Cette action est irréversible.')) {
                        return;
                    }

                    fetch('<%= request.getContextPath() %>/app/admin/resultats-evaluations/delete-evaluation', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: 'evaluationId=' + evaluationId
                    })
                    .then(response => {
                        if (!response.ok) {
                            return response.text().then(text => {
                                throw new Error('Erreur ' + response.status + ': ' + text);
                            });
                        }
                        return response.text();
                    })
                    .then(() => {
                        window.location.reload();
                    })
                    .catch(error => {
                        alert('Erreur lors de la suppression: ' + error.message);
                        console.error('Erreur:', error);
                    });
                }
            </script>
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
    // Valeur de satisfaction (0-100)
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
