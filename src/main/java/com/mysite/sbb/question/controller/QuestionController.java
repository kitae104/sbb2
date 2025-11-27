package com.mysite.sbb.question.controller;

import com.mysite.sbb.member.entity.Member;
import com.mysite.sbb.member.service.MemberService;
import com.mysite.sbb.question.dto.QuestionDto;
import com.mysite.sbb.question.entity.Question;
import com.mysite.sbb.question.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/question")
public class QuestionController {

    private final QuestionService questionService;
    private final MemberService memberService;

    @GetMapping("/modify/{id}")
    public String modify(@PathVariable("id") Long id, Model model ){
        Question question = questionService.getQuestion(id);
        QuestionDto questionDto = new QuestionDto();
        questionDto.setSubject(question.getSubject());
        questionDto.setContent(question.getContent());
        model.addAttribute("questionDto", questionDto);
        return "question/inputForm";
    }

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Question> paging = questionService.getQuestionList(page);
        System.out.println("=== paging : " + paging );
        model.addAttribute("paging", paging);
        return "question/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model){
        Question question = questionService.getQuestion(id);
        model.addAttribute("question", question);
        log.info("question : {}", question);
        return "question/detail";
    }

    @GetMapping("/create")
    public String createForm(Model model){
        model.addAttribute("questionDto", new QuestionDto());
        return "question/inputForm";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid QuestionDto questionDto,
                         BindingResult bindingResult,
                         Principal principal,
                         Model model
                         ) {
        if(bindingResult.hasErrors()){
            model.addAttribute("questionDto", questionDto);
            return "question/inputForm";
        }

        Member member = memberService.getMember(principal.getName());

        questionService.create(questionDto, member);
        return "redirect:/question/list";
    }

}
