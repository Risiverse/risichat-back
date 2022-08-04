import org.json.JSONException;
import org.json.JSONObject;

public sealed interface ClientMessage permits ClientChatMessage {
    JSONObject getParsedMessage();
    JSONObject validateMessage(JSONObject message);
    boolean shouldBroadcast();
    boolean shouldInsertIntoDB();

    static ClientMessageContent validateClientMessage(String message) throws JSONException {
        JSONObject jsonClientMessage;
        ClientMessageContent parsedClientMessage;
        try {
            jsonClientMessage = new JSONObject(message);
        } catch (JSONException exception) {
            throw new JSONException("Invalid message format. Be sure to send stringified JSON.");
        }
        try {
            parsedClientMessage = new ClientMessageContent(
                    jsonClientMessage.getString("type"),
                    jsonClientMessage.getJSONObject("data"));
        } catch (JSONException exception) {
            throw new JSONException("'type' and/or 'data' fields are not in your message or has wrong values.");
        }
        return parsedClientMessage;
    }
}
