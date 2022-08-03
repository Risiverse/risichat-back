import org.java_websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AppClientMessage {
    private final WebSocket senderWS;
    private final JSONObject messageData;

    public abstract JSONObject messageParser();
    public abstract boolean shouldBroadcast();
    public abstract boolean shouldInsertIntoDB();

    public AppClientMessage(WebSocket senderWS, String message) {
        this.senderWS = senderWS;

        try {
            this.messageData = new JSONObject(message).getJSONObject("data");
        } catch (JSONException error) {
            this.sendMessageError(
                    "Invalid WS message. Be sure to send stringified JSON.",
                    400);
            throw new JSONException("Invalid WS message.");
        }
    }

    public void sendMessageError(String message, int code) {
        JSONObject response = new JSONObject();
        response.put("type", "error");
        response.put("status", code);
        response.put("message", message);
        response.put("data", this.messageData);

        this.senderWS.send(response.toString());
    }

    public JSONObject getMessageData() {
        return messageData;
    }
}
