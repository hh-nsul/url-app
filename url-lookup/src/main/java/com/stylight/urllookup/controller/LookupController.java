package com.stylight.urllookup.controller;

import com.stylight.urllookup.service.LookupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/url-lookup/v1")
public class LookupController {

    private LookupService lookupService;

    public LookupController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    @PostMapping("/param-url/list")
    public ResponseEntity<Map<String, String>> getPrettyUrlByParamUrl(@RequestBody List<String> parameterizedUrlList) {
        Map<String, String> urlMap = lookupService.getPrettyUrlByParamUrl(parameterizedUrlList);
        return ResponseEntity.ok().body(urlMap);
    }

    @PostMapping("/pretty-url/list")
    public ResponseEntity<Map<String, String>> getParamUrlByPrettyUrl(@RequestBody List<String> prettyUrlList) {
        Map<String, String> urlMap = lookupService.getParamUrlByPrettyUrl(prettyUrlList);
        return ResponseEntity.ok().body(urlMap);
    }
}
