import org.json.JSONObject;
import java.util.Objects;

public record ClientMessageContent(String type, JSONObject data) {
    public ClientMessageContent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(data);
    }
}