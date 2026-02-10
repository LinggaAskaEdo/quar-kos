package com.otis.usersvc.dto;

import java.util.List;
import java.util.Map;

import com.otis.usersvc.model.User;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SearchResponse {
	private List<User> data;
	private Map<String, String> filters;
	private Integer limit;
	private Integer offset;
	private Integer count;

	public SearchResponse(List<User> data, Map<String, String> filters, Integer limit, Integer offset) {
		this.data = data;
		this.filters = filters;
		this.limit = limit;
		this.offset = offset;
		this.count = data.size();
	}

	public List<User> getData() {
		return this.data;
	}

	public void setData(List<User> data) {
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
