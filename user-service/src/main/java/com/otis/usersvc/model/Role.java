package com.otis.usersvc.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Role {
	private UUID id;
	private String name;
	private String description;
	private LocalDateTime assignedAt;

	public Role() {
	}

	public Role(UUID id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}
}
