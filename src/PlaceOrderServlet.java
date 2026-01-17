import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import domain.CartItem;

@WebServlet(name = "PlaceOrderServlet", urlPatterns = "/PlaceOrderServlet")
public class PlaceOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect("shopping-cart.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            request.setAttribute("errorMessage", "Session expired. Please log in again.");
            request.getRequestDispatcher("login.html").forward(request, response);
            return;
        }
        Integer customerId = user.getId();

        String cardNumber = request.getParameter("cardNumber");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String expirationDate = request.getParameter("expirationDate");

        try (Connection conn = DatabaseConnectionPool.getMasterConnection()) {
            String formattedDate = convertToMySQLDateFormat(expirationDate);

            if (!validateCreditCard(conn, cardNumber, firstName, lastName, formattedDate)) {
                request.setAttribute("errorMessage", "Invalid payment information. Please try again.");
                request.getRequestDispatcher("/Payment.jsp").forward(request, response);
                return;
            }

            for (CartItem item : cart) {
                recordSale(conn, customerId, item.getMovieId(), item.getQuantity());
            }

            request.setAttribute("confirmedCart", cart);
            request.setAttribute("confirmationMessage", "Your order has been placed successfully!");
            request.getRequestDispatcher("/Confirmation.jsp").forward(request, response);

            session.setAttribute("cart", null);

        } catch (SQLException e) {
            throw new ServletException("Error processing order", e);
        }
    }

    private String convertToMySQLDateFormat(String expirationDate) {
        String[] dateParts = expirationDate.split("/");
        return dateParts[2] + "-" + dateParts[0] + "-" + dateParts[1];
    }

    private boolean validateCreditCard(Connection conn, String cardNumber, String firstName, String lastName, String expirationDate) throws SQLException {
        final String query = "SELECT * FROM creditcards WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cardNumber);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, expirationDate);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean result = rs.next();
                return result;
            }
        }
    }

    private int recordSale(Connection conn, int customerId, String movieId, int quantity) throws SQLException {
        String insertSaleQuery = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(insertSaleQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, movieId);
            stmt.executeUpdate();
    
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating sale failed, no ID obtained.");
            }
        }
    }
}
