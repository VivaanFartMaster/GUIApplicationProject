import java.io.*;
import java.util.*;

public class UserManager {
    private File file;
    private Map<String, User> users = new HashMap<>();

    public UserManager(String filename) {
        file = new File(filename);
        loadUsers();
    }

    private void loadUsers() {
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.put(parts[0], new User(parts[0], parts[1], Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void saveAll() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (User u : users.values()) {
                pw.println(u.username + "," + u.password + "," + u.highScore);
            }
            pw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        User newUser = new User(username, password, 0);
        users.put(username, newUser);
        saveAll();
        return true;
    }

    public User login(String username, String password) {
        User u = users.get(username);
        if (u != null && u.password.equals(password)) {
            return u; 
        } else {
            return null; 
        }
    }
    
    public void saveUserScore(User u) {
        if (users.containsKey(u.username)) {
            users.put(u.username, u);
            saveAll();
        }
    }
}