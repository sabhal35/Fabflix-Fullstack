<%@ page import="domain.Movie" %>
<%@ page import="domain.Star" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Top 20 Movies - Fabflix</title>

  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap" rel="stylesheet">

  <style>
    body {
      background-color: #f0f4f8;
      font-family: 'Montserrat', sans-serif;
    }
    .container {
      margin-top: 80px;
      margin-bottom: 80px;
    }
    .movie-table {
      background-color: white;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
      border-radius: 10px;
    }
    .movie-link {
      color: #4b6cb7;
      text-decoration: none;
      font-weight: bold;
    }
    .movie-link:hover {
      text-decoration: underline;
    }
    .star-link {
      color: #4b6cb7;
    }
    .btn-custom {
      background-color: #4b6cb7;
      color: white;
      border: none;
      border-radius: 5px;
    }
    .btn-custom:hover {
      background-color: #3c5cae;
    }
    .thead-custom {
      background-color: #3c5cae;
      color: white;
    }
    .table-hover tbody tr:hover {
      background-color: #e6f7ff;
    }
    .alert {
      margin: 20px 0;
    }
    footer {
      background-color: #f0f4f8;
      padding: 20px;
      text-align: center;
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      width: 100%;
      z-index: 1000;
    }
  </style>
</head>
<body>

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
  <h1 class="browse-title" style="font-size: 2.5rem; font-weight: bold; color: #4b6cb7; text-align: center; margin-bottom: 40px;">
    Top 20 Movies
  </h1>

  <% String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null && !errorMessage.isEmpty()) { %>
  <div class="alert alert-danger" role="alert">
    <%= errorMessage %>
  </div>
  <% } %>

  <div class="text-center mb-3">
    <form action="Top20Servlet" method="GET" class="mb-3">
      <input type="hidden" name="title" value="<%= request.getParameter("title") %>">
      <input type="hidden" name="year" value="<%= request.getParameter("year") %>">
      <input type="hidden" name="director" value="<%= request.getParameter("director") %>">
      <input type="hidden" name="star" value="<%= request.getParameter("star") %>">
      <input type="hidden" name="genre" value="<%= request.getParameter("genre") %>">

      <label for="sort" class="form-label">Sort by:</label>
      <select name="sort" id="sort" class="form-select">
        <option value="title_asc" <%= "title_asc".equals(request.getParameter("sort")) ? "selected" : "" %>>Title (Ascending) then Rating</option>
        <option value="title_desc" <%= "title_desc".equals(request.getParameter("sort")) ? "selected" : "" %>>Title (Descending) then Rating</option>
        <option value="rating_asc" <%= "rating_asc".equals(request.getParameter("sort")) ? "selected" : "" %>>Rating (Ascending) then Title</option>
        <option value="rating_desc" <%= "rating_desc".equals(request.getParameter("sort")) ? "selected" : "" %>>Rating (Descending) then Title</option>
      </select>

      <button type="submit" class="btn" style="background-color: #38b2ac; color: white;">Sort & Apply</button>
    </form>
  </div>

  <div class="table-responsive">
    <table class="table table-hover movie-table">
      <thead class="thead-custom">
      <tr>
        <th>Title</th>
        <th>Year</th>
        <th>Director</th>
        <th>Rating</th>
        <th>Genres</th>
        <th>Stars</th>
        <th>Action</th>
      </tr>
      </thead>
      <tbody>
      <%
        List<Movie> topMovies = (List<Movie>) request.getAttribute("topMovies");
        if (topMovies != null && !topMovies.isEmpty()) {
          for (Movie movie : topMovies) {
      %>
      <tr>
        <td>
          <a href="single-movie.html?id=<%= movie.getId() %>" class="movie-link">
            <%= movie.getTitle() %>
          </a>
        </td>
        <td><%= movie.getYear() %></td>
        <td><a href="SearchServlet?director=<%= movie.getDirector() %>&sort=title_asc" class="movie-link"><%= movie.getDirector() %></a></td>
        <td><%= String.format("%.1f", movie.getRating()) %></td>
        <td><%= String.join(", ", movie.getGenres()) %></td>
        <td>
          <% for (Star star : movie.getStars()) { %>
          <a href="single-star.html?id=<%= star.getId() %>" class="star-link">
            <%= star.getName() %><br>
          </a>
          <% } %>
        </td>
        <td>
          <button type="button" class="btn btn-custom" onclick="addToCart('<%= movie.getId() %>', '<%= movie.getTitle() %>')">Add to Cart</button>
          <p id="cart_message_<%= movie.getId() %>" class="success-message" style="display: none;">Added to cart!</p>
        </td>
      </tr>
      <%
        }
      } else {
      %>
      <tr>
        <td colspan="7" class="text-center">No movies found</td>
      </tr>
      <%
        }
      %>
      </tbody>
    </table>
  </div>
</div>

<footer>
  <p style="color: #38b2ac; margin: 0;">
    © 2024 Lara&Sona - All rights reserved.
  </p>
</footer>

<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<script>
  function addToCart(movieId, movieTitle) {
    $.ajax({
      url: 'CartServlet',
      type: 'POST',
      data: {
        movieId: movieId,
        movieTitle: movieTitle,
        price: '9.99'
      },
      success: function(response) {
        $("#cart_message_" + movieId).show();
        setTimeout(function() {
          $("#cart_message_" + movieId).hide();
        }, 2000);
      },
      error: function() {
        alert("Error adding movie to cart. Please try again.");
      }
    });
  }
</script>

</body>
</html>