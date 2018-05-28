package com.xaut.server.controller;

import com.xaut.server.response.DataResponse;
import com.xaut.server.service.PageDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class DataController {
    @Autowired
    private PageDataService pageDataService;

    @GetMapping("data")
    public DataResponse dataResponse() {
        return pageDataService.dataResponse();
    }
}
