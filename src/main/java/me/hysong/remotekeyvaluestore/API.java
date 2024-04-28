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
        } catch (Exception e) {
            response.setStatus(500);
        }

        if (value == null) {
            response.setStatus(404);
            return;
        }

        String[] lines = value.split("\n");
        if (lines[0].equals("[LOCKED]")) {
            // Check if parameter has "auth"
            String key = request.getParameter("auth");
            if (key == null) {
                response.setStatus(401);
                return;
            }
            if (!Authorization.pass(path.replace("/", "."), key)) {
                response.setStatus(401);
                return;
            }
            value = value.substring("[LOCKED]\n".length());
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
