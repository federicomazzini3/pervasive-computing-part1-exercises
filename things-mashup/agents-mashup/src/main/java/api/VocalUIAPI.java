package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface VocalUIAPI {

    String getId();

    /** PROPERTIES **/
    Future<String> getCommand();

    /** EVENTS **/

    void subscribeToNewCommand(Handler<String> handler);
}
