<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Utilisateur" %>
<%@ page import="model.Note" %>
<%@ page import="model.Examen" %>
<%@ page import="model.Matiere" %>
<%@ page import="model.Specialite" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.LinkedHashMap" %>
<%
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/app/login");
        return;
    }
    
    // Vérifier que c'est un étudiant
    if (user.getRole() != null && !user.getRole().equals("etudiant")) {
        response.sendRedirect(request.getContextPath() + "/app/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes notes - Gradibou</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/global.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/composants.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/header.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/pages/mesNotes.css">
</head>
<body>
<div class="page-container notes-page">
    <%@ include file="includes/header.jsp" %>
    
    <main class="notes-main">
        <!-- En-tête avec le titre et badges -->
        <section class="notes-header">
            <div class="header-content">
                <div class="title-section">
                    <p class="header-subtitle">Mes notes</p>
                    <h1 class="header-title"><%= user.getNom() %> <%= user.getPrenom() %></h1>
                    
                    <!-- Badges spécialité et année -->
                    <div class="badges-container">
                        <% 
                            String specialiteTag = "";
                            try {
                                specialiteTag = user.getSpecialiteTag();
                                if (specialiteTag != null && !specialiteTag.isEmpty()) {
                        %>
                            <div class="badge badge-specialite" data-specialite="<%= specialiteTag.toLowerCase() %>">
                                <%= specialiteTag %>
                            </div>
                        <% 
                                }
                            } catch (Exception e) {}
                        %>
                        <div class="badge badge-year">3A</div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Statistiques principales -->
        <section class="stats-container">
            <%
                List<Note> allNotes = (List<Note>) request.getAttribute("notes");
                Map<Integer, Map<String, Object>> semesters = (Map<Integer, Map<String, Object>>) request.getAttribute("semesterStats");
                Map<String, Double> subjectAverages = (Map<String, Double>) request.getAttribute("subjectAverages");
                Double generalAverage = (Double) request.getAttribute("generalAverage");
                String bestSubject = (String) request.getAttribute("bestSubject");
                Double bestSubjectGrade = (Double) request.getAttribute("bestSubjectGrade");
                String worstSubject = (String) request.getAttribute("worstSubject");
                Double worstSubjectGrade = (Double) request.getAttribute("worstSubjectGrade");
                Integer rankingInSpeciality = (Integer) request.getAttribute("rankingInSpeciality");
                Integer totalStudentsInSpeciality = (Integer) request.getAttribute("totalStudentsInSpeciality");
                
                // Valeurs par défaut si données non disponibles
                if (generalAverage == null) generalAverage = 0.0;
                if (semesters == null) semesters = new TreeMap<>();
            %>
            
            <div class="stat-card stat-general-average">
                <div class="stat-average">
                    <span class="grade-large"><%= String.format("%.1f", generalAverage) %></span>
                    <span class="grade-max">/20</span>
                </div>
                <p class="stat-label">Moyenne Générale</p>
            </div>

            <div class="semesters-stats">
                <%
                    // Semestre 1
                    Double sem1Average = null;
                    if (semesters.containsKey(1)) {
                        sem1Average = (Double) semesters.get(1).get("moyenne");
                    }
                    if (sem1Average == null) sem1Average = 0.0;
                %>
                <div class="semester-stat">
                    <div class="stat-label-row">
                        <span class="stat-label">Moyenne</span>
                    </div>
                    <p class="stat-semester-label">Semestre 1</p>
                    <div class="grade-display">
                        <span class="grade-large"><%= String.format("%.1f", sem1Average) %></span>
                        <span class="grade-max">/20</span>
                    </div>
                </div>

                <%
                    // Semestre 2
                    Double sem2Average = null;
                    if (semesters.containsKey(2)) {
                        sem2Average = (Double) semesters.get(2).get("moyenne");
                    }
                    if (sem2Average == null) sem2Average = 0.0;
                %>
                <div class="semester-stat">
                    <div class="stat-label-row">
                        <span class="stat-label">Moyenne</span>
                    </div>
                    <p class="stat-semester-label">Semestre 2</p>
                    <div class="grade-display">
                        <span class="grade-large"><%= String.format("%.1f", sem2Average) %></span>
                        <span class="grade-max">/20</span>
                    </div>
                </div>
            </div>

            <!-- Graphique/Image de comparaison -->
            <div class="stat-card stat-chart">
                <div class="chart-placeholder">
                    <p class="chart-title">Moyenne Générale</p>
                    <p class="chart-subtitle">VS</p>
                    <p class="chart-comparison">Moyenne du Groupe</p>
                </div>
            </div>

            <div class="best-worst-container">
                <div class="best-subject-stat">
                    <div class="stat-label-row">
                        <span class="stat-label">Meilleure matière</span>
                    </div>
                    <p class="stat-subject-name">
                        <%= bestSubject != null ? bestSubject : "N/A" %>
                    </p>
                    <div class="grade-display">
                        <span class="grade-large"><%= bestSubjectGrade != null ? String.format("%.1f", bestSubjectGrade) : "N/A" %></span>
                        <span class="grade-max">/20</span>
                    </div>
                </div>

                <div class="worst-subject-stat">
                    <div class="stat-label-row">
                        <span class="stat-label">Pire matière</span>
                    </div>
                    <p class="stat-subject-name">
                        <%= worstSubject != null ? worstSubject : "N/A" %>
                    </p>
                    <div class="grade-display">
                        <span class="grade-large"><%= worstSubjectGrade != null ? String.format("%.1f", worstSubjectGrade) : "N/A" %></span>
                        <span class="grade-max">/20</span>
                    </div>
                </div>
            </div>

            <div class="ranking-stat">
                <div class="stat-label-row">
                    <span class="stat-label">Position dans la spécialité</span>
                    <% 
                        String specialiteTagRanking = "";
                        try {
                            specialiteTagRanking = user.getSpecialiteTag();
                            if (specialiteTagRanking != null && !specialiteTagRanking.isEmpty()) {
                    %>
                        <div class="badge badge-specialite-small" data-specialite="<%= specialiteTagRanking.toLowerCase() %>">
                            <%= specialiteTagRanking %>
                        </div>
                    <% 
                            }
                        } catch (Exception e) {}
                    %>
                </div>
                <div class="grade-display">
                    <span class="grade-large">
                        <%= rankingInSpeciality != null ? rankingInSpeciality : "N/A" %>
                    </span>
                    <span class="grade-max">
                        <%= totalStudentsInSpeciality != null ? "/" + totalStudentsInSpeciality : "" %>
                    </span>
                </div>
            </div>
        </section>

        <!-- Tableau des notes par matière/semestre -->
        <section class="notes-table-section">
            <div class="notes-table-wrapper">
                <!-- En-tête du tableau -->
                <div class="table-header">
                    <div class="table-header-row">
                        <span class="table-col-label">Matière/épreuve</span>
                        <span class="table-col-label">Min</span>
                        <span class="table-col-label">Max</span>
                        <span class="table-col-label">Moyenne de groupe</span>
                        <span class="table-col-label">Note</span>
                    </div>
                </div>

                <!-- Semestre 2 -->
                <%
                    Map<String, List<Map<String, Object>>> sem2Groups = (Map<String, List<Map<String, Object>>>) request.getAttribute("sem2Groups");
                    Map<String, Double> sem2SubjectAverages = (Map<String, Double>) request.getAttribute("sem2SubjectAverages");
                    Double sem2Total = (Double) request.getAttribute("sem2Average");
                    if (sem2Groups == null) sem2Groups = new LinkedHashMap<>();
                    if (sem2SubjectAverages == null) sem2SubjectAverages = new LinkedHashMap<>();
                    if (sem2Total == null) sem2Total = 0.0;
                %>
                <div class="semester-section">
                    <div class="semester-header">
                        <p class="semester-title">Semestre 2</p>
                        <div class="semester-total">
                            <span class="grade-large"><%= String.format("%.1f", sem2Total) %>/20</span>
                        </div>
                    </div>

                    <% if (sem2Groups != null && !sem2Groups.isEmpty()) {
                        for (Map.Entry<String, List<Map<String, Object>>> entry : sem2Groups.entrySet()) {
                            String subjectName = entry.getKey();
                            List<Map<String, Object>> exams = entry.getValue();
                            boolean isBest = (bestSubject != null && bestSubject.equals(subjectName));
                    %>
                    <!-- Subject header row -->
                    <div class="subject-row subject-group-header <%= isBest ? "best-grade" : "" %>">
                        <span class="col-subject"><%= subjectName %></span>
                        <span class="col-grade">-</span>
                        <span class="col-grade">-</span>
                        <span class="col-grade">-</span>
                        <span class="col-grade-student <%= isBest ? "best-badge" : "" %>">
                            <%= String.format("%.1f", sem2SubjectAverages.getOrDefault(subjectName, 0.0)) %>/20
                        </span>
                    </div>
                    <%  
                            if (exams != null) {
                                for (Map<String, Object> examRow : exams) {
                                    String examName = (String) examRow.get("nomExamen");
                                    Double minGrade = (Double) examRow.get("minimum");
                                    Double maxGrade = (Double) examRow.get("maximum");
                                    Double groupAverage = (Double) examRow.get("moyenneGroupe");
                                    Double studentGrade = (Double) examRow.get("note");
                                    if (minGrade == null) minGrade = 0.0;
                                    if (maxGrade == null) maxGrade = 0.0;
                                    if (groupAverage == null) groupAverage = 0.0;
                                    if (studentGrade == null) studentGrade = 0.0;
                    %>
                    <div class="exam-row">
                        <span class="col-exam-name"><%= examName %></span>
                        <span class="col-grade"><%= String.format("%.1f", minGrade) %></span>
                        <span class="col-grade"><%= String.format("%.1f", maxGrade) %></span>
                        <span class="col-grade"><%= String.format("%.1f", groupAverage) %></span>
                        <span class="col-grade-student"><%= String.format("%.1f", studentGrade) %>/20</span>
                    </div>
                    <%          }
                            }
                        }
                    } %>
                </div>

                <!-- Semestre 1 -->
                <%
                    Map<String, List<Map<String, Object>>> sem1Groups = (Map<String, List<Map<String, Object>>>) request.getAttribute("sem1Groups");
                    Map<String, Double> sem1SubjectAverages = (Map<String, Double>) request.getAttribute("sem1SubjectAverages");
                    Double sem1Total = (Double) request.getAttribute("sem1Average");
                    if (sem1Groups == null) sem1Groups = new LinkedHashMap<>();
                    if (sem1SubjectAverages == null) sem1SubjectAverages = new LinkedHashMap<>();
                    if (sem1Total == null) sem1Total = 0.0;
                %>
                <div class="semester-section">
                    <div class="semester-header">
                        <p class="semester-title">Semestre 1</p>
                        <div class="semester-total">
                            <span class="grade-large"><%= String.format("%.1f", sem1Total) %>/20</span>
                        </div>
                    </div>

                    <% if (sem1Groups != null && !sem1Groups.isEmpty()) {
                        for (Map.Entry<String, List<Map<String, Object>>> entry : sem1Groups.entrySet()) {
                            String subjectName = entry.getKey();
                            List<Map<String, Object>> exams = entry.getValue();
                            boolean isBest = (bestSubject != null && bestSubject.equals(subjectName));
                    %>
                    <!-- Subject header row -->
                    <div class="subject-row subject-group-header <%= isBest ? "best-grade" : "" %>">
                        <span class="col-subject"><%= subjectName %></span>
                        <span class="col-grade">-</span>
                        <span class="col-grade">-</span>
                        <span class="col-grade">-</span>
                        <span class="col-grade-student <%= isBest ? "best-badge" : "" %>">
                            <%= String.format("%.1f", sem1SubjectAverages.getOrDefault(subjectName, 0.0)) %>/20
                        </span>
                    </div>
                    <%  
                            if (exams != null) {
                                for (Map<String, Object> examRow : exams) {
                                    String examName = (String) examRow.get("nomExamen");
                                    Double minGrade = (Double) examRow.get("minimum");
                                    Double maxGrade = (Double) examRow.get("maximum");
                                    Double groupAverage = (Double) examRow.get("moyenneGroupe");
                                    Double studentGrade = (Double) examRow.get("note");
                                    if (minGrade == null) minGrade = 0.0;
                                    if (maxGrade == null) maxGrade = 0.0;
                                    if (groupAverage == null) groupAverage = 0.0;
                                    if (studentGrade == null) studentGrade = 0.0;
                    %>
                    <div class="exam-row">
                        <span class="col-exam-name"><%= examName %></span>
                        <span class="col-grade"><%= String.format("%.1f", minGrade) %></span>
                        <span class="col-grade"><%= String.format("%.1f", maxGrade) %></span>
                        <span class="col-grade"><%= String.format("%.1f", groupAverage) %></span>
                        <span class="col-grade-student"><%= String.format("%.1f", studentGrade) %>/20</span>
                    </div>
                    <%          }
                            }
                        }
                    } %>
                </div>
            </div>
        </section>
    </main>
</div>
</body>
</html>
