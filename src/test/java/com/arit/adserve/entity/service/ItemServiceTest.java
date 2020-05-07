package com.arit.adserve.entity.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import com.arit.adserve.App;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.repository.ItemRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("persistence-test.yml")
public class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Resource
    ItemRepository itemRepository;

    private static boolean setUpIsDone = false;

    @Before
    public void setUp () {
        if(setUpIsDone) return;
        Item item = new Item();
        item.setItemId("id");
        item.setTitle("title");
        itemRepository.save(item);
        Item item1 = new Item();
        item1.setItemId("id1");
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
    
    @Test
    public void testItemsUpdatedToday() {
		long itemCount = itemRepository.countItemsUpdatedAfter(new Date());
		log.debug("items: {}", itemCount);
		LocalDate localDate = LocalDate.now().minusDays(1);
		Date date = java.util.Date.from(localDate.atStartOfDay()
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
		itemCount = itemRepository.countItemsUpdatedAfter(date);
		log.debug("items: {}", itemCount);
		log.debug("items total: {}", itemRepository.count());

	}
}
