package com.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.demo.beans.ContentBean;
import com.demo.service.MainService;

import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

@Controller
public class MainController {

	@Autowired
	private MainService MainService;
	
	@GetMapping("/main")
	public String main(Model model) {
		//4개의 각각의 게시판의 글들(게시판별 list) 담은 ArrayList list
		ArrayList<List<ContentBean>> list = new ArrayList<List<ContentBean>>();

		list.add(MainService.getMainList(1)); //자유게시판 리스트
		list.add(MainService.getMainList(2)); //유머게시판 리스트
		list.add(MainService.getMainList(3)); //정치게시판 리스트
		list.add(MainService.getMainList(4)); //스포츠게시판 리스트
		
		model.addAttribute("list", list);
		
		return "main";
	}
}
