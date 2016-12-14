package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.*;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Created by Marthin on 2016-12-14.
 *
 * https://github.com/vert-x3/vertx-examples/tree/master/web-examples/src/main/java/io/vertx/example/web/chat
 */
public class ChatVerticle extends AbstractVerticle{
    SockJSSocket hej;
    @Override
    public void start() {
        Router router = Router.router(vertx);

        // Allow events for the designated addresses in/out of the event bus bridge
        BridgeOptions options = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("chat-server"));

        // Create the event bus bridge and add it to the router.
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options, event -> {

            // You can also optionally provide a handler like this which will be passed any events that occur on the bridge
            // You can use this for monitoring or logging, or to change the raw messages in-flight.
            // It can also be used for fine grained access control.

            if (event.type() == BridgeEventType.SOCKET_CREATED) {
                System.out.println("A socket was created");
                hej = event.socket();
            }

            // This signals that it's ok to process the event
            event.complete(true);

        }));

        // Create a router endpoint for the static content.
        router.route().handler(StaticHandler.create());

        // Start the web server and tell it to use the router to handle requests.
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

        EventBus eb = vertx.eventBus();
        // Register to listen for messages coming IN to the server
        MessageConsumer<Object> handler = eb.consumer("chat-server").handler(message -> {
            // Create a timestamp string
            String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
            // Send the message back out to all clients with the timestamp prepended.
            eb.publish("chat-client", timestamp + ": " + message.body());
        });
    }
}
