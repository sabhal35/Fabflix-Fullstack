<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Employee Dashboard</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            color: #333;
        }
        .container {
            margin-top: 40px;
        }
        .navbar {
            background-color: #6fcf97;
            padding: 10px;
            border-radius: 5px;
        }
        .navbar a {
            color: white;
            font-weight: bold;
            padding: 10px;
            text-decoration: none;
        }
        .navbar a:hover {
            color: #333;
        }
        .table-header {
            background-color: #6fcf97;
            color: white;
            padding: 10px;
            font-size: 1.2rem;
            text-align: center;
            border-radius: 5px;
            margin-top: 20px;
        }
        .table-section {
            margin-bottom: 30px;
        }
        .metadata-table {
            width: 100%;
            margin-top: 10px;
        }
        .metadata-table th {
            background-color: #6fcf97;
            color: white;
        }
        footer {
            font-size: 0.8rem;
            color: #888;
            text-align: center;
            margin-top: 30px;
        }
    </style>
</head>
<body>

<div class="container">
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
</div>


<div class="container">
    <h2 class="text-center font-weight-bold mt-4">Metadata</h2>

    <%
        Map<String, List<String[]>> metadata = (Map<String, List<String[]>>) request.getAttribute("metadata");
        if (metadata != null) {
            for (Map.Entry<String, List<String[]>> tableEntry : metadata.entrySet()) {
                String tableName = tableEntry.getKey();
                List<String[]> columns = tableEntry.getValue();
    %>

    <div class="table-section">
        <div class="table-header"><%= tableName %></div>
        <table class="table metadata-table">
            <thead>
            <tr>
                <th>Attribute</th>
                <th>Type</th>
            </tr>
            </thead>
            <tbody>
            <%
                for (String[] column : columns) {
            %>
            <tr>
                <td><%= column[0] %></td>
                <td><%= column[1] %></td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>

    <%
            }
        }
    %>
</div>

<footer>© 2024 Lara&Sona. All rights reserved.</footer>
</body>
</html>
