<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add a Star</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            background-color: #f4f6f8;
            font-family: Arial, sans-serif;
        }
        .container {
            margin-top: 50px;
        }
        .form-title {
            text-align: center;
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 30px;
        }
        .btn-submit {
            background-color: #6fcf97;
            color: white;
            border: none;
            width: 100%;
            padding: 10px;
            font-size: 1.2rem;
            border-radius: 5px;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="EmployeeDashboard.jsp">Dashboard</a>
    <div class="collapse navbar-collapse">
        <ul class="navbar-nav ml-auto">
            <li class="nav-item"><a class="nav-link" href="AddMovie.jsp">Add a movie</a></li>
            <li class="nav-item"><a class="nav-link" href="AddStar.jsp">Add a star</a></li>
            <li class="nav-item"><a class="nav-link" href="AddGenre.jsp">Add a genre</a></li>
            <li class="nav-item"><a class="nav-link" href="MainPage.jsp">Browsing Movies</a></li>
        </ul>
    </div>
</nav>

<div class="container">

    <% if (request.getAttribute("starMessage") != null) { %>
    <div class="alert alert-info"><%= request.getAttribute("starMessage") %></div>
    <% } %>

    <h2 class="form-title">Add a Star</h2>
    <form action="AddStarServlet" method="post">
        <div class="form-group">
            <label for="starName">Star Name</label>
            <input type="text" class="form-control" id="starName" name="starName" required>
        </div>
        <div class="form-group">
            <label for="birthYear">Birth Year (optional)</label>
            <input type="number" class="form-control" id="birthYear" name="birthYear">
        </div>
        <button type="submit" class="btn-submit">Submit</button>
    </form>
</div>

</body>
</html>