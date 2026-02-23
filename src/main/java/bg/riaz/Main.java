package bg.riaz;

import io.github.cdimascio.dotenv.Dotenv;

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

        DatabaseManager dbManager = new DatabaseManager(url, user, password);

        while (true) {
            dbManager.performFullCheck();

            try {
                System.out.println("Изчакване 10 секунди...");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}