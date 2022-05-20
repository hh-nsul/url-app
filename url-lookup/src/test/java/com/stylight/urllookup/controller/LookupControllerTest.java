package com.stylight.urllookup.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stylight.urllookup.service.LookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LookupControllerTest {

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private LookupService lookupService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<String> parameterizedUrlList;

    private List<String> prettyUrlList;

    private String websiteUrl = "http://www.stylight.com";

    @BeforeEach
    void setUp() {

        parameterizedUrlList = new ArrayList<>(){
            {
                add(websiteUrl + "/products");
                add(websiteUrl + "/products?gender=female");
                add(websiteUrl + "/products?tag=5678");
                add(websiteUrl + "/products?gender=female&tag=123&tag=1234");
                add(websiteUrl + "/products?brand=123");
            }
        };

        prettyUrlList = new ArrayList<>(){
            {
                add(websiteUrl + "/Fashion/");
                add(websiteUrl + "/Women/");
                add(websiteUrl + "/Boat--Shoes/");
                add(websiteUrl + "/Women/Shoes/");
                add(websiteUrl + "/Adidas/");
            }
        };
    }

    @Test
    void getPrettyUrlByParamUrl() throws Exception {

        // given
        Map<String, String> urlMap = new HashMap<>();
        for (int i = 0; i < parameterizedUrlList.size(); ++i) {
            urlMap.put(parameterizedUrlList.get(i), prettyUrlList.get(i));
        }

        given(lookupService.getPrettyUrlByParamUrl(parameterizedUrlList)).willReturn(urlMap);

        final String requestUrl = "http://localhost:" + randomServerPort + "/url-lookup/v1/pretty-url/list";
        URI uri = new URI(requestUrl);

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<List<String>> request = new HttpEntity<>(parameterizedUrlList, httpHeaders);

        ResponseEntity<Map> response = testRestTemplate.postForEntity(uri, request, Map.class);

        // when
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // then
        for (int i = 0; i < parameterizedUrlList.size(); ++i) {
            assertThat(response.getBody().containsKey(parameterizedUrlList.get(i)));
            assertThat(response.getBody().containsValue(prettyUrlList.get(i)));
        }
    }

    @Test
    void getParamUrlByPrettyUrl() throws URISyntaxException {

        // given
        Map<String, String> urlMap = new HashMap<>();
        for (int i = 0; i < prettyUrlList.size(); ++i) {
            urlMap.put(prettyUrlList.get(i), parameterizedUrlList.get(i));
        }

        given(lookupService.getParamUrlByPrettyUrl(prettyUrlList)).willReturn(urlMap);

        final String requestUrl = "http://localhost:" + randomServerPort + "/url-lookup/v1/param-url/list";
        URI uri = new URI(requestUrl);

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<List<String>> request = new HttpEntity<>(prettyUrlList, httpHeaders);

        ResponseEntity<Map> response = testRestTemplate.postForEntity(uri, request, Map.class);

        // when
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // then
        for (int i = 0; i < prettyUrlList.size(); ++i) {
            assertThat(response.getBody().containsKey(prettyUrlList.get(i)));
            assertThat(response.getBody().containsValue(parameterizedUrlList.get(i)));
        }
    }
}