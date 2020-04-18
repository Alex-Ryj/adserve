package com.arit.adserve.entity.service;

import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.repository.ItemRepository;
import com.arit.adserve.verticle.ItemVerticle;
import io.vertx.ext.web.common.template.test;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    private static boolean setUpIsDone = false;

    @Before
    public void setUp () {
        if(setUpIsDone) return;
        Item item = new Item();
        item.setItemId("Id");
        item.setTitle("title");
        itemRepository.save(item);
        Item item1 = new Item();
        item1.setItemId("Id1");
        item1.setTitle("title1");
        itemRepository.save(item1);
        setUpIsDone = true;
    }

    @Test
    public void getItemTest() {
      Item item =  itemService.findById("id");
      Assert.assertNotNull(item);
      Assert.assertEquals("title", item.getTitle());
    }

    @Test
    public void getItemsTest() {
        List<String> itemIds = new ArrayList<>();
        itemIds.add("id");
        itemIds.add("id1");
        Iterable<Item> items =  itemService.findAllById(itemIds);
        Assert.assertNotNull(items);
        Assert.assertTrue(items.iterator().hasNext());
        items.iterator().next();
        Assert.assertTrue(items.iterator().hasNext());
    }
}
