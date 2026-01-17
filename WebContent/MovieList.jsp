<%@ page import="domain.Movie" %>
<%@ page import="domain.Star" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Movie List - Fabflix</title>

  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap" rel="stylesheet">

  <style>
    body {
      background-color: #f0f4f8;
      font-family: 'Montserrat', sans-serif;
    }
    .container {
      margin-top: 80px;
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
      background-color: #d1e7ff;
    }
    .container {
      margin-top: 80px;
      margin-bottom: 80px;
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
    Movie List
  </h1>

  <div class="text-center mb-3">
    <form action="MovieListServlet" method="GET" class="mb-3">
      <%
        String[] parameterNames = {"title", "genre", "year", "director", "star", "startChar", "type"};
        for (String paramName : parameterNames) {
          String paramValue = request.getParameter(paramName);
          if (paramValue != null && !paramValue.isEmpty()) {
      %>
      <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>">
      <%
          }
        }
      %>


      <% if (request.getParameter("startChar") != null) { %>
      <input type="hidden" name="startChar" value="<%= request.getParameter("startChar") %>">
      <% } %>
      <% if (request.getParameter("genre") != null) { %>
      <input type="hidden" name="genre" value="<%= request.getParameter("genre") %>">
      <% } %>

      <label for="sort" class="form-label">Sort by:</label>
      <select name="sort" id="sort" class="form-select">
        <option value="title_asc_rating_asc" <%= "title_asc_rating_asc".equals(request.getParameter("sort")) ? "selected" : "" %>>Title ↑, Rating ↑</option>
        <option value="title_asc_rating_desc" <%= "title_asc_rating_desc".equals(request.getParameter("sort")) ? "selected" : "" %>>Title ↑, Rating ↓</option>
        <option value="title_desc_rating_asc" <%= "title_desc_rating_asc".equals(request.getParameter("sort")) ? "selected" : "" %>>Title ↓, Rating ↑</option>
        <option value="title_desc_rating_desc" <%= "title_desc_rating_desc".equals(request.getParameter("sort")) ? "selected" : "" %>>Title ↓, Rating ↓</option>
        <option value="rating_asc_title_asc" <%= "rating_asc_title_asc".equals(request.getParameter("sort")) ? "selected" : "" %>>Rating ↑, Title ↑</option>
        <option value="rating_asc_title_desc" <%= "rating_asc_title_desc".equals(request.getParameter("sort")) ? "selected" : "" %>>Rating ↑, Title ↓</option>
        <option value="rating_desc_title_asc" <%= "rating_desc_title_asc".equals(request.getParameter("sort")) ? "selected" : "" %>>Rating ↓, Title ↑</option>
        <option value="rating_desc_title_desc" <%= "rating_desc_title_desc".equals(request.getParameter("sort")) ? "selected" : "" %>>Rating ↓, Title ↓</option>
      </select>

      <label for="limit" class="form-label">Movies per page:</label>
      <select name="limit" id="limit" class="form-select">
        <option value="10" <%= "10".equals(request.getParameter("limit")) ? "selected" : "" %>>10</option>
        <option value="25" <%= "25".equals(request.getParameter("limit")) ? "selected" : "" %>>25</option>
        <option value="50" <%= "50".equals(request.getParameter("limit")) ? "selected" : "" %>>50</option>
        <option value="100" <%= "100".equals(request.getParameter("limit")) ? "selected" : "" %>>100</option>
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
        List<Movie> movies = (List<Movie>) request.getAttribute("movies");
        if (movies != null && !movies.isEmpty()) {
          for (Movie movie : movies) {
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
        <td>
          <%
            String genresString = movie.getGenres();
            String[] genres = genresString.split(",");
            for (int i = 0; i < genres.length; i++) {
              String genre = genres[i].trim();
          %>
          <a href="MovieListServlet?type=genre&genre=<%= genre %>" class="movie-link"><%= genre %></a>
          <%= (i < genres.length - 1) ? ", " : "" %>
          <%
            }
          %>
        </td>

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

  <div class="pagination-controls text-center mt-4">
    <%
      int currentPage = 1;
      int limit = 10;

      if (request.getParameter("page") != null) {
        currentPage = Integer.parseInt(request.getParameter("page"));
      }
      if (request.getParameter("limit") != null) {
        limit = Integer.parseInt(request.getParameter("limit"));
      }
    %>
    <form action="MovieListServlet" method="GET">
      <%
        // Preserve all search and sort parameters in pagination
        for (String paramName : parameterNames) {
          String paramValue = request.getParameter(paramName);
          if (paramValue != null && !paramValue.isEmpty()) {
      %>
      <input type="hidden" name="<%= paramName %>" value="<%= paramValue %>">
      <%
          }
        }
      %>
      <input type="hidden" name="limit" value="<%= limit %>">
      <input type="hidden" name="sort" value="<%= request.getParameter("sort") %>">

      <% if (currentPage > 1) { %>
      <button type="submit" name="page" value="<%= currentPage - 1 %>" class="btn btn-secondary">Previous</button>
      <% } %>

      <span>Page <%= currentPage %></span>

      <% if (movies != null && movies.size() == limit) { %>
      <button type="submit" name="page" value="<%= currentPage + 1 %>" class="btn btn-secondary">Next</button>
      <% } %>
    </form>
  </div>
</div>

<footer style="background-color: #f0f4f8; padding: 20px; text-align: center; position: fixed; bottom: 0; left: 0; right: 0; width: 100%; z-index: 1000;">
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