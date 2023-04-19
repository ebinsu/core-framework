package core.framework.query.support;

import core.framework.query.support.namequery.NameQueryParamImpl;

import java.util.Map;

/**
 * @author ebin
 */
public interface NameQueryParam<T> {
    String getQueryName();

    Class<T> getResultType();

    Map<String, Object> getQueryParam();

    NameQueryParamImpl<T> addQueryParam(String key, Object value);
}
