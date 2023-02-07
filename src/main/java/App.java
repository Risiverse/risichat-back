import io.github.cdimascio.dotenv.Dotenv;

public class App {
    private static int parsePortFromEnvFile(Dotenv dotenv) throws RuntimeException {
        try {
            return Integer.parseInt(dotenv.get("WS_PORT"));
        } catch (NumberFormatException exception) {
            throw new RuntimeException("WS server port is missing or incorrect in .env file.");
        }
    }

    public static void main(String[] args) {
        var dotenv = Dotenv.load();
        int port = parsePortFromEnvFile(dotenv);

        var server = new AppServer(port);
        server.start();

        System.out.println("Server starting on " + server.getAddress() + " ...");
    }
}
