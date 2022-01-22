package api;

import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface VocalUIAPI {

    String getId();

    /** PROPERTIES **/
    Future<String> getCommand();

    Future<Void> setCommand(String command);

    /** EVENTS **/

    void subscribeToNewCommand(Handler<String> handler);
}
