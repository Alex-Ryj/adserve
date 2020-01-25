package com.arit.adserve.entity.service;

import java.util.ArrayList;
import java.util.List;

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
	 
	 public Item save(Item item) throws Exception {
	        if (StringUtils.isEmpty(item.getTitle())) {
	            throw new Exception("Title is required");
	        }	
	        if (item.getItemId() != null && existsById(item.getItemId())) { 
	            throw new Exception("Item with id: " + item.getItemId() + " already exists");
	        }
	        return itemRepository.save(item);
	    }
	    
	    public void update(Item item) throws Exception {
	        if (StringUtils.isEmpty(item.getTitle())) {
	            throw new Exception("Title is required");
	        }
	        if (StringUtils.isEmpty(item.getViewItemURL())) {
	            throw new Exception("Itrem URL is required");
	        }
	        if (!existsById(item.getItemId())) {
	            throw new Exception("Cannot find Item with id: " + item.getItemId());
	        }
	        itemRepository.save(item);
	    }
	    
	    public void deleteById(String id) throws Exception {
	        if (!existsById(id)) { 
	            throw new Exception("Cannot find Item with id: " + id);
	        }
	        else {
	            itemRepository.deleteById(id);
	        }
	    }
	    
	    public Long count() {
	        return itemRepository.count();
	    }
	    
	    private boolean existsById(String id) {
	        return itemRepository.existsById(id);
	    }

}
