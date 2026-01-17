<%@ page import="domain.CartItem" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap" rel="stylesheet">
    <style>
        body {
            background-color: #f0f4f8;
            font-family: 'Montserrat', sans-serif;
        }
        .container {
            margin-top: 50px;
        }
        h1 {
            color: #4b6cb7;
            margin-bottom: 30px;
            text-align: center;
        }
        .table {
            background-color: white;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            border-radius: 10px;
        }
        .btn-warning, .btn-success, .btn-danger {
            margin: 0 5px;
        }
        .btn-success {
            background-color: #28a745;
            border: none;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .btn-danger {
            background-color: #dc3545;
            border: none;
        }
        .btn-danger:hover {
            background-color: #c82333;
        }
        .btn-warning {
            background-color: #ffc107;
            border: none;
        }
        .btn-warning:hover {
            background-color: #e0a800;
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
        <h1 class="text-center mb-4" style="font-size: 2.5rem; font-weight: bold; color: #4b6cb7;">Shopping Cart</h1>

    <table class="table">
        <thead>
        <tr>
            <th>Title</th>
            <th>Quantity</th>
            <th>Delete</th>
            <th>Price</th>
            <th>Total</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            double totalPrice = 0.0;
            if (cart != null && !cart.isEmpty()) {
                for (CartItem item : cart) {
                    double itemTotal = item.getPrice() * item.getQuantity();
                    totalPrice += itemTotal;
        %>
        <tr>
            <td><%= item.getTitle() %></td>
            <td>
                <form action="CartUpdateServlet" method="POST" style="display: inline;">
                    <input type="hidden" name="movieId" value="<%= item.getMovieId() %>">
                    <button type="submit" name="action" value="decrease" class="btn btn-warning">-</button>
                </form>
                <%= item.getQuantity() %>
                <form action="CartUpdateServlet" method="POST" style="display: inline;">
                    <input type="hidden" name="movieId" value="<%= item.getMovieId() %>">
                    <button type="submit" name="action" value="increase" class="btn btn-success">+</button>
                </form>
            </td>
            <td>
                <form action="CartUpdateServlet" method="POST">
                    <input type="hidden" name="movieId" value="<%= item.getMovieId() %>">
                    <button type="submit" name="action" value="remove" class="btn btn-danger">Delete</button>
                </form>
            </td>
            <td>$<%= item.getPrice() %></td>
            <td>$<%= itemTotal %></td>
        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="5" class="text-center">Your cart is empty.</td>
        </tr>
        <%
            }
        %>
        </tbody>
        <tfoot>
        <tr>
            <th colspan="4">Total Price</th>
            <th>$<%= totalPrice %></th>
        </tr>
        </tfoot>
    </table>

    <%
        session.setAttribute("totalPrice", totalPrice);
    %>

    <div class="text-center">
        <a href="Payment.jsp" class="btn btn-success">Proceed to Payment</a>
    </div>
</div>

<footer style="background-color: #f0f4f8; padding: 20px; text-align: center; position: fixed; bottom: 0; left: 0; right: 0; width: 100%; z-index: 1000;">
    <p style="color: #38b2ac; margin: 0;">
        © 2024 Lara&Sona - All rights reserved.
    </p>
</footer>

</body>
</html>