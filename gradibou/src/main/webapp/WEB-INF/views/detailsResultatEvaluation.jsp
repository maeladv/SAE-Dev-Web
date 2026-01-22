<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Evaluation" %>
<!DOCTYPE html>
<html>
<head>
    <title>Détails des Résultats - Admin</title>
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
        h1, h2, h3 { color: #333; }
        .stat-box {
            display: inline-block;
            padding: 15px;
            background-color: #e7f3ff;
            border-left: 4px solid #2196F3;
            border-radius: 3px;
            margin: 10px 15px 10px 0;
            min-width: 200px;
        }
        .stat-label {
            font-size: 12px;
            color: #666;
            text-transform: uppercase;
        }
        .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #0066cc;
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
        .section-title {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px solid #ddd;
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
        .info-box {
            background-color: #d1ecf1;
            border-left: 4px solid #17a2b8;
            padding: 15px;
            border-radius: 3px;
            margin-bottom: 20px;
        }
        .progress-bar {
            width: 100%;
            height: 20px;
            background-color: #e0e0e0;
            border-radius: 3px;
            overflow: hidden;
            margin-top: 5px;
        }
        .progress-fill {
            height: 100%;
            background-color: #4caf50;
            transition: width 0.3s;
        }
    </style>
</head>
<body>
    <h1>Détails des Résultats d'Évaluation</h1>
    
    <%
        Evaluation evaluation = (Evaluation) request.getAttribute("evaluation");
        Integer tauxGlobal = (Integer) request.getAttribute("tauxGlobal");
        Double moyenneGlobale = (Double) request.getAttribute("moyenneGlobale");
        java.util.Map<String, Object> specialitePlusMoins = 
            (java.util.Map<String, Object>) request.getAttribute("specialitePlusMoins");
        java.util.Map<String, Object> specialiteStats = 
            (java.util.Map<String, Object>) request.getAttribute("specialiteStats");
        java.util.List<java.util.Map<String, Object>> matiereStats = 
            (java.util.List<java.util.Map<String, Object>>) request.getAttribute("matiereStats");
        
        if (evaluation != null) {
    %>
    
    <div class="card">
        <div class="info-box">
            <strong>Évaluation du semestre <%= evaluation.getSemestre() %></strong><br>
            Du <%= evaluation.getDate_debut() %> au <%= evaluation.getDate_fin() %>
        </div>
        
        <h2>Statistiques Globales</h2>
        
        <div>
            <div class="stat-box">
                <div class="stat-label">Taux de réponse global</div>
                <div class="stat-value"><%= tauxGlobal != null ? tauxGlobal : 0 %>%</div>
            </div>
            
            <div class="stat-box">
                <div class="stat-label">Moyenne générale</div>
                <div class="stat-value"><%= moyenneGlobale != null ? String.format("%.2f", moyenneGlobale) : "0.00" %>/5</div>
            </div>
        </div>
    </div>

    <%
        if (specialitePlusMoins != null) {
            Integer specialitePlusId = (Integer) specialitePlusMoins.get("plusId");
            String specialitePlusNom = (String) specialitePlusMoins.get("plusNom");
            Integer specialiteMoinsId = (Integer) specialitePlusMoins.get("moinsId");
            String specialiteMoinsNom = (String) specialitePlusMoins.get("moinsNom");
    %>
    <div class="card">
        <h2>Spécialités avec le Plus/Moins de Réponses</h2>
        <table>
            <tr>
                <th>Type</th>
                <th>Spécialité</th>
                <th>ID</th>
            </tr>
            <% if (specialitePlusNom != null) { %>
            <tr>
                <td><strong style="color: green;">Plus de réponses</strong></td>
                <td><%= specialitePlusNom %></td>
                <td><%= specialitePlusId %></td>
            </tr>
            <% } %>
            <% if (specialiteMoinsNom != null) { %>
            <tr>
                <td><strong style="color: red;">Moins de réponses</strong></td>
                <td><%= specialiteMoinsNom %></td>
                <td><%= specialiteMoinsId %></td>
            </tr>
            <% } %>
        </table>
    </div>
    <% } %>

    <%
        if (specialiteStats != null && !specialiteStats.isEmpty()) {
    %>
    <div class="card section-title">
        <h2>Résultats par Spécialité</h2>
        
        <%
            for (java.util.Map.Entry<String, Object> entry : specialiteStats.entrySet()) {
                String specialiteNom = entry.getKey();
                java.util.Map<String, Object> stats = (java.util.Map<String, Object>) entry.getValue();
                Integer tauxReponse = (Integer) stats.get("tauxReponse");
                Double moyenne = (Double) stats.get("moyenne");
        %>
        <div style="margin-bottom: 25px;">
            <h3><%= specialiteNom %></h3>
            
            <div class="stat-box">
                <div class="stat-label">Taux de réponse</div>
                <div class="stat-value"><%= tauxReponse %>%</div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: <%= tauxReponse %>%;"></div>
                </div>
            </div>
            
            <div class="stat-box">
                <div class="stat-label">Moyenne générale</div>
                <div class="stat-value"><%= String.format("%.2f", moyenne) %>/5</div>
            </div>
        </div>
        <% } %>
    </div>
    <% } %>

    <%
        if (matiereStats != null && !matiereStats.isEmpty()) {
    %>
    <div class="card section-title">
        <h2>Résultats par Matière</h2>
        
        <table>
            <thead>
                <tr>
                    <th>Matière</th>
                    <th>Taux de réponse</th>
                    <th>Qualité Support</th>
                    <th>Qualité Équipe</th>
                    <th>Qualité Matériel</th>
                    <th>Pertinence Examen</th>
                    <th>Utilité Moyenne</th>
                </tr>
            </thead>
            <tbody>
                <%
                    for (java.util.Map<String, Object> stats : matiereStats) {
                        String matiereNom = (String) stats.get("matiereNom");
                        Integer tauxReponse = (Integer) stats.get("tauxReponse");
                        Double qualiteSupport = (Double) stats.get("qualiteSupport");
                        Double qualiteEquipe = (Double) stats.get("qualiteEquipe");
                        Double qualiteMateriel = (Double) stats.get("qualiteMateriel");
                        Double pertinenceExamen = (Double) stats.get("pertinenceExamen");
                        Double utilitePourFormation = (Double) stats.get("utilitePourFormation");
                %>
                <tr>
                    <td><strong><%= matiereNom %></strong></td>
                    <td><%= tauxReponse %>%</td>
                    <td><%= String.format("%.2f", qualiteSupport) %>/5</td>
                    <td><%= String.format("%.2f", qualiteEquipe) %>/5</td>
                    <td><%= String.format("%.2f", qualiteMateriel) %>/5</td>
                    <td><%= String.format("%.2f", pertinenceExamen) %>/5</td>
                    <td><%= String.format("%.1f%%", utilitePourFormation * 100) %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
    <% } %>
    
    <%
        } else {
    %>
    <div class="card">
        <p>Erreur : évaluation non trouvée.</p>
    </div>
    <% } %>

    <a href="<%= request.getContextPath() %>/app/admin/resultats-evaluations" class="back-button">Retour à la liste</a>
</body>
</html>
