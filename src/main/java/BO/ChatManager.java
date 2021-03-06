package BO;

import DB.DAL.ChatDb;
import ViewModel.ChatMessageViewModel;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.stream.Collectors;


public class ChatManager {
    private ChatDb db;

    public ChatManager(){
        db = new ChatDb();
    }

    public JsonObject sendMessage(ChatMessageViewModel chat) {
        return new JsonObject(Json.encodePrettily(ModelConverter.convertToChatMessageViewModel(db.addMessage(chat))));
    }

    public  JsonArray getChatMessagesBySenderAndReceiver(ChatMessageViewModel chat) {
        return new JsonArray(Json.encodePrettily(db.findChatMessagesBySenderAndReceiver(chat).stream().map(ModelConverter::convertToChatMessageViewModel).collect(Collectors.toList())));
    }
}
