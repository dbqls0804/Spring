package com.demo.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.beans.ContentBean;
import com.demo.beans.LoginUserBean;
import com.demo.beans.PageBean;
import com.demo.service.BoardService;

@Controller
@RequestMapping("/board")
public class BoardController {
	
	@Autowired
	private BoardService boardService;

	//현재 로그인 유저 객체
	@Resource(name = "loginUserBean")
	private LoginUserBean loginUserBean;
	
	@GetMapping("/main")
	public String main(@RequestParam("board_info_idx") int board_info_idx, Model model,
					   @RequestParam(value = "page", defaultValue = "1") int page) {
		model.addAttribute("board_info_idx", board_info_idx);
		model.addAttribute("page", page);

		String boardInfoName = boardService.getBoardInfoName(board_info_idx);
		model.addAttribute("boardInfoName", boardInfoName);
		
		List<ContentBean> contentList = boardService.getContentList(board_info_idx, page);
		model.addAttribute("contentList", contentList);
		//페이지네이션 표시하기
		PageBean pageBean = boardService.getContentCnt(board_info_idx, page);
		model.addAttribute("pageBean", pageBean);
		
		return "board/main";
	}

	@GetMapping("/read")
	public String read(@RequestParam("board_info_idx") int board_info_idx,
					   @RequestParam("content_idx") int content_idx,
					   @RequestParam("page") int page,
					   Model model) {
		model.addAttribute("page", page);
		model.addAttribute("board_info_idx", board_info_idx);
		model.addAttribute("content_idx", content_idx);
        model.addAttribute("loginUserBean", loginUserBean);

		//글 번호로 dB에서 게시글 내용 가져오기
		ContentBean readContentBean = boardService.getContentInfo(content_idx);
		model.addAttribute("readContentBean", readContentBean);
		
		return "board/read";
	}
	
	@GetMapping("/write")
	public String write(@RequestParam("board_info_idx") int board_info_idx,
						@ModelAttribute("writeContentBean") ContentBean writeContentBean) {
		//보드인덱스번호를 게시글객체에 입력한다. 
		writeContentBean.setContent_board_idx(board_info_idx);
		return "board/write";
	}
	
	@PostMapping("/write_pro")
	public String write_pro(@Valid @ModelAttribute("writeContentBean") ContentBean writeContentBean,
							BindingResult result) {		
		if(result.hasErrors()) {
			return "board/write";
		}
		// 유효성 검사 완료후 DB에 새 게시글 저장
		boardService.addContentInfo(writeContentBean);
		
		return "board/write_success";
	}
	
	@GetMapping("/modify")
	public String modify(@RequestParam("board_info_idx") int board_info_idx,
			   			 @RequestParam("content_idx") int content_idx,
			   			 @ModelAttribute("modifyContentBean") ContentBean modifyContentBean,
						 @RequestParam("page") int page,
						 Model model) {
		//전달된 게시판번호와 게시글번호를 빈 객체 modify빈에 입력
		modifyContentBean.setContent_board_idx(board_info_idx);
		modifyContentBean.setContent_idx(content_idx);

		boardService.getContents(modifyContentBean);		
		model.addAttribute("modifyContentBean", modifyContentBean);
		model.addAttribute("page", page);
	
		return "board/modify";
	}
	
	@PostMapping("/modify_pro")
	public String modify_pro(@Valid @ModelAttribute("modifyContentBean") ContentBean modifyContentBean,
			@RequestParam("page") int page,
			BindingResult result,
			Model model) {
		model.addAttribute("page", page);
		
		if(result.hasErrors()) {
			return "board/modify";
	}
		//DB에 수정된 게시글을 업데이트 한다
		boardService.modifyContentInfo(modifyContentBean);
		return "board/modify_success";
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam("board_info_idx") int board_info_idx,
						 @RequestParam("content_idx") int content_idx, Model model) {
		boardService.deleteContentInfo(content_idx); //게시글 삭제
		model.addAttribute("board_info_idx", board_info_idx);
		
		return "board/delete";
	}
	
	//수정, 삭제 권한이 없는데(주소 직접 입력 등) 요청할 경우
	@GetMapping("/not_writer")
	public String not_writer() {
		return "board/not_writer";
	}
}