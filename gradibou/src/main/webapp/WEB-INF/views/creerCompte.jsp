<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Créer un compte - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <style>
        .admin-container { max-width: 800px; margin: 30px auto; padding: 20px; }
        .card { border: 1px solid #ddd; padding: 30px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px; background: white; }
        h1 { margin-bottom: 30px; }
        h2 { margin-top: 0; }
        form { display: flex; flex-direction: column; gap: 15px; }
        .error { color: #dc3545; padding: 10px; background: #f8d7da; border-radius: 4px; }
        .success { color: #155724; padding: 10px; background: #d4edda; border-radius: 4px; }
        .link-box { background: #f0f0f0; padding: 10px; border-radius: 4px; word-break: break-all; }
    </style>
</head>
<body>
<div class="page-container">
    <%@ include file="includes/header.jsp" %>
    
    <main class="admin-container">
        <h1>Créer un nouvel utilisateur</h1>
        
        <div class="card">
            <h2>Formulaire de création</h2>
        <div id="responseMessage"></div>

        <form id="creerUtilisateurForm" method="post" action="<%= request.getContextPath() %>/app/admin/creer-utilisateur">
            <input class="input-field" type="text" name="nom" placeholder="Nom" required>
            <input class="input-field" type="text" name="prenom" placeholder="Prénom" required>
            <input class="input-field" type="email" name="email" placeholder="Email" required>
            <input class="input-field" type="date" name="dateNaissance" required>
            
            <select class="input-field" name="role" id="roleSelect" required onchange="toggleINE()">
                <option value="">-- Choisir un rôle --</option>
                <option value="etudiant">Étudiant</option>
                <option value="professeur">Professeur</option>
                <option value="admin">Admin</option>
            </select>
            
            <input class="input-field" type="text" name="ine" id="ineField" placeholder="INE (obligatoire pour les étudiants)" style="display:none;">
            
            <button class="btn btn-primary" type="submit">Créer l'utilisateur</button>
        </form>
    </div>

    <script>
        function toggleINE() {
            var roleSelect = document.getElementById('roleSelect');
            var ineField = document.getElementById('ineField');
            
            if (roleSelect.value === 'etudiant') {
                ineField.style.display = 'block';
                ineField.required = true;
            } else {
                ineField.style.display = 'none';
                ineField.required = false;
                ineField.value = '';
            }
        }

        document.getElementById('creerUtilisateurForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            
            fetch('<%= request.getContextPath() %>/app/admin/creer-utilisateur', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                const messageDiv = document.getElementById('responseMessage');
                
                if (data.success) {
                    messageDiv.innerHTML = `
                        <div class="success">
                            <p><strong>${data.message}</strong></p>
                            <p><strong>Email :</strong> ${data.utilisateur.email}</p>
                            <p><strong>Lien d'activation :</strong></p>
                            <div class="link-box">
                                <a href="${data.lien}" target="_blank">${data.lien}</a>
                            </div>
                            <p><small>Ce lien expire dans 7 jours. Envoyez-le à l'utilisateur.</small></p>
                        </div>
                    `;
                    document.getElementById('creerUtilisateurForm').reset();
                } else {
                    messageDiv.innerHTML = `<div class="error"><p>${data.message}</p></div>`;
                }
            })
            .catch(error => {
                document.getElementById('responseMessage').innerHTML = 
                    `<div class="error"><p>Erreur : ${error.message}</p></div>`;
            });
        });
    </script>
    </main>
</div>
</body>
</html>