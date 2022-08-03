import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;

public class App {
    private static final int DEFAULT_PORT = 8887;
    private static final Dotenv dotenv = Dotenv.load();

    private static Integer parsePort(String portStr) {
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return DEFAULT_PORT;
    }

    private static int getPort() {
        var envPort = Optional.ofNullable(dotenv.get("WS_PORT"));
        return envPort.map(App::parsePort).orElse(DEFAULT_PORT);
    }

    public static void main(String[] args) {
        AppServer s = new AppServer(getPort());
        s.start();
        System.out.println("Server starting on " + s.getAddress() + " ...");
    }
}
