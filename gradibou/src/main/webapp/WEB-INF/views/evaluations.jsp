<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Évaluations Disponibles</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            max-width: 1000px; 
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
        .info {
            background-color: #d1ecf1;
            border: 1px solid #bee5eb;
            color: #0c5460;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .error { 
            color: #dc3545; 
            padding: 10px; 
            background: #f8d7da; 
            border-radius: 4px; 
            margin-bottom: 10px; 
        }
        .success { 
            color: #155724; 
            padding: 10px; 
            background: #d4edda; 
            border-radius: 4px; 
            margin-bottom: 10px; 
        }
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
        .evaluation-item {
            margin-bottom: 15px;
            padding: 15px;
            border: 1px solid #e0e0e0;
            border-radius: 4px;
            background-color: #fafafa;
        }
        .evaluation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        .evaluation-title {
            font-weight: bold;
            font-size: 16px;
            color: #333;
        }
        .evaluation-date {
            color: #666;
            font-size: 14px;
        }
        .evaluation-details {
            color: #555;
            font-size: 14px;
            margin: 8px 0;
        }
        .status-badge {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
        }
        .status-open {
            background-color: #d4edda;
            color: #155724;
        }
        .status-done {
            background-color: #d1ecf1;
            color: #0c5460;
        }
        .status-closed {
            background-color: #f8d7da;
            color: #721c24;
        }
        .btn-group {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        button, a.button { 
            padding: 8px 14px; 
            background-color: #0d6efd; 
            color: #fff; 
            border: none; 
            cursor: pointer; 
            border-radius: 4px;
            text-decoration: none;
            font-size: 14px;
        }
        button:hover, a.button:hover { 
            background-color: #0b5ed7; 
        }
        button:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }
        a.button.secondary {
            background-color: #6c757d;
        }
        a.button.secondary:hover {
            background-color: #5a6268;
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
    <h1>Évaluations Disponibles</h1>
    
    <% if (request.getAttribute("error") != null) { %>
        <div class="card">
            <div class="error"><%= request.getAttribute("error") %></div>
        </div>
    <% } %>
    
    <% if (request.getAttribute("success") != null) { %>
        <div class="card">
            <div class="success"><%= request.getAttribute("success") %></div>
        </div>
    <% } %>
    
    <div class="card">
        <div class="info">
            <strong>Instructions :</strong> Vous trouverez ci-dessous toutes les évaluations disponibles pour les matières de votre spécialité. 
            Cliquez sur "Répondre" pour participer à une évaluation.
        </div>

        <%
            java.util.List<Map<String, Object>> evaluations = 
                (java.util.List<Map<String, Object>>) request.getAttribute("evaluations");
            
            if (evaluations != null && !evaluations.isEmpty()) {
                for (Map<String, Object> eval : evaluations) {
        %>
            <div class="evaluation-item">
                <div class="evaluation-header">
                    <div>
                        <div class="evaluation-title"><%= eval.get("matiere_nom") %></div>
                        <div class="evaluation-date">
                            Du <%= eval.get("date_debut") %> au <%= eval.get("date_fin") %>
                        </div>
                    </div>
                    <div class="btn-group">
                        <% 
                            String status = (String) eval.get("status");
                            String statusBadge = "";
                            boolean canAnswer = false;
                            
                            if ("open".equals(status)) {
                                statusBadge = "status-open";
                                canAnswer = true;
                            } else if ("answered".equals(status)) {
                                statusBadge = "status-done";
                                canAnswer = false;
                            } else {
                                statusBadge = "status-closed";
                                canAnswer = false;
                            }
                        %>
                        <span class="status-badge <%= statusBadge %>">
                            <% if ("open".equals(status)) { %>
                                Ouverte
                            <% } else if ("answered".equals(status)) { %>
                                Répondue
                            <% } else { %>
                                Fermée
                            <% } %>
                        </span>
                        <% if (canAnswer) { %>
                            <form method="get" action="<%= request.getContextPath() %>/app/etudiant/repondre-evaluation" 
                                  style="margin: 0; display: flex; gap: 5px;">
                                <input type="hidden" name="evaluationId" value="<%= eval.get("evaluation_id") %>">
                                <input type="hidden" name="matiereId" value="<%= eval.get("matiere_id") %>">
                                <button type="submit">Répondre</button>
                            </form>
                        <% } else if ("answered".equals(status)) { %>
                            <button disabled>Déjà répondu</button>
                        <% } else { %>
                            <button disabled>Évaluation fermée</button>
                        <% } %>
                    </div>
                </div>
                <div class="evaluation-details">
                    <strong>Semestre :</strong> <%= eval.get("semestre") %><br>
                    <strong>Taux de réponse :</strong> <%= eval.get("taux_reponse") %>%
                </div>
            </div>
        <%
                }
            } else {
        %>
            <div class="no-evaluations">
                <p>Aucune évaluation disponible pour le moment.</p>
            </div>
        <%
            }
        %>
    </div>

    <a href="<%= request.getContextPath() %>/app/etudiant" class="back-button">Retour au tableau de bord</a>
</body>
</html>
