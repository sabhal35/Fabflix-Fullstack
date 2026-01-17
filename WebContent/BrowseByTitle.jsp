<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Browse by Title</title>

  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap" rel="stylesheet">

  <style>
    body {
      background-color: #f0f4f8;
      font-family: 'Montserrat', sans-serif;
      color: #333;
    }
    .container {
      margin-top: 50px;
    }
    .browse-title {
      font-size: 2.5rem;
      font-weight: 700;
      color: #4b6cb7;
      text-align: center;
      margin-bottom: 40px;
    }
    .title-list {
      display: flex;
      flex-wrap: wrap;
      gap: 15px;
      justify-content: center;
    }
    .title-item {
      background-color: #38b2ac;
      color: white;
      padding: 15px 25px;
      font-size: 1.2rem;
      border-radius: 8px;
      text-align: center;
      text-decoration: none;
      transition: background-color 0.3s, transform 0.3s;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }
    .title-item:hover {
      background-color: #319795;
      transform: translateY(-3px);
    }
    .logout-button {
      margin-bottom: 20px;
      text-align: right;
    }
    .row-separator {
      margin-top: 30px;
    }
    .btn-primary {
      background-color: #4b6cb7;
      border: none;
    }
    .btn-primary:hover {
      background-color: #3c5cae;
    }
  </style>
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar navbar-expand-lg" style="background-color: #182848; padding: 20px; font-size: 1.2rem;">
  <a class="navbar-brand text-white" href="MainPage.jsp" style="font-size: 1.5rem;">Fabflix</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNav">
    <ul class="navbar-nav mr-auto">
      <li class="nav-item">
        <a class="nav-link text-light" href="Top20Servlet">Top20</a>
      </li>
      <li class="nav-item">
        <a class="nav-link text-light" href="#">Results</a>
      </li>
      <li class="nav-item">
        <a class="nav-link text-light" href="shopping-cart.jsp">Checkout</a>
      </li>
    </ul>

    <form class="form-inline" action="SearchServlet" method="get">
      <input class="form-control mr-sm-2" type="text" name="title" placeholder="Title" aria-label="Title" style="width: 150px;">
      <input class="form-control mr-sm-2" type="text" name="genre" placeholder="Genre" aria-label="Genre" style="width: 150px;">
      <input class="form-control mr-sm-2" type="number" name="year" placeholder="Year" aria-label="Year" style="width: 120px;" min="1800" max="2100">
      <input class="form-control mr-sm-2" type="text" name="director" placeholder="Director" aria-label="Director" style="width: 160px;">
      <input class="form-control mr-sm-2" type="text" name="star" placeholder="Star" aria-label="Star" style="width: 150px;">
      <button class="btn btn-outline-light my-2 my-sm-0" type="submit">Search</button>
    </form>

    <div class="ml-2">
      <a href="LogoutServlet" class="btn btn-danger">Logout</a>
    </div>
  </div>
</nav>

<div class="container">
  <h2 class="browse-title">Browse Movies by Title</h2>

  <div class="title-list">
    <%
      List<String> characters = (List<String>) request.getAttribute("characters");
      for (String character : characters) {
        if (character.matches("[A-Z]")) {
    %>
    <a href="MovieListServlet?type=title&startChar=<%= character %>" class="title-item"><%= character %></a>
    <%
        }
      }
    %>
  </div>

  <div class="title-list row-separator">
    <%
      for (String character : characters) {
        if (character.matches("[0-9*]")) {
    %>
    <a href="MovieListServlet?type=title&startChar=<%= character %>" class="title-item"><%= character %></a>
    <%
        }
      }
    %>
  </div>

</div>

<footer style="background-color: #f0f4f8; padding: 20px; text-align: center; position: fixed; bottom: 0; left: 0; right: 0; width: 100%; z-index: 1000;">
  <p style="color: #38b2ac; margin: 0;">
    © 2024 Lara&Sona - All rights reserved.
  </p>
</footer>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>
</html>