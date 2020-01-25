package com.arit.adserve.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.service.ItemService;

@Controller
public class ItemController {
	
	private final Logger logger = LoggerFactory.getLogger(ItemController.class);
	
	@Autowired
	private ItemService itemService;	
	
	private final int ROW_PER_PAGE = 2;
	
	
	@GetMapping(value = {"/", "/index"})
	public String index(Model model) {
	    model.addAttribute("title", "item management");
	    return "index";
	}
	
	@GetMapping(value = "/items")
	public String getItems(Model model,
	        @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
	    List<Item> items = itemService.findAll(pageNumber, ROW_PER_PAGE);
	 
	    long count = itemService.count();
	    boolean hasPrev = pageNumber > 1;
	    boolean hasNext = (pageNumber * ROW_PER_PAGE) < count;
	    model.addAttribute("items", items);
	    model.addAttribute("hasPrev", hasPrev);
	    model.addAttribute("prev", pageNumber - 1);
	    model.addAttribute("hasNext", hasNext);
	    model.addAttribute("next", pageNumber + 1);
	    return "item-list";
	}
}
