import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClientChatMessageTest {

    @Test
    void clientChatMessage() {
        assertThrows(JSONException.class, ()->
                new ClientChatMessage(new JSONObject()));
        assertThrows(JSONException.class, ()->
                new ClientChatMessage(new JSONObject(Map.of(
                        "username", "pouet",
                        "conten", "hello"))
                ));
        assertThrows(JSONException.class, ()->
                new ClientChatMessage(new JSONObject(Map.of(
                        "username", 2,
                        "content", "hello"))
                ));
        assertDoesNotThrow(()->
                new ClientChatMessage(new JSONObject(Map.of(
                        "username", "jacquie",
                        "content", "hello"))
                ));
    }

    @Test
    void getParsedMessage() {
        ClientChatMessage clientChatMessage = new ClientChatMessage(
                new JSONObject(Map.of(
                        "username", "phillipe",
                        "content", "bonsoir")
                ));

        ClientMessageContent clientMessageContent = clientChatMessage.getParsedMessage();

        assertEquals(clientMessageContent.type(), "newMessage");
        assertEquals(clientMessageContent.data()
                .getString("username"), "phillipe");
        assertEquals(clientMessageContent.data()
                .getString("content"), "bonsoir");
    }

    @Test
    void MessageContent() {
        assertThrows(NullPointerException.class, ()->
                new ClientChatMessageContent(42424242, null, "salut"));
        assertThrows(NullPointerException.class, ()->
                new ClientChatMessageContent(42424242, "marcel", null));
        assertThrows(IllegalArgumentException.class, ()->
                new ClientChatMessageContent(-124, "marcel", "salut"));
        assertDoesNotThrow(()->
                new ClientChatMessageContent(69696969, "marcel", "salut"));
    }

    @Test
    void shouldBroadcast() {
        ClientChatMessage clientChatMessage = new ClientChatMessage(new JSONObject(Map.of(
                "username", "jacquie",
                "content", "hello"
        )));
        assertTrue(clientChatMessage.shouldBroadcast());
    }

    @Test
    void shouldInsertIntoDB() {
        ClientChatMessage clientChatMessage = new ClientChatMessage(new JSONObject(Map.of(
                "username", "jacquie",
                "content", "hello"
        )));
        assertTrue(clientChatMessage.shouldInsertIntoDB());
    }
}