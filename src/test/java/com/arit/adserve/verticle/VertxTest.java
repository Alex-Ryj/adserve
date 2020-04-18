package com.arit.adserve.verticle;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class VertxTest {

    @Test /*(timeout=5000)*/
    public void async_behavior(TestContext context) {
        Vertx vertx = Vertx.vertx();
        context.assertEquals("foo", "foo");
        Async a1 = context.async();
        Async a2 = context.async(3);
        vertx.setTimer(100, n -> a1.complete());
        vertx.setPeriodic(100, n -> a2.countDown());
    }
}
