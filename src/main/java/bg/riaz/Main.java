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
            }
        } catch (SQLException e) {
            System.err.println("Грешка при свързване: " + e.getMessage());
        }
    }
}