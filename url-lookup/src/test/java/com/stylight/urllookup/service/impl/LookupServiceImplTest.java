package com.stylight.urllookup.service.impl;

import com.google.common.collect.HashBiMap;
import com.stylight.urllookup.constant.UrlType;
import com.stylight.urllookup.repository.LookupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LookupServiceImplTest {

    @Mock
    private LookupRepository lookupRepository;

    private HashBiMap<String, String> dictionaryMap;

    @InjectMocks
    private LookupServiceImpl lookupService;

    private String websiteUrl = "http://www.stylight.com";

    @BeforeEach
    void setUp() {
        Map<String, String> map = new HashMap<>(){
            {
                put("/products", "/Fashion/");
                put("/products?gender=female", "/Women/");
                put("/products?tag=5678", "/Boat--Shoes/");
                put("/products?gender=female&tag=123&tag=1234", "/Women/Shoes/");
                put("/products?brand=123", "/Adidas/");
            }
        };

        dictionaryMap = HashBiMap.create(map);
        lookupService.dictionaryMap = dictionaryMap;
    }

    @ParameterizedTest
    @CsvSource(value = {"/products, /Fashion/",
                        "/products?gender=female, /Women/",
                        "/products?brand=123, /Adidas/"})
    void getBestMatchExact(String parameterizedUrl, String prettyUrl) {
        String matchedUrl = lookupService.getBestMatch(parameterizedUrl, UrlType.PARAM_URL);
        assertThat(matchedUrl).isEqualTo(prettyUrl);
    }

    @ParameterizedTest
    @CsvSource(value = {"/products?gender=female&tag=111&tag=222, /Women/",
                        "/products?brand=123&tag=111&tag=222, /Adidas/",
                        "/products?brand=321&tag=111&tag=222, /Fashion/"})
    void getBestMatchMostCovered(String parameterizedUrl, String prettyUrl) {
        String matchedUrl = lookupService.getBestMatch(parameterizedUrl, UrlType.PARAM_URL);
        assertThat(matchedUrl).isEqualTo(prettyUrl);
    }

    @ParameterizedTest
    @CsvSource(value = {"http://www.stylight.com/products, http://www.stylight.com/Fashion/",
            "http://www.stylight.com/products?gender=female, http://www.stylight.com/Women/",
            "http://www.stylight.com/products?brand=123, http://www.stylight.com/Adidas/"})
    void getPrettyUrlByParamUrl(String parameterizedUrl, String prettyUrl) {

        // given
        List<String> parameterizedUrlList = new ArrayList<>() {
            {
                add(parameterizedUrl);
            }
        };

        // when
        Map<String, String> map = lookupService.getPrettyUrlByParamUrl(parameterizedUrlList);

        // then
        assertThat(map.get(parameterizedUrl)).isEqualTo(prettyUrl);
    }

    @ParameterizedTest
    @CsvSource(value = {"http://www.stylight.com/Fashion/, http://www.stylight.com/products",
                        "http://www.stylight.com/Women/, http://www.stylight.com/products?gender=female",
                        "http://www.stylight.com/Boat--Shoes/, http://www.stylight.com/products?tag=5678"})
    void getParamUrlByPrettyUrl(String prettyUrl, String parameterizedUrl) {

        // given
        List<String> prettyUrlList = new ArrayList<>() {
            {
                add(prettyUrl);
            }
        };

        // when
        Map<String, String> map = lookupService.getParamUrlByPrettyUrl(prettyUrlList);

        // then
        assertThat(map.get(prettyUrl)).isEqualTo(parameterizedUrl);
    }
}