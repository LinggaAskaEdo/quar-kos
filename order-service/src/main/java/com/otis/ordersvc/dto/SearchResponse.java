package com.otis.ordersvc.dto;

import java.util.List;
import java.util.Map;

import com.otis.ordersvc.model.Order;

public class SearchResponse {
    private List<Order> data;
    private Map<String, String> filters;
    private Integer limit;
    private Integer offset;
    private Integer count;

    public SearchResponse(List<Order> data, Map<String, String> filters, Integer limit, Integer offset) {
        this.data = data;
        this.filters = filters;
        this.limit = limit;
        this.offset = offset;
        this.count = data.size();
    }

    public List<Order> getData() {
        return this.data;
    }

    public void setData(List<Order> data) {
        this.data = data;
    }

    public Map<String, String> getFilters() {
        return this.filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return this.offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
