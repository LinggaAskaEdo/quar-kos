package com.otis.usersvc.resource.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.otis.usersvc.dto.CreateProfileRequest;
import com.otis.usersvc.dto.CreateUserRequest;
import com.otis.usersvc.dto.RoleDTO;
import com.otis.usersvc.dto.SearchResponse;
import com.otis.usersvc.dto.UserDTO;
import com.otis.usersvc.dto.UserProfileDTO;
import com.otis.usersvc.dto.UserWithProfileDTO;
import com.otis.usersvc.service.UserService;

import io.quarkus.test.Mock;
import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {
	@Mock
	UserService userService;

	@InjectMocks
	UserResource userResource;

	@Captor
	ArgumentCaptor<Map<String, String>> filtersCaptor;

	private UUID userId;
	private UUID userProfileId;
	private UUID roleId;
	private UserDTO sampleUser;
	private UserProfileDTO sampleProfile;
	private UserWithProfileDTO sampleUserWithProfile;
	private RoleDTO sampleRole;
	private CreateUserRequest createUserRequest;
	private CreateProfileRequest createProfileRequest;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		userProfileId = UUID.randomUUID();
		roleId = UUID.randomUUID();

		sampleUser = new UserDTO(
				userId,
				"testuser",
				"test@example.com", null);

		sampleProfile = new UserProfileDTO(userProfileId,
				userId,
				"John",
				"Doe",
				"+123456789",
				"123 Main St");

		sampleUserWithProfile = new UserWithProfileDTO(
				userId,
				"testuser",
				"test@example.com",
				null,
				sampleProfile);

		sampleRole = new RoleDTO(
				roleId,
				"ADMIN",
				"Administrator role",
				null);

		createUserRequest = new CreateUserRequest(
				"testuser",
				"test@example.com");

		createProfileRequest = new CreateProfileRequest(
				"John",
				"Doe",
				"+123456789",
				"123 Main St");
	}

	// --- getAllUsers ---
	@Test
	void getAllUsers_shouldReturnListOfUsers() {
		List<UserDTO> users = List.of(sampleUser);
		when(userService.getAllUsers()).thenReturn(users);

		List<UserDTO> result = userResource.getAllUsers();

		assertEquals(users, result);
		verify(userService).getAllUsers();
	}

	// --- getUserById ---
	@Test
	void getUserById_whenFound_shouldReturnOkWithUser() {
		when(userService.getUserById(userId)).thenReturn(Optional.of(sampleUser));

		Response response = userResource.getUserById(userId);

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(sampleUser, response.getEntity());
	}

	@Test
	void getUserById_whenNotFound_shouldReturnNotFound() {
		when(userService.getUserById(userId)).thenReturn(Optional.empty());

		Response response = userResource.getUserById(userId);

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getEntity());
	}

	// --- createUser ---
	@Test
	void createUser_shouldReturnCreatedWithUser() {
		when(userService.createUser(anyString(), anyString())).thenReturn(sampleUser);

		Response response = userResource.createUser(createUserRequest);

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertEquals(sampleUser, response.getEntity());
		verify(userService).createUser("testuser", "test@example.com");
	}

	// --- getUserWithProfile ---
	@Test
	void getUserWithProfile_whenFound_shouldReturnOkWithUser() {
		when(userService.getUserWithProfile(userId)).thenReturn(Optional.of(sampleUserWithProfile));

		Response response = userResource.getUserWithProfile(userId);

		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		assertEquals(sampleUser, response.getEntity());
	}

	@Test
	void getUserWithProfile_whenNotFound_shouldReturnNotFound() {
		when(userService.getUserWithProfile(userId)).thenReturn(Optional.empty());

		Response response = userResource.getUserWithProfile(userId);

		assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
		assertNull(response.getEntity());
	}

	// --- createUserProfile ---
	@Test
	void createUserProfile_shouldReturnCreatedWithProfile() {
		when(userService.createUserProfile(any(UUID.class), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(sampleProfile);

		Response response = userResource.createUserProfile(userId, createProfileRequest);

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
		assertEquals(sampleProfile, response.getEntity());
		verify(userService).createUserProfile(userId, "John", "Doe", "+123456789", "123 Main St");
	}

	// --- getUserRoles ---
	@Test
	void getUserRoles_shouldReturnListOfRoles() {
		List<RoleDTO> roles = List.of(sampleRole);
		when(userService.getUserRoles(userId)).thenReturn(roles);

		List<RoleDTO> result = userResource.getUserRoles(userId);

		assertEquals(roles, result);
		verify(userService).getUserRoles(userId);
	}

	// --- assignRole ---
	@Test
	void assignRole_shouldReturnNoContent() {
		doNothing().when(userService).assignRole(userId, roleId);

		Response response = userResource.assignRole(userId, roleId);

		assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
		assertNull(response.getEntity());
		verify(userService).assignRole(userId, roleId);
	}

	// --- searchUsers ---
	@Test
	void searchUsers_withAllParams_shouldBuildFiltersAndReturnResponse() {
		// given
		String username = "testuser";
		String email = "test@example.com";
		String id = userId.toString();
		String sortBy = "created_at";
		String sortDirection = "DESC";
		Integer limit = 50;
		Integer offset = 10;
		List<UserDTO> users = List.of(sampleUser);

		when(userService.searchUsers(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(users);

		// when
		Response response = userResource.searchUsers(
				username, email, id,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		SearchResponse entity = (SearchResponse) response.getEntity();
		assertEquals(users, entity.getData());
		assertEquals(limit, entity.getLimit());
		assertEquals(offset, entity.getOffset());

		verify(userService).searchUsers(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		assertEquals(3, capturedFilters.size());
		assertEquals(username, capturedFilters.get("username"));
		assertEquals(email, capturedFilters.get("email"));
		assertEquals(id, capturedFilters.get("id"));
	}

	@Test
	void searchUsers_withSomeParams_shouldBuildFiltersOnlyForProvidedParams() {
		// given
		String username = "testuser";
		String sortBy = "created_at";
		String sortDirection = "DESC";
		Integer limit = 100;
		Integer offset = 0;
		List<UserDTO> users = List.of(sampleUser);

		when(userService.searchUsers(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(users);

		// when
		Response response = userResource.searchUsers(
				username, null, null,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		verify(userService).searchUsers(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		assertEquals(1, capturedFilters.size());
		assertEquals(username, capturedFilters.get("username"));
		assertNull(capturedFilters.get("email"));
		assertNull(capturedFilters.get("id"));
	}

	@Test
	void searchUsers_withEmptyStringParams_shouldNotAddToFilters() {
		// given
		String username = "";
		String email = "   "; // blank
		String id = null;
		String sortBy = "created_at";
		String sortDirection = "DESC";
		Integer limit = 100;
		Integer offset = 0;
		List<UserDTO> users = List.of(sampleUser);

		when(userService.searchUsers(anyMap(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset)))
				.thenReturn(users);

		// when
		Response response = userResource.searchUsers(
				username, email, id,
				sortBy, sortDirection, limit, offset);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		verify(userService).searchUsers(filtersCaptor.capture(), eq(sortBy), eq(sortDirection), eq(limit), eq(offset));
		Map<String, String> capturedFilters = filtersCaptor.getValue();
		assertTrue(capturedFilters.isEmpty());
	}

	@Test
	void searchUsers_withDefaultValues_shouldUseDefaults() {
		// given
		List<UserDTO> users = List.of(sampleUser);
		when(userService.searchUsers(anyMap(), eq("created_at"), eq("DESC"), eq(100), eq(0)))
				.thenReturn(users);

		// when – no query params provided (all null, so defaults apply)
		Response response = userResource.searchUsers(null, null, null, null, null, null, null);

		// then
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		verify(userService).searchUsers(anyMap(), eq("created_at"), eq("DESC"), eq(100), eq(0));
	}
}