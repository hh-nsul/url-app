package com.stylight.urllookup.util;

import org.springframework.util.StringUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UrlUtil {

    public static List<String> getParamListFromUrl(URL url) {

        List<String> queryParamList = new ArrayList<>();

        /**
         * url:   https://www.stylight.com/products?gender=female&tag=123&tag=1234
         * query: gender=female&tag=123&tag=1234
         */
        String query = url.getQuery();
        if (StringUtils.hasText(query)) {
            String[] paramArray = query.split("&");

            for (int i = paramArray.length - 1; i >= 0; --i) {
                String param = URLDecoder.decode(paramArray[i], StandardCharsets.UTF_8);
                queryParamList.add(param);
            }
        }

        // ["tag=1234", "tag=123", "gender=female"]
        return queryParamList;
    }

    static public String removeLastParam(String url, String param) {

        /**
         * url:    https://www.stylight.com/products?gender=female&tag=123&tag=1234
         * param:  tag=1234
         * return: https://www.stylight.com/products?gender=female&tag=123
         */
        int lastIndex = url.lastIndexOf(param);
        return url.substring(0, lastIndex - 1);
    }
}
