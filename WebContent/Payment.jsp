<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="domain.CartItem" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Information - Fabflix</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap" rel="stylesheet">
    <style>
        body {
            background-color: #f0f4f8;
            font-family: 'Montserrat', sans-serif;
        }
        .container {
            margin-top: 50px;
            max-width: 600px;
        }
        .payment-form {
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .payment-title {
            font-size: 2.5rem;
            font-weight: bold;
            color: #4b6cb7;
            text-align: center;
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .btn-place-order {
            background-color: #28a745;
            color: white;
            font-size: 1.2rem;
            padding: 12px;
            transition: background-color 0.3s ease;
        }
        .btn-place-order:hover {
            background-color: #218838;
        }
        .alert {
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<!-- Top Bar -->
<nav class="navbar navbar-expand-lg" style="background-color: #182848; padding: 20px; font-size: 1.2rem;">
    <a class="navbar-brand text-white" href="MainPage.jsp" style="font-size: 1.5rem;">Fabflix</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item">
                <a class="nav-link text-light" href="Top20Servlet">Top 20</a>
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
    <h1 class="payment-title">Payment Information</h1>

    <% if (request.getAttribute("errorMessage") != null) { %>
    <div class="alert alert-danger" role="alert">
        <%= request.getAttribute("errorMessage") %>
    </div>
    <% } %>

    <div class="payment-form">
        <form action="PlaceOrderServlet" method="post">
            <div class="form-group">
                <label for="cardNumber">Card Number:</label>
                <input type="text" id="cardNumber" name="cardNumber" class="form-control" placeholder="Enter card number" required>
            </div>
            <div class="form-group">
                <label for="firstName">First Name:</label>
                <input type="text" id="firstName" name="firstName" class="form-control" placeholder="First name" required>
            </div>
            <div class="form-group">
                <label for="lastName">Last Name:</label>
                <input type="text" id="lastName" name="lastName" class="form-control" placeholder="Last name" required>
            </div>
            <div class="form-group">
                <label for="expirationDate">Expiration Date:</label>
                <input type="text" id="expirationDate" name="expirationDate" class="form-control" placeholder="mm/dd/yyyy" required>
            </div>

            <div class="form-group text-center">
                <%
                    Double totalPrice = (Double) session.getAttribute("totalPrice");
                    if (totalPrice != null) {
                %>
                <p class="text-info">Total Payment: $<%= totalPrice %></p>
                <% } else { %>
                <p class="text-info">Total Payment: $0.00</p>
                <% } %>
            </div>

            <div class="form-group text-center">
                <button type="submit" class="btn btn-place-order btn-block">Place Order</button>
            </div>
        </form>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<footer style="background-color: #f0f4f8; padding: 20px; text-align: center; position: fixed; bottom: 0; left: 0; right: 0; width: 100%; z-index: 1000;">
    <p style="color: #38b2ac; margin: 0;">
        © 2024 Lara&Sona - All rights reserved.
    </p>
</footer>

</body>
</html>