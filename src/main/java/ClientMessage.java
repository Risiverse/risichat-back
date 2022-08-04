import org.json.JSONException;
import org.json.JSONObject;

public sealed interface ClientMessage permits ClientChatMessage {
    JSONObject getMessage();
    JSONObject getParsedMessage();
    JSONObject validateMessage(JSONObject message);
    boolean shouldBroadcast();
    boolean shouldInsertIntoDB();

    static JSONObject validateClientMessage(String message) throws JSONException {
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
            throw new JSONException("'type' and/or 'data' fields are not in your message or has wrong values.");
        }
        return parsedClientMessage;
    }
}
