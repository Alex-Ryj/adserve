package com.arit.adserve.entity.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import com.arit.adserve.App;
import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.repository.ItemRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@PropertySource("persistence-test.yml")
public class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Resource
    static ItemRepository itemRepository;

    private static boolean setUpIsDone = false;

    @BeforeAll
    public static void setUp () {
        if(setUpIsDone) return;
        Item item = new Item();
        item.setProviderItemId("id");
        item.setTitle("title");
        item.setProviderName(Constants.EBAY);
        item.setViewItemURL("http://viewItemURL.com");
        itemRepository.save(item);
        Item item1 = new Item();
        item1.setProviderItemId("id1");
        item1.setProviderName(Constants.EBAY);
        item1.setTitle("title1");
        item1.setViewItemURL("http://viewItemURL.com");
        itemRepository.save(item1);
        setUpIsDone = true;
    }

    @Test
    public void getItemTest() {
      Item item =  itemService.findById(new ItemId("id", "ebay"));
      assertNotNull(item);
      assertEquals("title", item.getTitle());
    }

    @Test
    public void getItemsTest() {
        List<ItemId> itemIds = new ArrayList<>();
        itemIds.add(new ItemId("id", "ebay"));
        itemIds.add(new ItemId("id1", "ebay"));
        Iterable<Item> items =  itemService.findAllById(itemIds);
        assertNotNull(items);
        assertTrue(items.iterator().hasNext());
        items.iterator().next();
        assertTrue(items.iterator().hasNext());
    }
    
    @Test
    public void testCountItemsUpdatedToday() {
		long itemCount = itemRepository.countItemsUpdatedAfter(new Date(), Constants.EBAY);
		log.debug("items: {}", itemCount);
		assertEquals(0, itemCount);
		LocalDate localDate = LocalDate.now().minusDays(1);
		Date date = java.util.Date.from(localDate.atStartOfDay()
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
		itemCount = itemRepository.countItemsUpdatedAfter(date, Constants.EBAY);
		assertEquals(2, itemCount);
		assertEquals(2, itemRepository.count());

	}
    
    @Test
    public void testGetItemsToUpdatToday() {
		LocalDate localDate = LocalDate.now().plusDays(1);
		Date date = java.util.Date.from(localDate.atStartOfDay()
			      .atZone(ZoneId.systemDefault())
			      .toInstant());
		var items = itemRepository.getItemsFromProviderUpdatedBefore(date, Constants.EBAY, PageRequest.of(0, 2));
		log.debug("items: {}", items.size());
		assertEquals(2, items.size());
	}
}
