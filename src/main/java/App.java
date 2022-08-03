import io.github.cdimascio.dotenv.Dotenv;

public class App {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String hostname = "localhost";
        int port = 8887;

        try {
            hostname = dotenv.get("WS_HOST");
            port = Integer.parseInt(dotenv.get("WS_PORT"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        AppServer s = new AppServer(hostname, port);
        s.start();

        System.out.println("Server starting on " + s.getAddress() + " ...");
    }
}
