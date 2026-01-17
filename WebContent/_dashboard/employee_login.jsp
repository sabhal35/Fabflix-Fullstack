<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Employee Login</title>

  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Lato:wght@400&display=swap" rel="stylesheet">

  <style>
    body {
      background-color: #4b6cb7;
      background-image: linear-gradient(135deg, #4b6cb7 0%, #182848 100%);
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
      font-family: 'Lato', sans-serif;
      color: #ffffff;
    }
    .login-container {
      max-width: 400px;
      width: 100%;
      padding: 40px;
      background-color: #ffffff;
      border-radius: 12px;
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
      text-align: center;
      color: #182848;
    }
    .login-title {
      font-family: 'Montserrat', sans-serif;
      font-size: 1.75rem;
      color: #4b6cb7;
      margin-bottom: 20px;
      font-weight: 700;
    }
    .form-group {
      margin-bottom: 20px;
    }
    .form-control {
      border-radius: 25px;
      border: 1px solid #4b6cb7;
      padding: 10px 15px;
      font-size: 1rem;
      color: #182848;
    }
    .form-control::placeholder {
      color: #b0bec5;
    }
    .btn-custom {
      background-color: #38b2ac;
      color: white;
      font-size: 1.2rem;
      padding: 10px;
      width: 100%;
      border: none;
      border-radius: 25px;
      margin-top: 20px;
      cursor: pointer;
      transition: background-color 0.3s ease;
    }
    .btn-custom:hover {
      background-color: #2a9da8;
    }
    footer {
      font-family: 'Lato', sans-serif;
      font-size: 0.9rem;
      margin-top: 20px;
      color: #b0bec5;
    }
    #login_error_message {
      color: #e63946;
      font-size: 0.95rem;
      display: none;
      margin-top: 15px;
    }
  </style>
</head>
<body>

<div class="login-container">
  <h2 class="login-title">Employee Sign In</h2>
  <form id="employee_login_form" action="<c:url value='/_dashboard/login' />" method="post">
    <div class="form-group">
      <input type="email" class="form-control" id="email" name="email" placeholder="Email" required>
    </div>
    <div class="form-group">
      <input type="password" class="form-control" id="password" name="password" placeholder="Password" required>
    </div>
    <button type="submit" class="btn-custom">Login</button>
  </form>

  <div id="login_error_message" style="<%= request.getAttribute("errorMessage") != null ? "" : "display:none;" %>">
    <%= request.getAttribute("errorMessage") %>
  </div>
  <footer>&copy; 2024 Lara&Sona. All rights reserved.</footer>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>

  const contextPath = '<%= request.getContextPath() %>';

  $(document).ready(function() {
    $("#employee_login_form").submit(function(e) {
      e.preventDefault();

      $("#login_error_message").hide();

      const formData = {
        email: $("#email").val(),
        password: $("#password").val(),
      };

      const ajaxUrl = contextPath + "/_dashboard/login";

      $.ajax({
        url: ajaxUrl,
        method: "POST",
        data: formData,
        dataType: 'json',
        success: function(response) {
          if (response.status === "success") {
            // Redirect using the contextPath
            window.location.href = contextPath + "/_dashboard";
          } else {
            $("#login_error_message")
                    .text(response.message || "Login failed. Please try again.")
                    .show();
          }
        },
        error: function(xhr, status, error) {
          console.error("AJAX Error:", status, error);
          console.error("Response Text:", xhr.responseText);
          $("#login_error_message")
                  .text("Server error. Please try again.")
                  .show();
        }
      });
    });
  });
</script>

</body>
</html>