import io.vertx.core.Vertx;
import vertx.ChatVerticle;

/**
 * Created by Marthin on 2016-12-14.
 */
public class Main {
    public static void main(String[] args) {
        Vertx v = Vertx.vertx();
        v.deployVerticle(new ChatVerticle());
    }
}
