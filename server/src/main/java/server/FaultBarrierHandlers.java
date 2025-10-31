package server;

import io.javalin.http.Context;

public class FaultBarrierHandlers {
    public void failureHandler(Context ctx) {
        ctx.status(500);
    }

}
