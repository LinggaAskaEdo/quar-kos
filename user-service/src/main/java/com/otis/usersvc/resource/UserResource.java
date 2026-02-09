package com.otis.usersvc.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.otis.usersvc.dto.CreateProfileRequest;
import com.otis.usersvc.dto.CreateUserRequest;
import com.otis.usersvc.dto.SearchResponse;
import com.otis.usersvc.model.Role;
import com.otis.usersvc.model.User;
import com.otis.usersvc.model.UserProfile;
import com.otis.usersvc.service.UserService;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
	private final UserService userService;

	public UserResource(UserService userService) {
		this.userService = userService;
	}

	@GET
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GET
	@Path("/{id}")
	public Response getUserById(@PathParam("id") UUID id) {
		return userService.getUserById(id)
				.map(user -> Response.ok(user).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	public Response createUser(@Valid CreateUserRequest request) {
		User user = userService.createUser(request.getUsername(), request.getEmail());
		return Response.status(Response.Status.CREATED).entity(user).build();
	}

	@GET
	@Path("/{id}/with-profile")
	public Response getUserWithProfile(@PathParam("id") UUID id) {
		return userService.getUserWithProfile(id)
				.map(user -> Response.ok(user).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Path("/{id}/profile")
	public Response createUserProfile(@PathParam("id") UUID id, @Valid CreateProfileRequest request) {
		UserProfile profile = userService.createUserProfile(id, request.getFirstName(), request.getLastName(),
				request.getPhone(), request.getAddress());

		return Response.status(Response.Status.CREATED).entity(profile).build();
	}

	@GET
	@Path("/{id}/roles")
	public List<Role> getUserRoles(@PathParam("id") UUID id) {
		return userService.getUserRoles(id);
	}

	@POST
	@Path("/{userId}/roles/{roleId}")
	public Response assignRole(@PathParam("userId") UUID userId, @PathParam("roleId") UUID roleId) {
		userService.assignRole(userId, roleId);
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	@GET
	@Path("/search")
	public Response searchUsers(
			@QueryParam("username") String username,
			@QueryParam("email") String email,
			@QueryParam("id") String id,
			@QueryParam("sortBy") @DefaultValue("created_at") String sortBy,
			@QueryParam("sortDirection") @DefaultValue("DESC") String sortDirection,
			@QueryParam("limit") @DefaultValue("100") Integer limit,
			@QueryParam("offset") @DefaultValue("0") Integer offset) {

		Map<String, String> filters = new HashMap<>();
		if (username != null && !username.isEmpty()) {
			filters.put("username", username);
		}

		if (email != null && !email.isEmpty()) {
			filters.put("email", email);
		}

		if (id != null && !id.isEmpty()) {
			filters.put("id", id);
		}

		List<User> users = userService.searchUsers(filters, sortBy, sortDirection, limit, offset);

		return Response.ok(new SearchResponse(users, filters, limit, offset)).build();
	}
}
