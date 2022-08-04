import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

public record ClientMessageContent(String type, JSONObject data) {
    public ClientMessageContent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(data);
    }

    public JSONObject getJSONformattedContent() {
        return new JSONObject(Map.of(
                "type", type,
                "data", data));
    }
}