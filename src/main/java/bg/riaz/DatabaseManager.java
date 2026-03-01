package bg.riaz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private final String url;
    private final String user;
    private final String password;

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void performFullCheck() {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                logger.info("Успешно свързване с Docker Postgres!");

                initializeDatabase(conn);

                String currentVersion = checkVersion(conn);
                int currentUsers = checkUserCount(conn);

                saveHeartbeat(conn, currentUsers, currentVersion);
            }
        } catch (SQLException e) {
            logger.error("Грешка при свързване: ", e);
        }
    }

    public String checkVersion(Connection conn) {
        String sql = "SELECT version()";

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String version = rs.getString(1);
                logger.info("Версия: {}", version);
                return version;
            }
        } catch (SQLException e) {
            logger.error("Проблем при проверката на версията: ", e);
        }
        return "Unknown Version";
    }


    public int checkUserCount(Connection conn) {
        String sql = "SELECT count(*) FROM pg_stat_activity WHERE backend_type = 'client backend'";

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("Брой: {}", count);
                return count;
            }
        } catch (SQLException e) {
            logger.error("Проблем при проверката на броя user-и: ", e);
        }
        return 0;
    }


    private void initializeDatabase(Connection conn) {
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
            logger.info("Инфраструктурата е подготвена: Таблица 'heartbeats' е готова.");
        } catch (SQLException e) {
            logger.error("Грешка при подготовка на базата: ", e);
        }
    }

    public void saveHeartbeat(Connection conn, int users, String version) {
        String sql = "INSERT INTO heartbeats (active_users, db_version, status) VALUES (?, ?, ?)";

        try (var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, users);
            pstmt.setString(2, version);
            pstmt.setString(3, "Healthy");

            pstmt.executeUpdate();
            logger.info("Успешен запис в историята на базата данни!");
        } catch (SQLException e) {
            logger.error("Грешка при запис: ", e);
        }
    }
}
