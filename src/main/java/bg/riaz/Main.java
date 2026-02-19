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

                checkVersion(conn);
                checkUserCount(conn);
            }
        } catch (SQLException e) {
            System.err.println("Грешка при свързване: " + e.getMessage());
        }
    }

    public static void checkVersion(Connection conn) {
        String sql = "SELECT version()";

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Версия: " + rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("Проблем при проверката: " + e.getMessage());
        }
    }


    public static void checkUserCount(Connection conn) {
        String sql = "SELECT count(*) FROM pg_stat_activity WHERE backend_type = 'client backend'";

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                System.out.println("Брой: " + rs.getString(1));
            }
        } catch (SQLException e) {
            System.err.println("Проблем при проверката: " + e.getMessage());
        }
    }
}