package com.arit.adserve.verticle;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.repository.ItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.jackson.JacksonCodec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemVerticleTest implements ApplicationContextAware {

    ApplicationContext applicationContext;

    @Autowired
    ItemRepository itemRepository;

    Vertx vertx = Vertx.vertx();

    private static boolean setUpIsDone = false;

    @Before
    public void setUp () {
        if(setUpIsDone) return;
        DeploymentOptions optionsWorker = new DeploymentOptions().setWorker(true);
        vertx.deployVerticle(applicationContext.getBean(ItemVerticle.class), optionsWorker);
        Item item = new Item();
        item.setProviderItemId("id");
        item.setTitle("title");
        itemRepository.save(item);
        Item item1 = new Item();
        item1.setProviderItemId("id1");
        item1.setTitle("title1");
        itemRepository.save(item1);
        setUpIsDone = true;
    }

    @Test
    public void getItemTest () {
        log.info("item test rep: " +  itemRepository.findById(new ItemId("id", "ebay")));
        vertx.eventBus().request(ItemVerticle.GET_ITEM_VTX, "id", reply -> {
            if(reply.succeeded()) {
                log.info("received: {}", reply.result().body());
                Item item = Json.decodeValue(reply.result().body().toString(), Item.class);
            }else
                log.info("error", reply.cause());
        });
    }

    @Test
    public void getItemsTest () {
        JsonArray jsonArray = new JsonArray().add("id").add("id1");
        vertx.eventBus().request(ItemVerticle.GET_ITEMS_VTX, jsonArray, reply -> {
            if(reply.succeeded()) {
                log.info("received: {}", reply.result().body());
                List<Item> items = JacksonCodec.decodeValue(reply.result().body().toString(), new TypeReference<List<Item>>() {});
            }else
                log.info("error", reply.cause());
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
