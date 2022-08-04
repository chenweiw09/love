package com.book.xw.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyBolgController {

    @GetMapping("/index")
    public String getIndex(){
        return "index";
    }

    @GetMapping("/about-tech")
    public String aboutTech(){
        return "tech";
    }

    @GetMapping("/wx")
    public String wx(){
        return "wx";
    }

    @GetMapping("/mybooks")
    public String mybooks(){
        return "mybooks";
    }

    @GetMapping("/love")
    public String love(){
        return "love";
    }

}
