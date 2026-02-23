package bg.riaz;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    public String checkVersion(Connection conn) {
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


    public int checkUserCount(Connection conn) {
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


    public void initializeDatabase(Connection conn) {
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

    public void saveHeartbeat(Connection conn, int users, String version) {
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
