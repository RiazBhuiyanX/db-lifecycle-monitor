package bg.riaz;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerIT {

    private static String url;
    private static String user;
    private static String password;

    @BeforeAll
    static void setup() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        user = dotenv.get("POSTGRES_USER");
        password = dotenv.get("POSTGRES_PASSWORD");
        String dbName = dotenv.get("POSTGRES_DB");
        String port = dotenv.get("POSTGRES_PORT");

        assertNotNull(user, "Конфигурацията POSTGRES_USER липсва в .env!");
        assertNotNull(password, "Конфигурацията POSTGRES_PASSWORD липсва в .env!");
        assertNotNull(dbName, "Конфигурацията POSTGRES_DB липсва в .env!");
        assertNotNull(port, "Конфигурацията POSTGRES_PORT липсва в .env!");

        url = "jdbc:postgresql://localhost:" + port + "/" + dbName;
    }

    @Test
    void testRealConnection() {
        assertNotNull(url, "URL не трябва да е null! Провери .env файла.");

        DatabaseManager manager = new DatabaseManager(url, user, password);

        assertDoesNotThrow(() -> {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String version = manager.checkVersion(conn);
                System.out.println("Connected to: " + version);
                assertTrue(version.contains("PostgreSQL"));
            }
        });
    }
}
