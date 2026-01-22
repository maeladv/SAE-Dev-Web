<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Specialite" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Utilisateur" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détails Spécialité - Admin</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/modals.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/admin-specialite-details.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/specialites.css">

</head>
<body>
    <script src="<%= request.getContextPath() %>/static/js/admin.js"></script>
    <script>
        const contextPath = '<%= request.getContextPath() %>';
    </script>
    <div class="page-container">
        <%@ include file="includes/header.jsp" %>
        
        <main class="specialite-details-main">
            <%
                Specialite specialite = (Specialite) request.getAttribute("specialite");
                String tagClass = specialite != null ? "tag-" + specialite.getTag().toLowerCase() : "";
            %>
            
            <!-- Toolbar -->
            <div class="specialite-toolbar">
                <div class="specialite-header">
                    <% if (specialite != null) { %>
                    <h1><%= specialite.getNom() %></h1>
                    <div class="specialite-info">
                        <span class="specialite-tag-badge <%= tagClass %>"><%= specialite.getTag().toUpperCase() %></span>
                        <span class="specialite-year"><%= specialite.getAnnee() %>A</span>
                    </div>
                    <% } %>
                </div>
                <%-- bouton primaire liste d'appel --%>
                <div class="specialite-actions">
                    <a class="btn btn-primary btn-with-icon" href="<%= request.getContextPath() %>/app/admin/specialites">
                        Liste d'appel
                        <img src="<%= request.getContextPath() %>/static/icons/white/file-export.svg" alt="Télécharger">
                    </a>
                </div>
            </div>

            <!-- Content Grid -->
            <div class="specialite-content-grid">
                <!-- Matières Section -->
                <div class="specialite-card">
                    <div class="card-header">
                        <h2>Matières</h2>
                        <button class="btn btn-secondary btn-with-icon" onclick="openModal('addMatiereModal')">
                            <img src="<%= request.getContextPath() %>/static/icons/black/circle-plus.svg" alt="Ajouter">
                            Ajouter une matière
                        </button>
                    </div>
                    
                    <div class="matieres-container">
                        <%
                            List<Matiere> matieres = (List<Matiere>) request.getAttribute("matieres");
                            List<Utilisateur> profs = (List<Utilisateur>) request.getAttribute("professeurs");
                            java.util.Map<Integer, String> profEmailMap = new java.util.HashMap<>();
                            if (profs != null) {
                                for (Utilisateur p : profs) {
                                    profEmailMap.put(p.getId(), p.getemail());
                                }
                            }

                            if (matieres != null && !matieres.isEmpty()) {
                                // Group by semester
                                java.util.Map<Integer, java.util.List<Matiere>> byPartnered = new java.util.TreeMap<>();
                                for (Matiere m : matieres) {
                                    byPartnered.computeIfAbsent(m.getSemestre(), k -> new java.util.ArrayList<>()).add(m);
                                }
                
                                for (java.util.Map.Entry<Integer, java.util.List<Matiere>> entry : byPartnered.entrySet()) {
                        %>
                        <div class="semester-group">
                            <h3 class="semester-title">Semestre <%= entry.getKey() %></h3>
                            <%
                                for (Matiere m : entry.getValue()) {
                            %>
                            <div class="matiere-item">
                                <div class="matiere-info">
                                    <h3><%= m.getNom().toUpperCase() %></h3>
                                    <span class="matiere-prof"><%= profEmailMap.getOrDefault(m.getProfId(), "") %></span>
                                </div>
                                <div class="matiere-actions">
                                    <button class="btn btn-tertiary" title="Modifier" onclick="editMatiere(<%= m.getId() %>, '<%= m.getNom().replace("'", "\\'" ) %>', <%= m.getSemestre() %>, '<%= profEmailMap.getOrDefault(m.getProfId(), "") %>')">
                                        <img src="<%= request.getContextPath() %>/static/icons/black/pen.svg" alt="Modifier">
                                    </button>
                                    <button class="btn btn-tertiary" title="Supprimer" onclick="confirmDeleteMatiere(<%= m.getId() %>, '<%= m.getNom().replace("'", "\\\'" ) %>')">
                                        <img src="<%= request.getContextPath() %>/static/icons/black/trash.svg" alt="Supprimer">
                                    </button>
                                    <button class="btn btn-primary" onclick="viewExams(<%= m.getId() %>)">
                                        Voir
                                    </button>
                                </div>
                            </div>
                            <%
                                }
                            %>
                        </div>
                        <%
                                }
                            } else {
                        %>
                        <div class="empty-state">Aucune matière trouvée</div>
                        <%
                            }
                        %>
                    </div>
                </div>

                <!-- Étudiants Section -->
                <div class="specialite-card">
                    <div class="card-header">
                        <h2>Étudiants (<span id="studentCount">0</span>)</h2>
                        <button class="btn btn-secondary btn-with-icon" onclick="openModal('addStudentModal')">
                            <img src="<%= request.getContextPath() %>/static/icons/black/user-plus.svg" alt="Ajouter">
                            Ajouter un étudiant
                        </button>
                    </div>
                    
                    <div class="students-container">
                        <%
                            List<Utilisateur> students = (List<Utilisateur>) request.getAttribute("students");
                            if (students != null && !students.isEmpty()) {
                                out.print("<script>document.getElementById('studentCount').textContent = '" + students.size() + "';</script>");
                                for (Utilisateur s : students) {
                        %>
                        <div class="student-item">
                            <div class="student-info">
                                <div class="student-name"><%= s.getNom() %> <%= s.getPrenom() %></div>
                                <div class="student-email"><%= s.getemail() %></div>
                            </div>
                            <div class="student-actions">
                                <button class="btn btn-tertiary" title="Supprimer" onclick="confirmRemoveStudent('<%= s.getemail() %>', '<%= s.getPrenom() %> <%= s.getNom() %>')">
                                    <img src="<%= request.getContextPath() %>/static/icons/black/trash.svg" alt="Supprimer">
                                </button>
                                <button class="btn btn-primary" title="Voir les notes" onclick="viewStudentGrades(<%= s.getId() %>)">
                                    <img src="<%= request.getContextPath() %>/static/icons/white/user-graduate.svg" alt="Voir les notes">
                                </button>
                            </div>
                        </div>
                        <%
                                }
                            } else {
                        %>
                        <div class="empty-state">Aucun étudiant trouvé</div>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- Modal: Ajouter une matière -->
    <div id="addMatiereModal" class="modal-overlay">
        <div class="modal modal-small">
            <h2 class="modal-title">Ajouter une matière</h2>
            <div class="modal-content">
                <form onsubmit="return submitAddMatiere(event)">
                    <div class="form-group">
                        <label for="matiere-nom">Nom de la matière</label>
                        <input type="text" id="matiere-nom" name="nom" placeholder="Pentest" class="input-field" required>
                    </div>
                    <div class="form-group">
                        <label for="matiere-prof">Professeur responsable de la spécialité</label>
                        <input type="email" id="matiere-prof" name="profEmail" placeholder="Rentrez l'adresse email du professeur..." class="input-field" required>
                    </div>
                    <div class="form-group">
                        <label for="matiere-semestre">Semestre associé à la matière</label>
                        <select id="matiere-semestre" name="semestre" class="input-field" required>
                            <option value="">Choisir un semestre</option>
                            <option value="1">Semestre 1</option>
                            <option value="2">Semestre 2</option>
                        </select>
                    </div>
                    <div class="modal-actions">
                        <button type="button" class="btn btn-tertiary" onclick="closeModal('addMatiereModal')">Annuler</button>
                        <button type="submit" class="btn btn-primary">Créer la matière</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal: Modifier une matière -->
    <div id="editMatiereModal" class="modal-overlay">
        <div class="modal modal-small">
            <h2 class="modal-title">Modifier la matière</h2>
            <div class="modal-content">
                <form onsubmit="return submitEditMatiere(event)">
                    <input type="hidden" id="edit-matiere-id" name="id">
                    <div class="form-group">
                        <label for="edit-matiere-nom">Nom de la matière</label>
                        <input type="text" id="edit-matiere-nom" name="nom" class="input-field" required>
                    </div>
                    <div class="form-group">
                        <label for="edit-matiere-prof">Professeur responsable de la spécialité</label>
                        <input type="email" id="edit-matiere-prof" name="profEmail" class="input-field" required>
                    </div>
                    <div class="form-group">
                        <label for="edit-matiere-semestre">Semestre associé à la matière</label>
                        <select id="edit-matiere-semestre" name="semestre" class="input-field" required>
                            <option value="">Choisir un semestre</option>
                            <option value="1">Semestre 1</option>
                            <option value="2">Semestre 2</option>
                        </select>
                    </div>
                    <div class="modal-actions">
                        <button type="button" class="btn btn-tertiary" onclick="closeModal('editMatiereModal')">Annuler</button>
                        <button type="submit" class="btn btn-primary">Enregistrer</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal: Confirmation supprimer matière -->
    <div id="deleteMatieireModal" class="modal-overlay">
        <div class="modal modal-small">
            <h2 class="modal-title">Supprimer la matière ?</h2>
            <div class="modal-content">
                <p class="modal-message">Êtes-vous sûr·e de vouloir effectuer cette opération ?</p>
                <div id="deleteMatiereError" style="display: none; margin-bottom: 1rem; padding: 0.75rem; background-color: #ffe6e6; border-left: 4px solid #fe3232; border-radius: 4px;">
                    <p id="deleteMatiereErrorText" style="margin: 0; color: #fe3232; font-size: 0.9rem;"></p>
                </div>
                <div class="modal-actions">
                    <button type="button" class="btn btn-tertiary" onclick="closeModal('deleteMatieireModal')">Annuler</button>
                    <button type="button" class="btn btn-primary btn-danger" onclick="submitDeleteMatiere()">Supprimer définitivement</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: Ajouter un étudiant -->
    <div id="addStudentModal" class="modal-overlay">
        <div class="modal modal-small">
            <h2 class="modal-title">Ajouter un étudiant</h2>
            <div class="modal-content">
                <form onsubmit="return submitAddStudent(event)">
                    <div class="form-group">
                        <label for="student-email">Adresse email de l'étudiant</label>
                        <input type="email" id="student-email" name="email" placeholder="Rentrez l'adresse email de l'étudiant à ajouter" class="input-field" required>
                    </div>
                    <div class="modal-actions">
                        <button type="button" class="btn btn-tertiary" onclick="closeModal('addStudentModal')">Annuler</button>
                        <button type="submit" class="btn btn-primary">Ajouter</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Modal: Confirmation retirer étudiant -->
    <div id="removeStudentModal" class="modal-overlay">
        <div class="modal modal-small">
            <h2 class="modal-title">Retirer l'étudiant de la spécialité ?</h2>
            <div class="modal-content">
                <p class="modal-message">Êtes-vous sûr·e de vouloir effectuer cette opération ?</p>
                <div class="modal-actions">
                    <button type="button" class="btn btn-tertiary" onclick="closeModal('removeStudentModal')">Annuler</button>
                    <button type="button" class="btn btn-primary btn-danger" onclick="submitRemoveStudent()">Retirer l'étudiant</button>
                </div>
            </div>
        </div>
    </div>

    <script src="<%= request.getContextPath() %>/static/js/modals.js"></script>
    <script>
        <%
            Specialite spec = (Specialite) request.getAttribute("specialite");
            int specId = (spec != null) ? spec.getId() : -1;
        %>
        const specialiteId = <%= specId %>;

        function openModal(modalId) {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.classList.add('active');
            }
        }

        function closeModal(modalId) {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.classList.remove('active');
            }
        }

        // Initialiser les dropdowns en réutilisant la logique existante
        window.addEventListener('load', () => {
            if (typeof initDropdowns === 'function') {
                initDropdowns();
            }
        });

        let currentDeleteMatiereId = null;
        let currentRemoveStudentId = null;
        let currentRemoveStudentEmail = null;

        function editMatiere(id, nom, semestre, profEmail) {
            document.getElementById('edit-matiere-id').value = id;
            document.getElementById('edit-matiere-nom').value = nom;
            document.getElementById('edit-matiere-prof').value = profEmail;
            document.getElementById('edit-matiere-semestre').value = semestre;
            
            openModal('editMatiereModal');
        }

        function confirmDeleteMatiere(id, nom) {
            currentDeleteMatiereId = id;
            openModal('deleteMatieireModal');
        }

        function submitDeleteMatiere() {
            if (currentDeleteMatiereId) {
                // Utiliser application/x-www-form-urlencoded pour que getParameter fonctionne côté serveur
                const params = new URLSearchParams();
                params.append('id', currentDeleteMatiereId);
                
                // Cacher les erreurs précédentes
                const errorContainer = document.getElementById('deleteMatiereError');
                const errorText = document.getElementById('deleteMatiereErrorText');
                errorContainer.style.display = 'none';
                errorText.textContent = '';
                
                fetch(contextPath + '/app/admin/supprimer-matiere', {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest'
                    },
                    body: params
                }).then(response => {
                    if (response.ok) {
                        closeModal('deleteMatieireModal');
                        location.reload();
                    } else {
                        return response.text().then(text => {
                            const message = text && text.trim() ? text : 'Erreur lors de la suppression de la matière';
                            errorText.textContent = message;
                            errorContainer.style.display = 'block';
                        });
                    }
                }).catch(error => {
                    console.error('Erreur:', error);
                    errorText.textContent = 'Erreur réseau : impossible de contacter le serveur';
                    errorContainer.style.display = 'block';
                });
            }
        }

        function confirmRemoveStudent(email, nom) {
            currentRemoveStudentEmail = email;
            openModal('removeStudentModal');
        }

        function submitRemoveStudent() {
            if (currentRemoveStudentEmail) {
                const formData = new FormData();
                formData.append('email', currentRemoveStudentEmail);
                formData.append('specialiteId', specialiteId);
                
                fetch(contextPath + '/app/admin/retirer-etudiant', {
                    method: 'POST',
                    body: formData
                }).then(response => {
                    if (response.ok) {
                        closeModal('removeStudentModal');
                        location.reload();
                    } else {
                        alert('Erreur lors du retrait de l\'étudiant');
                    }
                }).catch(error => {
                    console.error('Erreur:', error);
                    alert('Erreur réseau');
                });
            }
        }

        function submitAddMatiere(event) {
            event.preventDefault();
            const nom = document.getElementById('matiere-nom').value;
            const semestre = document.getElementById('matiere-semestre').value;
            const profEmail = document.getElementById('matiere-prof').value;

            const form = document.querySelector('#addMatiereModal form');
            
            const params = new URLSearchParams();
            params.append('nom', nom);
            params.append('semestre', semestre);
            params.append('specialiteId', specialiteId);
            params.append('profEmail', profEmail);
            
            fetch(contextPath + '/app/admin/creer-matiere', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: params
            }).then(response => {
                if (response.ok) {
                    closeModal('addMatiereModal');
                    location.reload();
                } else {
                    return response.text().then(text => {
                        const message = text && text.trim() ? text : 'Erreur lors de la création de la matière';
                        if (typeof showFormError === 'function') {
                            showFormError(form, message);
                        }
                    });
                }
            }).catch(error => {
                console.error('Erreur:', error);
                if (typeof showFormError === 'function') {
                    showFormError(form, 'Erreur réseau');
                }
            });
            return false;
        }

        function submitEditMatiere(event) {
            event.preventDefault();
            const id = document.getElementById('edit-matiere-id').value;
            const nom = document.getElementById('edit-matiere-nom').value;
            const semestre = document.getElementById('edit-matiere-semestre').value;
            const profEmail = document.getElementById('edit-matiere-prof').value;
            const form = document.querySelector('#editMatiereModal form');
            
            const params = new URLSearchParams();
            params.append('id', id);
            params.append('nom', nom);
            params.append('semestre', semestre);
            params.append('profEmail', profEmail);
            params.append('specId', specialiteId);
            
            fetch(contextPath + '/app/admin/modifier-matiere', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: params
            }).then(response => {
                if (response.ok) {
                    closeModal('editMatiereModal');
                    location.reload();
                } else {
                    return response.text().then(text => {
                        const message = text && text.trim() ? text : 'Erreur lors de la modification de la matière';
                        if (typeof showFormError === 'function') {
                            showFormError(form, message);
                        }
                    });
                }
            }).catch(error => {
                console.error('Erreur:', error);
                if (typeof showFormError === 'function') {
                    showFormError(form, 'Erreur réseau');
                }
            });
            return false;
        }

        function submitAddStudent(event) {
            event.preventDefault();
            const email = document.getElementById('student-email').value;
            
            const formData = new FormData();
            formData.append('email', email);
            formData.append('specialiteId', specialiteId);
            
            fetch(contextPath + '/app/admin/ajouter-etudiant', {
                method: 'POST',
                body: formData
            }).then(response => {
                if (response.ok) {
                    closeModal('addStudentModal');
                    location.reload();
                } else {
                    alert('Erreur lors de l\'ajout de l\'étudiant');
                }
            }).catch(error => {
                console.error('Erreur:', error);
                alert('Erreur réseau');
            });
            return false;
        }

        function viewExams(matiereId) {
            window.location.href = contextPath + '/app/admin/examens?matId=' + matiereId;
        }

        function viewStudentGrades(studentId) {
            window.location.href = contextPath + '/app/admin/notes?studentId=' + studentId;
        }

        // Close modal when clicking overlay
        document.querySelectorAll('.modal-overlay').forEach(overlay => {
            overlay.addEventListener('click', (e) => {
                if (e.target === overlay) {
                    overlay.classList.remove('active');
                }
            });
        });
    </script>
</body>
</html>