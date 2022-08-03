import org.json.JSONException;
import org.json.JSONObject;

public record ClientChatMessage(JSONObject message) implements ClientMessage {
    @Override
    public JSONObject getMessage() {
        return message;
    }

    @Override
    public JSONObject getParsedMessage() {
        JSONObject data = validateMessage(getMessage());
        JSONObject parsedMessage = new JSONObject();
        parsedMessage.put("type", "newMessage");
        parsedMessage.put("data", data);
        return parsedMessage;
    }

    @Override
    public JSONObject validateMessage(JSONObject message) throws JSONException {
        JSONObject messageData = message.getJSONObject("data");
        JSONObject validatedMessage = new JSONObject();
        validatedMessage.put("timestamp", System.currentTimeMillis());
        try {
            validatedMessage.put("username", messageData.getString("username"));
            validatedMessage.put("content", messageData.getString("content"));
        } catch (JSONException exception) {
            throw new JSONException("Data format or values are not valid and/or missing.");
        }
        return validatedMessage;
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
