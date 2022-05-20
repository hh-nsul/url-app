package com.stylight.urllookup.service.impl;

import com.google.common.collect.HashBiMap;
import com.stylight.urllookup.constant.UrlType;
import com.stylight.urllookup.repository.LookupRepository;
import com.stylight.urllookup.service.LookupService;
import com.stylight.urllookup.util.UrlUtil;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LookupServiceImpl implements LookupService {

    private LookupRepository lookupRepository;

    protected HashBiMap<String, String> dictionaryMap;

    @Getter
    private final String WEBSITE_PREFIX = "http://www.stylight.com";

    public LookupServiceImpl(LookupRepository lookupRepository) throws IOException {
        this.lookupRepository = lookupRepository;
        dictionaryMap = HashBiMap.create(lookupRepository.getDictionaryMap());
    }

    @Override
    public Map<String, String> getPrettyUrlByParamUrl(List<String> parameterizedUrlList) {

        Map<String, String> map = parameterizedUrlList
                                .stream()
                                .collect(Collectors.toMap(
                                        Function.identity(),
                                        url -> WEBSITE_PREFIX
                                            + getBestMatch(url.substring(WEBSITE_PREFIX.length()), UrlType.PARAM_URL)));
        return map;
    }

    @Override
    public Map<String, String> getParamUrlByPrettyUrl(List<String> prettyUrlList) {
        Map<String, String> map = prettyUrlList
                                .stream()
                                .collect(Collectors.toMap(
                                        Function.identity(),
                                        prettyUrl -> WEBSITE_PREFIX + getMappedUrlFromDictionary(
                                                                        prettyUrl.substring(WEBSITE_PREFIX.length()),
                                                                        prettyUrl.substring(WEBSITE_PREFIX.length()),
                                                                        UrlType.PRETTY_URL)));
        return map;
    }

    public String getBestMatch(String srcUrl, UrlType srcUrlType) {

        String matchedUrl = getMappedUrlFromDictionary(srcUrl, srcUrl, srcUrlType);

        // If the exact match for the current source url can be found
        if (!matchedUrl.equals(srcUrl)) {
            return matchedUrl;
        }

        String biggestPartCoveredUrl = "";
        // Else, try to find the one that matches best for the source url
        try {
            URL url = new URL(WEBSITE_PREFIX + srcUrl);
            List<String> paramList = UrlUtil.getParamListFromUrl(url);
            if (CollectionUtils.isEmpty(paramList)) {
                return srcUrl;
            }
            biggestPartCoveredUrl = paramList.stream()
                        .map(param -> UrlUtil.removeLastParam(srcUrl, param))
                        .filter(shorterUrl -> {
                            String mappedUrl = getMappedUrlFromDictionary(shorterUrl, srcUrl, srcUrlType);
                            return !mappedUrl.equals(srcUrl);
                        })
                        .findFirst()
                        .orElseGet(() -> srcUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return getMappedUrlFromDictionary(biggestPartCoveredUrl, srcUrl, srcUrlType);
    }

    private String getMappedUrlFromDictionary(String partUrl, String srcUrl, UrlType srcUrlType) {

        if (srcUrlType == UrlType.PARAM_URL) {
            return dictionaryMap.getOrDefault(partUrl, srcUrl);
        } else {
            return dictionaryMap.inverse().getOrDefault(partUrl, srcUrl);
        }
    }
}