import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Voter {
    String aadhaar, username, password;
    boolean hasVoted = false;

    Voter(String a, String u, String p) {
        aadhaar = a;
        username = u;
        password = p;
    }
}

public class VotingSystemGUI extends JFrame {

    static HashMap<String, Voter> voters = new HashMap<>();
    static LinkedHashMap<String, Integer> votes = new LinkedHashMap<>();

    // ✅ Vote history with time & date
    static ArrayList<String> voteHistory = new ArrayList<>();

    // 🔐 ADMIN CREDENTIALS
    static final String ADMIN_ID = "admin123";
    static final String ADMIN_PASS = "secure@2025";

    public VotingSystemGUI() {

        votes.put("Candidate A", 0);
        votes.put("Candidate B", 0);
        votes.put("Candidate C", 0);

        setTitle("Online Voting System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton adminBtn = new JButton("Admin");
        JButton voterBtn = new JButton("Voter");

        adminBtn.addActionListener(e -> adminLogin());
        voterBtn.addActionListener(e -> voterMenu());

        setLayout(new GridLayout(3, 1));
        add(new JLabel("Welcome to Online Voting System", JLabel.CENTER));
        add(adminBtn);
        add(voterBtn);

        setVisible(true);
    }

    void adminLogin() {
        JTextField id = new JTextField();
        JPasswordField pass = new JPasswordField();

        Object[] fields = {
                "Admin ID:", id,
                "Password:", pass
        };

        int option = JOptionPane.showConfirmDialog(
                this, fields, "Admin Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            if (id.getText().equals(ADMIN_ID)
                    && new String(pass.getPassword()).equals(ADMIN_PASS)) {
                showResults();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin Credentials!");
            }
        }
    }

    void voterMenu() {
        String[] options = {"Register", "Login"};
        int choice = JOptionPane.showOptionDialog(
                this, "Choose Option", "Voter Menu",
                0, 3, null, options, options[0]);

        if (choice == 0) registerVoter();
        else if (choice == 1) loginVoter();
    }

    void registerVoter() {
        JTextField aadhaar = new JTextField();
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();

        Object[] fields = {
                "Aadhaar (12 digits):", aadhaar,
                "Username:", user,
                "Password:", pass
        };

        int option = JOptionPane.showConfirmDialog(
                this, fields, "Voter Registration", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {

            if (!aadhaar.getText().matches("\\d{12}")) {
                JOptionPane.showMessageDialog(this, "Invalid Aadhaar!");
                return;
            }

            if (voters.containsKey(aadhaar.getText())) {
                JOptionPane.showMessageDialog(this, "Already Registered!");
                return;
            }

            voters.put(aadhaar.getText(),
                    new Voter(aadhaar.getText(),
                            user.getText(),
                            new String(pass.getPassword())));

            JOptionPane.showMessageDialog(this, "Registration Successful!");
        }
    }

    void loginVoter() {
        JTextField aadhaar = new JTextField();
        JPasswordField pass = new JPasswordField();

        Object[] fields = {
                "Aadhaar:", aadhaar,
                "Password:", pass
        };

        int option = JOptionPane.showConfirmDialog(
                this, fields, "Voter Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {

            Voter v = voters.get(aadhaar.getText());

            if (v == null) {
                JOptionPane.showMessageDialog(this, "Voter Not Found!");
                return;
            }

            if (!v.password.equals(new String(pass.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Wrong Password!");
                return;
            }

            if (v.hasVoted) {
                JOptionPane.showMessageDialog(this, "Already Voted!");
                return;
            }

            castVote(v);
        }
    }

    void castVote(Voter v) {

        String[] candidates = votes.keySet().toArray(new String[0]);

        int choice = JOptionPane.showOptionDialog(
                this, "Select Candidate", "Vote",
                0, 3, null, candidates, candidates[0]);

        if (choice >= 0) {

            votes.put(candidates[choice],
                    votes.get(candidates[choice]) + 1);

            v.hasVoted = true;

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dtf =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            String record = "Aadhaar: " + v.aadhaar +
                    " | Voted For: " + candidates[choice] +
                    " | Time: " + now.format(dtf);

            voteHistory.add(record);

            JOptionPane.showMessageDialog(
                    this, "Vote Cast Successfully for " + candidates[choice]);
        }
    }

    void showResults() {

        StringBuilder result = new StringBuilder("Voting Results:\n\n");

        for (Map.Entry<String, Integer> e : votes.entrySet()) {
            result.append(e.getKey())
                  .append(" : ")
                  .append(e.getValue())
                  .append(" votes\n");
        }

        result.append("\n----- Vote Date & Time Log -----\n");

        for (String log : voteHistory) {
            result.append(log).append("\n");
        }

        JOptionPane.showMessageDialog(this, result.toString());

        // ✅ ADDED: Show Graph
        new GraphFrame(votes);
    }

    public static void main(String[] args) {
        new VotingSystemGUI();
    }
}

/* ======================================================
   ✅ ADDED CLASS: GRAPHICAL BAR CHART (NO EXISTING CHANGE)
   ====================================================== */

class GraphFrame extends JFrame {

    LinkedHashMap<String, Integer> votes;

    GraphFrame(LinkedHashMap<String, Integer> votes) {
        this.votes = votes;

        setTitle("Voting Results - Graph");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void paint(Graphics g) {
        super.paint(g);

        int x = 80;
        int yBase = 300;
        int barWidth = 60;

        g.drawString("Voting Result Graph", 180, 60);

        for (Map.Entry<String, Integer> e : votes.entrySet()) {

            int height = e.getValue() * 30;

            g.setColor(Color.BLUE);
            g.fillRect(x, yBase - height, barWidth, height);

            g.setColor(Color.BLACK);
            g.drawString(e.getKey(), x, yBase + 20);
            g.drawString(String.valueOf(e.getValue()),
                    x + 20, yBase - height - 10);

            x += 100;
        }
    }
}