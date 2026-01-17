import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class MainPageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("LoginPage.jsp?loginRequired=true");
        } else {
            request.getRequestDispatcher("MainPage.jsp").forward(request, response);
        }
    }
}
