package com.otis.ordersvc.dto;

import java.util.List;
import java.util.Map;

public class OrderSearchResponse {
	private List<OrderDTO> dataOrders;
	private Map<String, String> filters;
	private Integer limit;
	private Integer offset;
	private Integer count;

	public OrderSearchResponse(List<OrderDTO> dataOrders, Map<String, String> filters,
			Integer limit, Integer offset) {
		this.dataOrders = dataOrders;
		this.filters = filters;
		this.limit = limit;
		this.offset = offset;
		this.count = dataOrders != null ? dataOrders.size() : 0;
	}

	public List<OrderDTO> getDataOrders() {
		return dataOrders;
	}

	public void setDataOrders(List<OrderDTO> dataOrders) {
		this.dataOrders = dataOrders;
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
