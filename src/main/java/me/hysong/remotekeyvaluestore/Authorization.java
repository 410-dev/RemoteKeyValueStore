package me.hysong.remotekeyvaluestore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


@WebServlet("/auth")
public class Authorization extends HttpServlet {

    static class AuthorizationTrials {
        public static final int MAX_TRIALS = 5;
        public static final int LOCKOUT_TIME = 60000 * 10; // 10 minutes
        public int trials = 0;
        public long lastTrial = 0;

        public boolean canTry() {
            if (System.currentTimeMillis() - lastTrial > LOCKOUT_TIME) {
                trials = 0;
            }
            return trials < MAX_TRIALS;
        }
    }

    private static final HashMap<String, AuthorizationTrials> trials = new HashMap<>();

    public static boolean pass(String id, String key) {
        try {
            System.out.println("Checking pass for " + id + " with key " + key + " at " + ("/auth/" + id.replace(File.separator, ".").replace("/", ".")));
            String authValue = Database.read("/auth/" + id.replace(File.separator, ".").replace("/", "."));
            System.out.println("Condition: " + ((authValue == null && (key == null || key.isEmpty())) || (authValue != null && authValue.equals(CoreSHA.hash512(key)))));
            return (authValue == null && (key == null || key.isEmpty())) || (authValue != null && authValue.equals(CoreSHA.hash512(key)));
        } catch (Exception e) {
            return false;
        }
    }

    public static void makeAuth(String id, String key) {
        try {
            System.out.println("Making auth for " + id + " with key " + key);
            Database.write("/auth/" + id.replace(File.separator, ".").replace("/", "."), CoreSHA.hash512(key), "",false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void revokeAuth(String id) {
        try {
            System.out.println("Revoking auth for " + id);
            String path = "/auth/" + id.replace(File.separator, ".").replace("/", ".");
            if (Database.read(path) == null) {
                return;
            }
            Database.delete(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean unauthorized(HttpSession session) {
        if (session == null) {
            System.out.println("Session is null.");
            return true;
        }
        if (session.getAttribute("main.authorization") == null) {
            System.out.println("Session does not have main.authorization.");
            return true;
        }
        try {
            String authValue = Database.read("/auth/main.authorization");
            System.out.println("Auth value: " + authValue + " Session value: " + session.getAttribute("main.authorization"));
            return authValue == null || !authValue.equals(session.getAttribute("main.authorization"));
        } catch (Exception e) {
            System.out.println("Error reading auth value.");
            return true;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String key = request.getParameter("key");
        if (key == null) {
            response.setStatus(400);
            return;
        }
        String ip = request.getRemoteAddr();
        if (!trials.containsKey(ip)) {
            trials.put(ip, new AuthorizationTrials());
        }
        if (!trials.get(ip).canTry()) {
            response.setStatus(429);
            response.getWriter().println("Too many trials.");
            return;
        }
        if (pass("main.authorization", key)) {
            System.out.println("Authorized.");
            response.setStatus(200);
            request.getSession().setAttribute("main.authorization", CoreSHA.hash512(key));
            String redirect = request.getParameter("redirect");
            response.getWriter().println("Authorized.");
            trials.get(ip).trials = 0;
            if (redirect != null) {
                System.out.println("Redirecting to: " + request.getContextPath() + "/" + redirect);
                response.sendRedirect(request.getContextPath() + "/" + redirect);
            }
        } else {
            System.out.println("Login failed. Expected key: " + CoreSHA.hash512(key) + " Received key: " + key);
            trials.get(ip).trials++;
            trials.get(ip).lastTrial = System.currentTimeMillis();
            response.setStatus(401);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

}
