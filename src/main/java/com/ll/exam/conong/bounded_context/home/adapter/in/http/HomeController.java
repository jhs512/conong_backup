package com.ll.exam.conong.bounded_context.home.adapter.in.http;

import com.ll.exam.conong.base.AppConfig;
import com.ll.exam.conong.standard.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final Rq rq;

    @GetMapping("/")
    public String showHome() {
        if (rq.isLogined()) {
            return Rq.redirectWithMsg("/voice/upload", "");
        }

        return Rq.redirectWithMsg("/account/login", "");
    }

    @GetMapping("/version")
    @ResponseBody
    public int version() {
        return AppConfig.getVersion();
    }
}
