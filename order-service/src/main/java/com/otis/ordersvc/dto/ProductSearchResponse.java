package com.otis.ordersvc.dto;

import java.util.List;
import java.util.Map;

public class ProductSearchResponse {
	private List<ProductDTO> dataProducts;
	private Map<String, String> filters;
	private Integer limit;
	private Integer offset;
	private Integer count;

	public ProductSearchResponse(List<ProductDTO> dataProducts, Map<String, String> filters,
			Integer limit, Integer offset) {
		this.dataProducts = dataProducts;
		this.filters = filters;
		this.limit = limit;
		this.offset = offset;
		this.count = dataProducts != null ? dataProducts.size() : 0;
	}

	// Getters and setters
	public List<ProductDTO> getDataProducts() {
		return dataProducts;
	}

	public void setDataProducts(List<ProductDTO> dataProducts) {
		this.dataProducts = dataProducts;
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
