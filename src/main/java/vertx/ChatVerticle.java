package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.*;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Marthin on 2016-12-14.
 *
 * https://github.com/vert-x3/vertx-examples/tree/master/web-examples/src/main/java/io/vertx/example/web/chat
 */
public class ChatVerticle extends AbstractVerticle{
    private HashMap<String,<Integer,ServerWebSocket>> socketHashMap;

    public ChatVerticle() {
        socketHashMap = new HashMap<>();
    }

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer().websocketHandler(serverWebSocket -> {
            System.out.println("Connected!" + serverWebSocket.toString());
            serverWebSocket.closeHandler(close -> {
                socketHashMap.remove(vertx.sharedData().)
                System.out.println("closed" + socketHashMap.size());

            });
            serverWebSocket.frameHandler(webSocketFrame -> {
                System.out.println(webSocketFrame.textData());
                serverWebSocket.write(Buffer.buffer("hello"));
                //socketHashMap.put(serverWebSocket.textHandlerID(),serverWebSocket);
                System.out.println(socketHashMap.size());
            });
        }).requestHandler(req -> {
            req.response().sendFile("webroot/index.html");
        }).listen(8080);
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
        eb.consumer("chat-server").handler(message -> {
            // Create a timestamp string
            String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
            // Send the message back out to all clients with the timestamp prepended.
            eb.publish("chat.34", timestamp + ": " + message.body());
        });
    }

}
