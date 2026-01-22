<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Evaluation" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html>
<head>
    <title>Répondre à une Évaluation</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            max-width: 900px; 
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
        form { display: flex; flex-direction: column; }
        .form-group {
            margin: 15px 0;
            display: flex;
            flex-direction: column;
        }
        .form-group label {
            font-weight: bold;
            margin-bottom: 5px;
            color: #555;
        }
        input[type="text"], input[type="number"], input[type="datetime-local"], 
        textarea, select { 
            padding: 8px; 
            border: 1px solid #ddd; 
            border-radius: 4px;
            font-size: 14px;
        }
        textarea {
            resize: vertical;
            min-height: 80px;
        }
        .rating-group {
            display: flex;
            gap: 10px;
            margin-top: 5px;
        }
        .rating-group input[type="radio"] {
            margin-right: 5px;
        }
        .rating-group label {
            margin: 0;
            font-weight: normal;
            display: flex;
            align-items: center;
        }
        button { 
            padding: 10px; 
            background-color: #0d6efd; 
            color: #fff; 
            border: none; 
            cursor: pointer; 
            border-radius: 4px;
            font-size: 16px;
            margin-top: 20px;
        }
        button:hover { 
            background-color: #0b5ed7; 
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
        .info {
            background-color: #d1ecf1;
            border: 1px solid #bee5eb;
            color: #0c5460;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .nav-buttons {
            margin-top: 20px;
            display: flex;
            gap: 10px;
        }
        a.button { 
            display: inline-block; 
            padding: 10px 14px; 
            background: #6c757d; 
            color: #fff; 
            text-decoration: none; 
            border-radius: 4px;
        }
        a.button:hover { 
            background: #5a6268; 
        }
        .matiere-info {
            padding: 10px;
            background-color: #e7f3ff;
            border-left: 4px solid #2196F3;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <h1>Répondre à une Évaluation</h1>
    
    <% if (request.getAttribute("error") != null) { %>
        <div class="card">
            <div class="error"><%= request.getAttribute("error") %></div>
        </div>
    <% } %>
    
    <% if (request.getAttribute("success") != null) { %>
        <div class="card">
            <div class="success"><%= request.getAttribute("success") %></div>
            <a href="<%= request.getContextPath() %>/app/etudiant/evaluations" class="button">Retour aux évaluations</a>
        </div>
    <% } else { %>
        <div class="card">
            <div class="info">
                <strong>Instructions :</strong> Veuillez remplir ce formulaire d'évaluation en répondant honnêtement à toutes les questions.
            </div>

            <form method="post" action="<%= request.getContextPath() %>/app/etudiant/repondre-evaluation">
                <input type="hidden" name="evaluationId" value="<%= request.getAttribute("evaluationId") %>">
                <input type="hidden" name="matiereId" value="<%= request.getAttribute("matiereId") %>">

                <%
                    Evaluation eval = (Evaluation) request.getAttribute("evaluation");
                    Matiere matiere = (Matiere) request.getAttribute("matiere");
                    
                    if (eval != null && matiere != null) {
                %>
                    <div class="matiere-info">
                        <strong>Matière :</strong> <%= matiere.getNom() %><br>
                        <strong>Semestre :</strong> <%= eval.getSemestre() %>
                    </div>

                    <!-- Qualité du support -->
                    <div class="form-group">
                        <label>Qualité du support pédagogique (1 = Très mauvais, 5 = Excellent)</label>
                        <div class="rating-group">
                            <% for (int i = 1; i <= 5; i++) { %>
                                <label>
                                    <input type="radio" name="qualite_support" value="<%= i %>" required>
                                    <%= i %>
                                </label>
                            <% } %>
                        </div>
                    </div>

                    <!-- Qualité de l'équipe -->
                    <div class="form-group">
                        <label>Qualité de l'équipe pédagogique (1 = Très mauvais, 5 = Excellent)</label>
                        <div class="rating-group">
                            <% for (int i = 1; i <= 5; i++) { %>
                                <label>
                                    <input type="radio" name="qualite_equipe" value="<%= i %>" required>
                                    <%= i %>
                                </label>
                            <% } %>
                        </div>
                    </div>

                    <!-- Qualité du matériel -->
                    <div class="form-group">
                        <label>Qualité du matériel pédagogique (1 = Très mauvais, 5 = Excellent)</label>
                        <div class="rating-group">
                            <% for (int i = 1; i <= 5; i++) { %>
                                <label>
                                    <input type="radio" name="qualite_materiel" value="<%= i %>" required>
                                    <%= i %>
                                </label>
                            <% } %>
                        </div>
                    </div>

                    <!-- Pertinence de l'examen -->
                    <div class="form-group">
                        <label>Pertinence de l'examen/évaluation (1 = Très mauvais, 5 = Excellent)</label>
                        <div class="rating-group">
                            <% for (int i = 1; i <= 5; i++) { %>
                                <label>
                                    <input type="radio" name="pertinence_examen" value="<%= i %>" required>
                                    <%= i %>
                                </label>
                            <% } %>
                        </div>
                    </div>

                    <!-- Temps par semaine -->
                    <div class="form-group">
                        <label>Temps de travail par semaine (en heures)</label>
                        <div class="rating-group">
                            <label>
                                <input type="radio" name="temps_par_semaine" value="1" required>
                                < 2h
                            </label>
                            <label>
                                <input type="radio" name="temps_par_semaine" value="2">
                                2-4h
                            </label>
                            <label>
                                <input type="radio" name="temps_par_semaine" value="3">
                                4-6h
                            </label>
                            <label>
                                <input type="radio" name="temps_par_semaine" value="4">
                                6-8h
                            </label>
                            <label>
                                <input type="radio" name="temps_par_semaine" value="5">
                                > 8h
                            </label>
                        </div>
                    </div>

                    <!-- Utilité pour la formation -->
                    <div class="form-group">
                        <label>Cette matière vous a-t-elle semblé utile pour votre formation ?</label>
                        <div class="rating-group">
                            <label>
                                <input type="radio" name="utilite_pour_formation" value="1" required>
                                Oui
                            </label>
                            <label>
                                <input type="radio" name="utilite_pour_formation" value="0">
                                Non
                            </label>
                        </div>
                    </div>

                    <!-- Commentaires -->
                    <div class="form-group">
                        <label for="commentaires">Commentaires et suggestions (optionnel)</label>
                        <textarea id="commentaires" name="commentaires" placeholder="Vos commentaires ici..."></textarea>
                    </div>

                    <button type="submit">Soumettre l'évaluation</button>
                <% } %>
            </form>
        </div>

        <div class="nav-buttons">
            <a href="<%= request.getContextPath() %>/app/etudiant/evaluations" class="button">Retour aux évaluations</a>
        </div>
    <% } %>
</body>
</html>
