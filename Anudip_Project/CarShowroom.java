import java.sql.*;
import java.util.*;

public class CarShowroom {

    static final String URL = "jdbc:mysql://localhost:3306/carshowroom";
    static final String USER = "root";        // change if needed
    static final String PASS = "12345";       // change if needed
    static Scanner sc = new Scanner(System.in);
    static Connection con;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("🚗 Welcome to Car Showroom System!");

            while (true) {
                System.out.println("\n1. Create Account");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();
                sc.nextLine();

                if (ch == 1) createAccount();
                else if (ch == 2) loginUser();
                else if (ch == 3) break;
                else System.out.println("Invalid choice!");
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void createAccount() throws SQLException {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter State: ");
        String state = sc.nextLine();
        System.out.print("Enter Contact Number: ");
        String contact = sc.nextLine();
        System.out.print("Create Username: ");
        String username = sc.nextLine();
        System.out.print("Create Password: ");
        String password = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO users(name, state, contact, username, password, orders) VALUES (?,?,?,?,?,?)");
        ps.setString(1, name);
        ps.setString(2, state);
        ps.setString(3, contact);
        ps.setString(4, username);
        ps.setString(5, password);
        ps.setString(6, "None");
        ps.executeUpdate();
        System.out.println("✅ Account created successfully!");
    }

    static void loginUser() throws SQLException {
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM users WHERE username=? AND password=?");
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("\n✅ Login successful! Welcome, " + rs.getString("name"));
            userMenu(rs.getInt("id"));
        } else {
            System.out.println("❌ Invalid credentials!");
        }
    }

    static void userMenu(int userId) throws SQLException {
        while (true) {
            System.out.println("\n--- Commands ---");
            System.out.println("1. show all");
            System.out.println("2. view my category");
            System.out.println("3. buy car");
            System.out.println("4. my profile");
            System.out.println("5. logout");
            System.out.print("Enter command: ");
            String cmd = sc.nextLine().toLowerCase();

            switch (cmd) {
                case "show all": showAllCars(); break;
                case "view my category": viewByCategory(); break;
                case "buy car": buyCar(userId); break;
                case "my profile": showProfile(userId); break;
                case "logout": return;
                default: System.out.println("Unknown command!");
            }
        }
    }

    static void showAllCars() throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM cars");
        System.out.println("\n🚘 All Cars:");
        while (rs.next()) {
            System.out.printf("%d. %s %s (%s) - Rs.%d - %s [%s]%n",
                    rs.getInt("id"), rs.getString("brand"), rs.getString("model"),
                    rs.getString("variant"), rs.getInt("price"),
                    rs.getString("color"), rs.getString("category"));
        }
    }

    static void viewByCategory() throws SQLException {
        System.out.println("\nCategories: Sedan, Hatchback, SUV, Pickup, MPV");
        System.out.print("Enter category: ");
        String cat = sc.nextLine();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM cars WHERE category=?");
        ps.setString(1, cat);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.printf("%d. %s %s (%s) - Rs.%d - %s%n",
                    rs.getInt("id"), rs.getString("brand"), rs.getString("model"),
                    rs.getString("variant"), rs.getInt("price"), rs.getString("color"));
        }
    }

    static void buyCar(int userId) throws SQLException {
        showAllCars();
        System.out.print("\nEnter Car ID to buy: ");
        int carId = sc.nextInt();
        sc.nextLine();

        PreparedStatement carPs = con.prepareStatement("SELECT brand, model FROM cars WHERE id=?");
        carPs.setInt(1, carId);
        ResultSet carRs = carPs.executeQuery();
        if (carRs.next()) {
            String car = carRs.getString("brand") + " " + carRs.getString("model");
            PreparedStatement ps = con.prepareStatement("UPDATE users SET orders=? WHERE id=?");
            ps.setString(1, car);
            ps.setInt(2, userId);
            ps.executeUpdate();
            System.out.println("✅ You successfully purchased: " + car);
        } else {
            System.out.println("❌ Invalid Car ID!");
        }
    }

    static void showProfile(int userId) throws SQLException {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE id=?");
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            System.out.println("\n👤 My Profile");
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("State: " + rs.getString("state"));
            System.out.println("Contact: " + rs.getString("contact"));
            System.out.println("Orders: " + rs.getString("orders"));
        }
    }
}
