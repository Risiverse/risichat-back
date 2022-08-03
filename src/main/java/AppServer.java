import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;

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

    public Optional<ClientMessage> getClientMessageClass(WebSocket senderWS, JSONObject message) throws JSONException {
        String type = message.getString("type");
        return Optional.ofNullable(Map.of(
                "newMessage", new ClientChatMessage(message)
        ).get(type));
    }

    public void handleClientMessage(ClientMessage clientMessage, WebSocket senderWS) {
        JSONObject parsedClientMessage;
        try {
            parsedClientMessage = clientMessage.getParsedMessage();
        } catch (JSONException exception) {
            sendMessageError(
                    exception.getMessage(),
                    400,
                    clientMessage.getMessage().toString(),
                    senderWS);
            return;
        }
        if (clientMessage.shouldBroadcast()) {
            broadcast(parsedClientMessage.toString());
        }
        if (clientMessage.shouldInsertIntoDB()) {
            appDatabase.insertMessage(parsedClientMessage
                    .getJSONObject("data")
                    .toString());
        }
    }

    public void handleValidClientMessage(JSONObject validClientMessage, WebSocket senderWS, String message) {
        var optClientMessage = getClientMessageClass(senderWS, validClientMessage);
        optClientMessage.ifPresentOrElse(
                (clientMessage)->handleClientMessage(clientMessage, senderWS),
                ()->sendMessageError(
                        "This type of message does not exist.",
                        400,
                        message,
                        senderWS));
    }

    public void onMessageGlobal(WebSocket senderWS, String message) {
        var optValidClientMessage = ClientMessage.validateClientMessage(message);
        optValidClientMessage.ifPresentOrElse(
                (validCliMsg)->handleValidClientMessage(validCliMsg, senderWS, message),
                ()->sendMessageError(
                        "Invalid message format. Be sure to send stringified" +
                                "JSON that match a valid message format.",
                        400,
                        message,
                        senderWS)
        );
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