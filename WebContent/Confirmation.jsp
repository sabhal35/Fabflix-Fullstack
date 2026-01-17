<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="domain.CartItem" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Confirmation</title>
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
            max-width: 800px;
        }
        h1 {
            font-size: 2.5rem;
            font-weight: 700;
            color: #4b6cb7;
            text-align: center;
            margin-bottom: 30px;
        }
        p {
            font-size: 1.1rem;
            text-align: center;
            color: #333;
        }
        .table {
            margin-top: 20px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .table th {
            background-color: #4b6cb7;
            color: white;
            font-weight: 700;
            text-align: center;
        }
        .table td {
            text-align: center;
            font-size: 1.1rem;
        }
        .table th, .table td {
            vertical-align: middle;
        }
        .btn-primary {
            background-color: #4b6cb7;
            border: none;
            font-size: 1.1rem;
            padding: 10px 20px;
            margin-top: 30px;
            display: block;
            width: 100%;
            text-align: center;
        }
        .btn-primary:hover {
            background-color: #3c5cae;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Order Confirmation</h1>

    <p>Thank you for your order!</p>

    <table class="table table-bordered">
        <thead>
        <tr>
            <th>Movie Title</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Total</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<CartItem> confirmedCart = (List<CartItem>) request.getAttribute("confirmedCart");
            if (confirmedCart != null && !confirmedCart.isEmpty()) {
                double totalPrice = 0.0;
                for (CartItem item : confirmedCart) {
                    double itemTotal = item.getQuantity() * item.getPrice();
                    totalPrice += itemTotal;
        %>
        <tr>
            <td><%= item.getTitle() %></td>
            <td><%= item.getQuantity() %></td>
            <td>$<%= item.getPrice() %></td>
            <td>$<%= itemTotal %></td>
        </tr>
        <%
            }
        %>
        <tr>
            <th colspan="3">Total</th>
            <th>$<%= totalPrice %></th>
        </tr>
        <%
        } else {
        %>
        <tr>
            <td colspan="4">No items in cart.</td>
        </tr>
        <%
            }
        %>
        </tbody>
    </table>

    <a href="MainPage.jsp" class="btn btn-primary">Return to Home</a>
</div>
</body>
</html>