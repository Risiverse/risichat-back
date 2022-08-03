import org.json.JSONException;
import org.json.JSONObject;

public abstract class ClientMessage {
    private final JSONObject message;

    public abstract JSONObject getParsedMessage();
    public abstract JSONObject validateMessage(JSONObject message);
    public abstract boolean shouldBroadcast();
    public abstract boolean shouldInsertIntoDB();

    public ClientMessage(JSONObject message) {
        this.message = message;
    }

    public static JSONObject validateClientMessage(String message) throws JSONException {
        JSONObject parsedClientMessage;
        try {
            parsedClientMessage = new JSONObject(message);
        } catch (JSONException exception) {
            throw new JSONException("Invalid message format. Be sure to send stringified JSON.");
        }
        try {
            parsedClientMessage.getString("type");
            parsedClientMessage.getJSONObject("data");
        } catch (JSONException exception) {
            throw new JSONException("'type' and 'data' fields are not in your message or has wrong values.");
        }
        return parsedClientMessage;
    }

    public JSONObject getMessage() {
        return message;
    }
}
