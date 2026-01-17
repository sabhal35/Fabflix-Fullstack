import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;

@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {
    private final ArrayList<String> allowedURIs = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        allowedURIs.add("login.html");
        allowedURIs.add("login.js");
        allowedURIs.add("api/login");
        allowedURIs.add("_dashboard/login.html");
        allowedURIs.add("_dashboard/login");
        allowedURIs.add("index.html");
        allowedURIs.add("index.js");
        allowedURIs.add("css/");
        allowedURIs.add("js/");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String requestURI = httpRequest.getRequestURI();
        String relativeURI = requestURI.substring(contextPath.length()).replaceFirst("^/", "");

        if (relativeURI.startsWith("/")) {
            relativeURI = relativeURI.substring(1);
        }

        if (isUrlAllowedWithoutLogin(relativeURI)) {
            System.out.println("LoginFilter: Access allowed for " + relativeURI);
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (relativeURI.equals("_dashboard/login.html")) {
            if (session == null || session.getAttribute("employee") == null) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendRedirect(contextPath + "/_dashboard");
            }
            return;
        }

        if (relativeURI.startsWith("_dashboard")) {
            if (session == null || session.getAttribute("employee") == null) {
                httpResponse.sendRedirect(contextPath + "/_dashboard/login.html");
                return;
            }
        } else {
            if (session == null || (session.getAttribute("user") == null && session.getAttribute("customerId") == null)) {
                httpResponse.sendRedirect(contextPath + "/login.html");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        return allowedURIs.stream().anyMatch(uri -> {
            if (uri.endsWith("/")) {
                return requestURI.toLowerCase().contains(uri.toLowerCase());
            } else {
                return requestURI.toLowerCase().endsWith(uri.toLowerCase());
            }
        });
    }

    private void logAccessAttempt(String requestURI, String relativeURI) {
        System.out.println("LoginFilter: Accessed " + requestURI + " (relative: " + relativeURI + ")");
    }

    private void logNoSessionRedirect(String contextPath, HttpSession session) {
        System.out.println("No user in session, redirecting to login.html");
        System.out.println("Session ID in filter: " + (session != null ? session.getId() : "No session"));
    }

    private void logUserInSession(HttpSession session) {
        System.out.println("User in session, proceeding to requested page.");
        System.out.println("Session ID in filter: " + session.getId());
    }

    @Override
    public void destroy() {
    }
}
