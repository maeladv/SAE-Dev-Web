<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Evaluation" %>
<!DOCTYPE html>
<html>
<head>
    <title>Résultats des Évaluations - Admin</title>
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
