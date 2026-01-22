<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Espace Admin</title>
  </head>
  <body>
    <h1>Espace Administrateur</h1>

    <p>
      <a href="<%= request.getContextPath() %>/app/admin/creer-compte"
        >Créer un compte utilisateur</a
      ><br />
      <a href="<%= request.getContextPath() %>/app/admin/specialites"
        >Gérer les spécialités (et tout le reste)</a
      ><br />
      <!-- Les liens directs sont masqués car accessibles via la navigation hiérarchique -->
      <!--
      <a href="<%= request.getContextPath() %>/app/admin/creer-specialite"
        >Créer une spécialité</a
      ><br />
      <a href="<%= request.getContextPath() %>/app/admin/creer-matiere"
        >Créer une matière</a
      ><br />
      <a href="<%= request.getContextPath() %>/app/admin/creer-examen"
        >Créer un examen</a
      ><br />
      <a href="<%= request.getContextPath() %>/app/admin/creer-note"
        >Saisir une note</a
      >
      -->
    </p>

    <a class="button" href="<%= request.getContextPath() %>/app/logout"
      >Se déconnecter</a
    >
  </body>
</html>
