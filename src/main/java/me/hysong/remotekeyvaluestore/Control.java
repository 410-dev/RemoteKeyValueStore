package me.hysong.remotekeyvaluestore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/control")
public class Control extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Parameters:
        //   action: The action to perform. (lock, unlock, write, delete, read)
        //   key: The key to use for authorization.
        //   path: The path to the file.

        String action = request.getParameter("action");
        String key = request.getParameter("key");
        String path = request.getParameter("path");
        String value = request.getParameter("value");

        System.out.println("Action: " + action);

        if (action == null || key == null || path == null) {
            response.setStatus(400);
            return;
        }

        System.out.println("Key: " + key);
        System.out.println("Path: " + path);

        if (!Authorization.authorized(request.getSession())) {
            response.setStatus(401);
            System.out.println("Unauthorized.");
            return;
        }

        System.out.println("Authorized.");

        switch (action) {
            case "write": {
                System.out.println("Writing to " + path);
                if (value == null) {
                    response.setStatus(400);
                    response.getWriter().println("Value is null.");
                    return;
                }
                try {
                    System.out.println("Writing to " + path + " with value " + value);
                    Database.write(path, value, key, false);
                    response.setStatus(200);
                    System.out.println("Redirecting to /api/" + path);
                    response.sendRedirect(request.getContextPath() + "/api/" + path);
                } catch (Exception e) {
                    System.out.println("Error writing to file: " + e.getMessage());
                    response.setStatus(500);
                    response.getWriter().println("Error writing to file.");
                    return;
                }
            }
            default:
                response.setStatus(400);
                response.getWriter().println("Invalid action.");
                return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
