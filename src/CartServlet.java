import domain.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CartServlet", urlPatterns = "/CartServlet")
public class CartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String movieId = request.getParameter("movieId");
        String movieTitle = request.getParameter("movieTitle");
        double price = Double.parseDouble(request.getParameter("price"));

        HttpSession session = request.getSession();

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        boolean itemExists = false;
        for (CartItem item : cart) {
            if (item.getMovieId().equals(movieId)) {
                item.setQuantity(item.getQuantity() + 1);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            cart.add(new CartItem(movieId, movieTitle, 1, price));
        }

        response.sendRedirect("single-movie.html?id=" + movieId + "&cartStatus=added");
    }
}
