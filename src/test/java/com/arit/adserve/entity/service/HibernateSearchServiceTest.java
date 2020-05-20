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
public class HibernateSearchServiceTest {

    @Autowired
    HibernateSearchService searchService;

    @Autowired
    ItemRepository itemRepository;

    private static boolean setUpIsDone = false;

    @BeforeEach
    public void setUp () {
        if(setUpIsDone) return;
        Item item = new Item();
        item.setProviderItemId("id");
        item.setTitle("title of this item");
        item.setProviderName(Constants.EBAY);
        item.setDocId(item.getProviderName()+item.getProviderItemId());
        item.setViewItemURL("http://viewItemURL.com");
        itemRepository.save(item);
        Item item1 = new Item();
        item1.setProviderItemId("id1");
        item1.setProviderName(Constants.EBAY);
        item1.setTitle("stuff of another item");
        item1.setDocId(item1.getProviderName()+item1.getProviderItemId());
        item1.setViewItemURL("http://viewItemURL.com");
        itemRepository.save(item1);
        setUpIsDone = true;
    }

    @Test
    public void getSearchTest() {
      List<Item> items =  searchService.findItems("title");
      assertNotNull(items);
      assertEquals(1, items.size());
    }

}
