package bg.riaz;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String user = dotenv.get("POSTGRES_USER");
        String password = dotenv.get("POSTGRES_PASSWORD");
        String dbName = dotenv.get("POSTGRES_DB");
        String port = dotenv.get("POSTGRES_PORT");

        String url = "jdbc:postgresql://localhost:" + port + "/" + dbName;

        logger.info("Опит за свързване към: {}", url);

        if (dbName == null) {
            logger.error("Липсва .env файл или конфигурация!");
            logger.error("Моля, копирайте .env.example към .env и попълнете данните.");
            System.exit(1);
        }

        DatabaseManager dbManager = new DatabaseManager(url, user, password);

        while (true) {
            dbManager.performFullCheck();

            try {
                logger.info("Изчакване 10 секунди...");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}