import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public sealed interface ClientMessage permits ClientChatMessage {
    ClientMessageContent getParsedMessage();
    boolean shouldBroadcast();
    boolean shouldInsertIntoDB();

    static Optional<ClientMessage> getClientMessageClass(ClientMessageContent message) throws JSONException {
        return switch (message.type()) {
            case "newMessage" -> Optional.of(new ClientChatMessage(message.data()));
            default -> Optional.empty();
        };
    }

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
