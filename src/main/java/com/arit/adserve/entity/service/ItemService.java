package com.arit.adserve.entity.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.repository.ItemRepository;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Item findById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    public List<Item> findAll(int pageNumber, int rowPerPage) {
        List<Item> items = new ArrayList<>();
        PageRequest sortedByLastUpdateDesc = PageRequest.of(pageNumber - 1, rowPerPage,
                Sort.by("title").descending());
        itemRepository.findAll(sortedByLastUpdateDesc).forEach(items::add);
        return items;
    }

    public Iterable<Item> findAllById(List<String> itemIds) {
        return itemRepository.findAllById(itemIds);
    }

    public Item save(Item item) {
        if (StringUtils.isEmpty(item.getTitle())) {
            throw new PersistenceException("Title is required");
        }
        if (item.getItemId() != null && existsById(item.getItemId())) {
            throw new PersistenceException("Item with id: " + item.getItemId() + " already exists");
        }
        return itemRepository.save(item);
    }

    public void update(Item item) {
        if (StringUtils.isEmpty(item.getTitle())) {
            throw new PersistenceException("Title is required");
        }
        if (StringUtils.isEmpty(item.getViewItemURL())) {
            throw new PersistenceException("Item URL is required");
        }
        if (!existsById(item.getItemId())) {
            throw new PersistenceException("Cannot find Item with id: " + item.getItemId());
        }
        itemRepository.save(item);
    }

    public void deleteById(String id) {
        if (!existsById(id)) {
            throw new PersistenceException("Cannot find Item with id: " + id);
        } else {
            itemRepository.deleteById(id);
        }
    }

    public Long count() {
        return itemRepository.count();
    }
    
    public Long countItemsUodatedAfter(Date date) {
        return itemRepository.countItemsUpdatedAfter(date);
    }

    public boolean existsById(String id) {
        return itemRepository.existsById(id);
    }

}
