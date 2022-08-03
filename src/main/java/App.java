import io.github.cdimascio.dotenv.Dotenv;

public class App {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        int port = 8887;

        try {
            port = Integer.parseInt(dotenv.get("WS_PORT"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        AppServer s = new AppServer(port);
        s.start();

        System.out.println("Server starting on " + s.getAddress() + " ...");
    }
}
