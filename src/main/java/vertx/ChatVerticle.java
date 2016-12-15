package vertx;

import BO.ChatManager;
import ViewModel.ChatMessageViewModel;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.*;


/**
 * Created by Marthin on 2016-12-14.
 *
 * https://github.com/vert-x3/vertx-examples/tree/master/web-examples/src/main/java/io/vertx/example/web/chat
 */
public class ChatVerticle extends AbstractVerticle{
    private ChatManager chatManager;

    public ChatVerticle(){
        chatManager = new ChatManager();
    }
    @Override
    public void start() {
        Router router = Router.router(vertx);
        BridgeOptions options = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("chat.server"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("chat\\.[0-9]+"));

        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options, event -> {
            if (event.type() == BridgeEventType.SOCKET_CREATED) {
                System.out.println("A socket was created");
            }
            event.complete(true);
        }));

        // Create a router endpoint for the static content.
        router.route().handler(StaticHandler.create());
        // Start the web server and tell it to use the router to handle requests.
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

        EventBus eb = vertx.eventBus();
        // Register to listen for messages coming IN to the server
        eb.consumer("chat.server").handler(message -> {
            JsonObject json = new JsonObject(message.body().toString());
            System.out.println("Server got:" + json.toString());
            JsonObject chatMsg = new JsonObject(json.getJsonObject("chatMessageViewModel").toString());
            switch (json.getString("action")){
                case "getMessages":
                    chatManager.getChatMessagesBySenderAndReceiver(Json.decodeValue(chatMsg.toString(), ChatMessageViewModel.class));
                    eb.publish("chat."+json.getInteger("conversationId"), "Willkommen");
                    break;
                case "sendMessage":
                    eb.publish("chat."+json.getInteger("conversationId"), chatManager.sendMessage(Json.decodeValue(chatMsg.toString(), ChatMessageViewModel.class)).toString());
                    break;
            }
        });
    }
}
