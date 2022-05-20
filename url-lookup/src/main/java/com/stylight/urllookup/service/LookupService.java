package com.stylight.urllookup.service;

import java.util.List;
import java.util.Map;

public interface LookupService {

    Map<String, String> getPrettyUrlByParamUrl(List<String> parameterizedUrlList);

    Map<String, String> getParamUrlByPrettyUrl(List<String> prettyUrlList);
}
