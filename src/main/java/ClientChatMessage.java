import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

record MessageContent(long timestamp, String username, String content) {
    public MessageContent {
        Objects.requireNonNull(username);
        Objects.requireNonNull(content);
        if (timestamp < 0) throw new IllegalArgumentException("timestamps bust be > 0.");
    }
}

public final class ClientChatMessage implements ClientMessage {
    private final MessageContent validMessageContent;

    public ClientChatMessage(JSONObject message) {
        Objects.requireNonNull(message);
        validMessageContent = validateMessage(message);
    }

    private MessageContent validateMessage(JSONObject message) throws JSONException {
        try {
            return new MessageContent(
                    System.currentTimeMillis(),
                    message.getString("username"),
                    message.getString("content"));
        } catch (JSONException exception) {
            throw new JSONException("Data format or values are not valid and/or missing.");
        }
    }

    @Override
    public ClientMessageContent getParsedMessage() {
        JSONObject parsedMessageContent = new JSONObject(Map.of(
                "timestamp", validMessageContent.timestamp(),
                "username", validMessageContent.username(),
                "content", validMessageContent.content()));
        return new ClientMessageContent("newMessage", parsedMessageContent);
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
