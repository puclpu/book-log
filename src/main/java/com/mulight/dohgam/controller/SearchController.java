package com.mulight.dohgam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

	@GetMapping("/search")
	public String search(@RequestParam(name="keyword", required = false)String keyword, Model model) {
		model.addAttribute("keyword", keyword);
		
		return "search/search";
	}
	
	@GetMapping("/search/book")
	public String book(@RequestParam(name="isbn", required = false)String isbn, Model model) {
		model.addAttribute("isbn", isbn);
		
		return "search/book";
	}
	
}
