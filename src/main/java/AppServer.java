import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

public class AppServer extends WebSocketServer {
    private final Dotenv dotenv = Dotenv.load();
    private final AppDatabase appDatabase = new AppDatabase(
            dotenv.get("MONGO_HOST"),
            dotenv.get("MONGO_DB"),
            dotenv.get("MONGO_COLLECTION"));

    public AppServer(int port) {
        super(new InetSocketAddress(port));
    }

    public String getClientMessageType(String message) {
        try {
            return new JSONObject(message).getString("type");
        } catch (JSONException error) {
            return null;
        }
    }

    public AppClientMessage getClientMessageClass(WebSocket senderWS, String message) {
        String type = getClientMessageType(message);
        try {
            return Map.of(
                    "newMessage", new AppChatMessage(senderWS, message)
            ).get(type);
        } catch (JSONException error) {
            return null;
        }
    }

    public void onMessageGlobal(WebSocket senderWS, String message) {
        AppClientMessage clientMessage = getClientMessageClass(senderWS, message);
        if (clientMessage == null) return;
        try {
            JSONObject parsedMessage = clientMessage.messageParser();
            if (clientMessage.shouldBroadcast()) {
                broadcast(parsedMessage.toString());
            }
            if (clientMessage.shouldInsertIntoDB()) {
                appDatabase.insertMessage(parsedMessage.getJSONObject("data").toString());
            }
        } catch (Error error) {
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        broadcast("{\"type\":\"newConnection\",\"data\":{\"message\":\"A new user has logged in.\"}}");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast("{\"type\":\"newConnection\",\"data\":{\"message\":\"A user has logged out.\"}}");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        onMessageGlobal(conn, message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        onMessageGlobal(conn, message.toString());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(100);
    }

}