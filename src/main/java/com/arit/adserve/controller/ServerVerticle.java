package com.arit.adserve.controller;

import com.arit.adserve.ebay.EbayApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Base64;

import java.util.Date;

@Service
public class ServerVerticle extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger(ServerVerticle.class);

    @Override
    public void start() throws Exception {
        super.start();
        Router router = Router.router(vertx);

        vertx.createHttpServer()
                .requestHandler(request -> {
                    // This handler gets called for each request that arrives on the server
                    vertx.eventBus().request(EbayApi.EBAY_GET_IMAGE_CAMEL, "test", reply -> {
                        if(reply.succeeded()) {
                            log.info("received: {}", reply.result().body());
                            HttpServerResponse response = request.response();
                            response.putHeader("Content-Type", "image/png");
                            String base64Img = ((JsonObject) reply.result().body()).getString("img");
                            response.putHeader("Content-Length" , base64Img.length() + "");
                            byte[] resBuffer = Base64.getDecoder().decode(base64Img);
                            Buffer buffer = Buffer.buffer(resBuffer);
                            response.write(buffer).end();
                        }else
                            log.info("error", reply.cause());
                    });
                }).listen(8080);
    }

    private Future<Void> startHttpServer() {
        Promise<Void> promise = Promise.promise();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.get("/").handler(this::indexHandler);
        router.get("/wiki/:page").handler(this::pageRenderingHandler);
        router.post().handler(BodyHandler.create());
        router.post("/save").handler(this::pageUpdateHandler);
        router.post("/create").handler(this::pageCreateHandler);
        router.post("/delete").handler(this::pageDeletionHandler);

        server
                .requestHandler(router)
                .listen(8080, ar -> {
                    if (ar.succeeded()) {
                        log.info("HTTP server running on port 8080");
                        promise.complete();
                    } else {
                        log.error("Could not start a HTTP server", ar.cause());
                        promise.fail(ar.cause());
                    }
                });

        return promise.future();
    }

    private void indexHandler(RoutingContext context) {

        DeliveryOptions options = new DeliveryOptions().addHeader("action", "all-pages"); // <2>

        vertx.eventBus().request(EbayApi.EBAY_REQUEST_VTX, new JsonObject(), options, reply -> {  // <1>
            if (reply.succeeded()) {
                JsonObject body = (JsonObject) reply.result().body();   // <3>
                context.put("title", "Wiki home");
                context.put("pages", body.getJsonArray("pages").getList());
                context.response().putHeader("content-type", "text/plain");
                context.response().end(reply.result().body().toString());
            } else {
                context.fail(reply.cause());
            }
        });
    }
    // end::indexHandler[]

    // tag::rest[]
    private static final String EMPTY_PAGE_MARKDOWN =
            "# A new page\n" +
                    "\n" +
                    "Feel-free to write in Markdown!\n";

    private void pageRenderingHandler(RoutingContext context) {


    }

    private void pageUpdateHandler(RoutingContext context) {

    }

    private void pageCreateHandler(RoutingContext context) {

    }

    private void pageDeletionHandler(RoutingContext context) {

    }


}
