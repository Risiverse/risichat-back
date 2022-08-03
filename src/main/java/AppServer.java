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

    public void sendMessageError(String message, int code, String data, WebSocket senderWS) {
        JSONObject response = new JSONObject();
        response.put("type", "error");
        response.put("status", code);
        response.put("message", message);
        response.put("data", data);

        senderWS.send(response.toString());
    }

    public ClientMessage getClientMessageClass(WebSocket senderWS, JSONObject message) throws JSONException {
        String type = message.getString("type");
        return Map.of(
                "newMessage", new ClientChatMessage(message)
        ).get(type);
    }

    public void onMessageGlobal(WebSocket senderWS, String message) {
        JSONObject validatedClientMessage;
        try {
            validatedClientMessage = ClientMessage.validateClientMessage(message);
        } catch (JSONException exception) {
            sendMessageError(
                    exception.getMessage(),
                    400,
                    message,
                    senderWS);
            return;
        }

        ClientMessage clientMessageClass = getClientMessageClass(senderWS, validatedClientMessage);
        if (clientMessageClass == null) {
            sendMessageError(
                    "This type of message does not exist.",
                    400,
                    message,
                    senderWS);
            return;
        }

        JSONObject parsedClientMessage;
        try {
            parsedClientMessage = clientMessageClass.getParsedMessage();
        } catch (JSONException exception) {
            sendMessageError(
                    exception.getMessage(),
                    400,
                    message,
                    senderWS);
            return;
        }
        if (clientMessageClass.shouldBroadcast()) {
            broadcast(parsedClientMessage.toString());
        }
        if (clientMessageClass.shouldInsertIntoDB()) {
            appDatabase.insertMessage(parsedClientMessage
                    .getJSONObject("data")
                    .toString()
            );
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