package com.arit.adserve.verticle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class VertxTest {

	@Test /* (timeout=5000) */
	public void async_behavior(Vertx vertx, VertxTestContext testContext) throws Throwable {
		vertx.createHttpServer().requestHandler(req -> req.response().end()).listen(16969, testContext.completing());

		assertTrue((testContext.awaitCompletion(5, TimeUnit.SECONDS)) == true);
		if (testContext.failed()) {
			throw testContext.causeOfFailure();
		}
	}
}
