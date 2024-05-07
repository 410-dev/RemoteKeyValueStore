package me.hysong.remotekeyvaluestore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/*")
public class API extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String path = request.getPathInfo();
        if (path == null) {
            response.setStatus(400);
            return;
        }
        path = path.substring(1);

        if (path.startsWith("auth/")) {
            response.setStatus(403);
            return;
        }

        String value = "";
        try {
            value = Database.read(path);
            boolean passed = Authorization.pass(path, request.getParameter("auth"));
            if (!passed) {
                response.setStatus(401);
                return;
            }
        } catch (Exception e) {
            response.setStatus(500);
        }

        if (value == null) {
            response.setStatus(404);
            return;
        }

        response.setStatus(200);
        response.setContentType("text/plain");
        try {
            response.getWriter().println(value);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        doGet(request, response);
    }

}
