package de.nava.mlsample.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

@Controller
public class SampleWebController {

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("now", new Date());
        return "index";
    }

}
