import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClientMessageTest {

    @Test
    void validateClientMessage() {
        assertThrows(JSONException.class, ()->
                ClientMessage.validateClientMessage(""));
        assertThrows(JSONException.class, ()->
                ClientMessage.validateClientMessage("{\"type\":2, \"data\":{}}"));
        assertThrows(JSONException.class, ()->
                ClientMessage.validateClientMessage("{\"type\":\"2\", \"data\":[]}"));
        assertDoesNotThrow(()->
                ClientMessage.validateClientMessage("{\"type\":\"2\", \"data\":{}}"));

        String type = "newMessage";
        JSONObject data = new JSONObject(Map.of(
                "username", "gerard",
                "content", "hello, world"
        ));

        ClientMessageContent clientMessageContent = ClientMessage
                .validateClientMessage("{\"type\": \"newMessage\"," +
                        "\"data\": {\"username\": \"gerard\",\"content\": \"hello, world\"}}");

        assertEquals(clientMessageContent.type(), "newMessage");
        assertEquals(clientMessageContent.data()
                .getString("username"), "gerard");
        assertEquals(clientMessageContent.data()
                .getString("content"), "hello, world");
    }

    @Test
    void getClientMessageClass() {
        //TODO
    }
}