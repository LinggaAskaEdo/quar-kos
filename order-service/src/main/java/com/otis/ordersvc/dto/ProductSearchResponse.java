package com.otis.ordersvc.dto;

import java.util.List;
import java.util.Map;

public class ProductSearchResponse {
	private List<ProductDTO> data;
	private Map<String, String> filters;
	private Integer limit;
	private Integer offset;
	private Integer count;

	public ProductSearchResponse(List<ProductDTO> data, Map<String, String> filters,
			Integer limit, Integer offset) {
		this.data = data;
		this.filters = filters;
		this.limit = limit;
		this.offset = offset;
		this.count = data != null ? data.size() : 0;
	}

	// Getters and setters
	public List<ProductDTO> getData() {
		return data;
	}

	public void setData(List<ProductDTO> data) {
		this.data = data;
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
