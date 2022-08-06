import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClientMessageContentTest {

    @Test
    void ClientMessageContent() {
        assertThrows(NullPointerException.class, ()->
                new ClientMessageContent(null, new JSONObject()));
        assertThrows(NullPointerException.class, ()->
                new ClientMessageContent("pouet", null));
        assertDoesNotThrow(()->
                new ClientMessageContent("pouet", new JSONObject()));
    }

    @Test
    void getJSONformattedContent() {
        var clientMessageContent = new ClientMessageContent("newMessage", new JSONObject(
                Map.of(
                        "username", "didier",
                        "content", "salut à tous")
        ));

        assertEquals(clientMessageContent.type(), "newMessage");
        assertEquals(clientMessageContent.data()
                .getString("username"), "didier");
        assertEquals(clientMessageContent.data()
                .getString("content"), "salut à tous");
    }
}