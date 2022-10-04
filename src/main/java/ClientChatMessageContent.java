import java.util.Objects;

record ClientChatMessageContent(long timestamp, String username, String content) {
    public ClientChatMessageContent {
        Objects.requireNonNull(username);
        Objects.requireNonNull(content);
        if (timestamp < 0) throw new IllegalArgumentException("timestamps bust be > 0.");
    }
}
