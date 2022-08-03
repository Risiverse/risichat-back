import org.java_websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

public class AppChatMessage extends AppClientMessage {
    public AppChatMessage(WebSocket senderWS, String message) {
        super(senderWS, message);
    }

    @Override
    public JSONObject messageParser() {
        long timestamp;
        String username, content;

        try {
            JSONObject messageData = getMessageData();
            timestamp = System.currentTimeMillis();
            username = messageData.getString("username");
            content = messageData.getString("content");
        } catch (JSONException error) {
            sendMessageError("JSON Format not valid.", 400);
            return null;
        }

        JSONObject parsedMessage = new JSONObject();
        parsedMessage.put("type", "newMessage");

        JSONObject data = new JSONObject();
        data.put("timestamp", timestamp);
        data.put("username", username);
        data.put("content", content);

        parsedMessage.put("data", data);
        return parsedMessage;
    }

    @Override
    public boolean shouldBroadcast() {
        return true;
    }

    @Override
    public boolean shouldInsertIntoDB() {
        return true;
    }
}
