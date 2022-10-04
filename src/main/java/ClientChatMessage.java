import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public final class ClientChatMessage implements ClientMessage {
    private final ClientChatMessageContent validClientChatMessageContent;

    public ClientChatMessage(JSONObject message) {
        Objects.requireNonNull(message);
        validClientChatMessageContent = validateMessage(message);
    }

    private ClientChatMessageContent validateMessage(JSONObject message) throws JSONException {
        try {
            return new ClientChatMessageContent(
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
                "timestamp", validClientChatMessageContent.timestamp(),
                "username", validClientChatMessageContent.username(),
                "content", validClientChatMessageContent.content()));
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
