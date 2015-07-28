package com.worescloud.workdesk.storage.mongodb.filter;

import com.worescloud.workdesk.storage.Filter;

public interface Converter {
    <T> T convert(Filter filter);
}
