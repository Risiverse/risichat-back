import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public sealed interface ClientMessage permits ClientChatMessage {
    JSONObject getMessage();
    JSONObject getParsedMessage();
    JSONObject validateMessage(JSONObject message);
    boolean shouldBroadcast();
    boolean shouldInsertIntoDB();

    static Optional<JSONObject> validateClientMessage(String message) throws JSONException {
        var msg = new JSONObject(message);
        JSONObject parsedMsg = switch (msg.getString("type")) {
            case "newMessage" -> new ClientChatMessage(msg).getParsedMessage();
            default -> null;
        };
        return Optional.ofNullable(parsedMsg);
    }
}