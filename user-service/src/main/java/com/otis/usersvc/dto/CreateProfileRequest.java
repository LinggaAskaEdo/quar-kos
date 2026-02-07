package com.otis.usersvc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProfileRequest {
	@NotNull(message = "Firstname may not be blank")
	@NotBlank(message = "Firstname may not be null")
	private String firstName;

	@NotNull(message = "Lastname may not be blank")
	@NotBlank(message = "Lastname may not be null")
	private String lastName;

	@NotNull(message = "Phone may not be blank")
	@NotBlank(message = "Phone may not be null")
	private String phone;

	@NotNull(message = "Address may not be blank")
	@NotBlank(message = "Address may not be null")
	private String address;

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
