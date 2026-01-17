import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        JsonObject responseJsonObject = new JsonObject();
        response.setContentType("application/json");

        System.out.println("Login attempt with Email: " + email);

        try (Connection conn = DatabaseConnectionPool.getMasterConnection()) {
            String query = "SELECT id, firstName, lastName, password FROM customers WHERE email = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, email);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password").trim();
                        if (password.equals(storedPassword)) {
                            HttpSession session = request.getSession();
                            User user = new User(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), email);
                            session.setAttribute("user", user);

                            responseJsonObject.addProperty("status", "success");
                            responseJsonObject.addProperty("message", "Login successful");
                            responseJsonObject.addProperty("redirect", "MainPage.html");
                        } else {
                            responseJsonObject.addProperty("status", "fail");
                            responseJsonObject.addProperty("message", "Incorrect password");
                        }
                    } else {
                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "User not found");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", "Internal server error");
        }

        response.getWriter().write(responseJsonObject.toString());
    }
}
