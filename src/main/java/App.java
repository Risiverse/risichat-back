import io.github.cdimascio.dotenv.Dotenv;

public class App {
    public static void main(String[] args) {
        var dotenv = Dotenv.load();
        int port;

        try {
            port = Integer.parseInt(dotenv.get("WS_PORT"));
        } catch (NumberFormatException exception) {
            System.out.println("WS server port is missing in .env.");
            return;
        }

        var server = new AppServer(port);
        server.start();

        System.out.println("Server starting on " + server.getAddress() + " ...");
    }
}
