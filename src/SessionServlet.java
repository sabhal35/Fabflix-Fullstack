import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/session")
public class SessionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        response.setContentType("application/json");

        JsonObject jsonResponse = new JsonObject();

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            jsonResponse.addProperty("status", "success");

            JsonObject userJson = new JsonObject();
            userJson.addProperty("firstName", user.getFirstName());
            userJson.addProperty("lastName", user.getLastName());
            jsonResponse.add("user", userJson);

            jsonResponse.addProperty("searchTitle", (String) session.getAttribute("searchTitle"));
            jsonResponse.addProperty("searchGenre", (String) session.getAttribute("searchGenre"));
            jsonResponse.addProperty("searchYear", (String) session.getAttribute("searchYear"));
            jsonResponse.addProperty("searchDirector", (String) session.getAttribute("searchDirector"));
            jsonResponse.addProperty("searchStar", (String) session.getAttribute("searchStar"));
            jsonResponse.addProperty("searchSort", (String) session.getAttribute("searchSort"));
            jsonResponse.addProperty("searchPage", (Integer) session.getAttribute("searchPage"));
            jsonResponse.addProperty("searchLimit", (Integer) session.getAttribute("searchLimit"));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "No session found");
        }

        response.getWriter().write(jsonResponse.toString());
    }
}