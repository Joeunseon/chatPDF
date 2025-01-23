package com.project.chat_pdf.web.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class CustomErrorController {

    @GetMapping("/404")
    public String handleNotFound() {
        return "/error/404";
    }

    @GetMapping("/500")
    public String handleInternalServerError() {
        return "/error/500";
    }

    @GetMapping("/default")
    public String handleDefault() {
        return "/error/default";
    }
}
