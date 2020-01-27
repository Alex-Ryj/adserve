package com.arit.adserve.controller;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.repository.ItemRepository;
import com.arit.adserve.entity.service.ItemService;

@Controller
public class ItemController {

	private final Logger logger = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemService itemService;

	private final int ROW_PER_PAGE = 5;

	@GetMapping(value = { "/", "/index" })
	public String index(Model model) {
		model.addAttribute("title", "item management");
		return "index";
	}

	@GetMapping(value = "/items")
	public String getItems(Model model, @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
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

	@GetMapping(value = "/items/{itemId}")
	public String getItemById(Model model, @PathVariable String itemId) {
		Item item = null;
		try {
			item = itemService.findById(itemId);
			model.addAttribute("allowDelete", false);
		} catch (Exception ex) {
			model.addAttribute("errorMessage", ex.getMessage());
		}
		model.addAttribute("item", item);
		return "item";
	}

	@GetMapping(value = { "/items/add" })
	public String showAddItem(Model model) {
		Item item = new Item();
		model.addAttribute("add", true);
		model.addAttribute("item", item);
		return "item-edit";
	}

	@PostMapping(value = "/items/add")
	public String addItem(Model model, @ModelAttribute("item") Item item) {
		try {
			if (StringUtils.isBlank(item.getItemId())) {
				item.setItemId(UUID.randomUUID().toString());
			}
			itemService.save(item);
			return "redirect:/items/" + item.getItemId();
		} catch (Exception ex) {
			// log exception first,
			// then show error
			String errorMessage = ex.getMessage();
			logger.error(errorMessage);
			model.addAttribute("errorMessage", errorMessage);
			model.addAttribute("add", true);
			return "item-edit";
		}
	}

	@GetMapping(value = { "/items/{itemId}/edit" })
	public String showEditItem(Model model, @PathVariable String itemId) {
		Item item = null;
		try {
			item = itemService.findById(itemId);
		} catch (Exception ex) {
			model.addAttribute("errorMessage", ex.getMessage());
		}
		model.addAttribute("add", false);
		model.addAttribute("item", item);
		return "item-edit";
	}

	@PostMapping(value = { "/items/{itemId}/edit" })
	public String updateItem(Model model, @PathVariable String itemId, @ModelAttribute("item") Item item,
			@RequestParam("uploadingFile") MultipartFile uploadingFile) {
		try {
			Item existingItem = itemService.findById(itemId);
			if (existingItem != null) {
				existingItem.setDescription(item.getDescription());
				existingItem.setTitle(item.getTitle());	
				item = existingItem;
			}	 
			if (uploadingFile != null && !uploadingFile.isEmpty()) {
				item.setImage64BaseStr(Base64.getEncoder().encodeToString(uploadingFile.getBytes()));
				
				
			}
			itemService.update(item);
			return "redirect:/items/" + item.getItemId();
		} catch (Exception ex) {
			// log exception first,
			// then show error
			String errorMessage = ex.getMessage();
			logger.error(errorMessage);
			model.addAttribute("errorMessage", errorMessage);
			model.addAttribute("add", false);
			return "item-edit";
		}
	}
}
