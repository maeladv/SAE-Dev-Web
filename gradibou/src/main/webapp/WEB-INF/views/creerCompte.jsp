<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Espace Admin</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; }
        .card { border: 1px solid #ddd; padding: 20px; border-radius: 6px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); margin-bottom: 20px; }
        h2 { margin-top: 0; }
        form { display: flex; flex-direction: column; }
        input { margin: 10px 0; padding: 8px; }
        button { padding: 10px; background-color: #0d6efd; color: #fff; border: none; cursor: pointer; border-radius: 4px; }
        button:hover { background-color: #0b5ed7; }
        .error { color: #dc3545; padding: 10px; background: #f8d7da; border-radius: 4px; }
        .success { color: #155724; padding: 10px; background: #d4edda; border-radius: 4px; }
        .link-box { background: #f0f0f0; padding: 10px; border-radius: 4px; word-break: break-all; }
        a.button { display: inline-block; margin-top: 15px; padding: 10px 14px; background: #6c757d; color: #fff; text-decoration: none; border-radius: 4px; }
        a.button:hover { background: #5a6268; }
    </style>
</head>
<body>
    <h1>Espace Administrateur</h1>
    
    <div class="card">
        <h2>Créer un nouvel utilisateur</h2>
        
        <div id="responseMessage"></div>

        <form id="creerUtilisateurForm" method="post" action="<%= request.getContextPath() %>/app/admin/creer-utilisateur">
            <input type="text" name="nom" placeholder="Nom" required>
            <input type="text" name="prenom" placeholder="Prénom" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="date" name="dateNaissance" required>
            
            <select name="role" id="roleSelect" required onchange="toggleINE()">
                <option value="">-- Choisir un rôle --</option>
                <option value="etudiant">Étudiant</option>
                <option value="professeur">Professeur</option>
                <option value="admin">Admin</option>
            </select>
            
            <input type="text" name="ine" id="ineField" placeholder="INE (obligatoire pour les étudiants)" style="display:none;">
            
            <button type="submit">Créer l'utilisateur</button>
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

    <a class="button" href="<%= request.getContextPath() %>/app/logout">Se déconnecter</a>
</body>
</html>