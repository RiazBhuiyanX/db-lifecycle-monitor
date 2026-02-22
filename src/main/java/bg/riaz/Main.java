package bg.riaz;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        Dotenv dotenv = Dotenv.load();

        String user = dotenv.get("POSTGRES_USER");
        String password = dotenv.get("POSTGRES_PASSWORD");
        String dbName = dotenv.get("POSTGRES_DB");
        String port = dotenv.get("POSTGRES_PORT");

        String url = "jdbc:postgresql://localhost:" + port + "/" + dbName;

        System.out.println("Опит за свързване към: " + url);

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Успешно свързване с Docker Postgres!");

                initializeDatabase(conn);

                String currentVersion = checkVersion(conn);
                int currentUsers = checkUserCount(conn);

                saveHeartbeat(conn, currentUsers, currentVersion);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при свързване: " + e.getMessage());
        }
    }

    public static String checkVersion(Connection conn) {
        String sql = "SELECT version()";

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String version = rs.getString(1);
                System.out.println("Версия: " + version);
                return version;
            }
        } catch (SQLException e) {
            System.err.println("Проблем при проверката: " + e.getMessage());
        }
        return "Unknown Version";
    }


    public static int checkUserCount(Connection conn) {
        String sql = "SELECT count(*) FROM pg_stat_activity WHERE backend_type = 'client backend'";

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Брой: " + count);
                return count;
            }
        } catch (SQLException e) {
            System.err.println("Проблем при проверката: " + e.getMessage());
        }
        return 0;
    }

    public static void initializeDatabase(Connection conn) {
        String sql = """
            CREATE TABLE IF NOT EXISTS heartbeats (
                id SERIAL PRIMARY KEY,
                checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                active_users INT,
                db_version TEXT,
                status TEXT
                )
            """;

        try (var stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Инфраструктурата е подготвена: Таблица 'heartbeats' е готова.");
        } catch (SQLException e) {
            System.err.println("Грешка при подготовка на базата: " + e.getMessage());
        }
    }

    public static void saveHeartbeat(Connection conn, int users, String version) {
        String sql = "INSERT INTO heartbeats (active_users, db_version, status) VALUES (?, ?, ?)";

        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, users);
            pstmt.setString(2, version);
            pstmt.setString(3, "Healthy");

            pstmt.executeUpdate();
            System.out.println("Успешен запис в историята на базата данни!");
        } catch (SQLException e) {
            System.err.println("Грешка при запис: " + e.getMessage());
        }
    }
}