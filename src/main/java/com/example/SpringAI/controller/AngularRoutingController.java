package com.example.SpringAI.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AngularRoutingController {
    @GetMapping("/{path:^(?!.*\\..*$).*$}")
    public String redirect() {
        return "forward:/index.html";
    }
}

