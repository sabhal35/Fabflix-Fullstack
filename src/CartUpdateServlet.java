import domain.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@WebServlet(name = "CartUpdateServlet", urlPatterns = "/CartUpdateServlet")
public class CartUpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String movieId = request.getParameter("movieId");
        String action = request.getParameter("action");

        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart != null) {
            Iterator<CartItem> iterator = cart.iterator();

            while (iterator.hasNext()) {
                CartItem item = iterator.next();

                if (item.getMovieId().equals(movieId)) {
                    switch (action) {
                        case "increase":
                            item.setQuantity(item.getQuantity() + 1);
                            break;
                        case "decrease":
                            if (item.getQuantity() > 1) {
                                item.setQuantity(item.getQuantity() - 1);
                            }
                            break;
                        case "remove":
                            iterator.remove();
                            break;
                    }
                    break;
                }
            }

            double totalPrice = 0.0;
            for (CartItem item : cart) {
                totalPrice += item.getPrice() * item.getQuantity();
            }
            session.setAttribute("totalPrice", totalPrice);

            session.setAttribute("cart", cart);
        }

        response.sendRedirect("shopping-cart.jsp");
    }
}
