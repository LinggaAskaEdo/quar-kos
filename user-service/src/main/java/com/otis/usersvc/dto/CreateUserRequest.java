package com.otis.usersvc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {
	@NotNull(message = "Username may not be blank")
	@NotBlank(message = "Username may not be null")
	private String username;

	@NotNull(message = "Email may not be blank")
	@NotBlank(message = "Email may not be null")
	@Email
	private String email;

	public CreateUserRequest(String username, String email) {
		this.username = username;
		this.email = email;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
