package com.otis.usersvc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import com.otis.common.exception.CreationFailedException;
import com.otis.common.exception.DataAccessException;
import com.otis.common.preference.DatabaseColumns;
import com.otis.common.preference.FilterKey;
import com.otis.common.util.DynamicQueryBuilder;
import com.otis.common.util.SqlQueryLoader;
import com.otis.usersvc.dto.RoleDTO;
import com.otis.usersvc.dto.UserDTO;
import com.otis.usersvc.dto.UserProfileDTO;
import com.otis.usersvc.dto.UserWithProfileDTO;
import com.otis.usersvc.util.DtoMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository {
	private static final String QUERY_FILE = "user-queries.sql";

	private final DataSource dataSource;

	public UserRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<UserDTO> findAll() {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findAllUsers");
		List<UserDTO> users = new ArrayList<>();

		try (Connection conn = dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				users.add(DtoMapper.mapToUserDTO(rs));
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding all users", e);
		}

		return users;
	}

	public Optional<UserDTO> findById(UUID id) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findUserById");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(DtoMapper.mapToUserDTO(rs));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding user by id", e);
		}

		return Optional.empty();
	}

	public UserDTO create(String username, String email) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "insertUser");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			stmt.setString(2, email);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return DtoMapper.mapToUserDTO(rs);
				}
			}
		} catch (SQLException e) {
			throw new CreationFailedException("Error creating user", e);
		}

		throw new CreationFailedException("Failed to create user");
	}

	public Optional<UserWithProfileDTO> findUserWithProfile(UUID userId) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findUserWithProfile");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return Optional.of(DtoMapper.mapToUserWithProfileDTO(rs));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding user with profile", e);
		}

		return Optional.empty();
	}

	public UserProfileDTO createProfile(UUID userId, String firstName, String lastName, String phone, String address) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "insertUserProfile");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, userId);
			stmt.setString(2, firstName);
			stmt.setString(3, lastName);
			stmt.setString(4, phone);
			stmt.setString(5, address);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return DtoMapper.mapToUserProfileDTO(rs);
				}
			}
		} catch (SQLException e) {
			throw new CreationFailedException("Error creating user profile", e);
		}

		throw new CreationFailedException("Failed to create user profile");
	}

	public List<RoleDTO> findRolesByUserId(UUID userId) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findRolesByUserId");
		List<RoleDTO> roles = new ArrayList<>();

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					roles.add(DtoMapper.mapToRoleDTO(rs));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error finding roles for user", e);
		}

		return roles;
	}

	public void assignRoleToUser(UUID userId, UUID roleId) {
		String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "assignRoleToUser");

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setObject(1, userId);
			stmt.setObject(2, roleId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException("Error assigning role to user", e);
		}
	}

	// Dynamic filter method
	public List<UserDTO> findByFilters(Map<String, String> filters, String sortBy, String sortDirection,
			Integer limit, Integer offset) {
		DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder("users");
		queryBuilder.select(DatabaseColumns.ID, DatabaseColumns.USERNAME, DatabaseColumns.EMAIL,
				DatabaseColumns.CREATED_AT);

		// Apply filters
		if (filters.containsKey(FilterKey.USERNAME)) {
			queryBuilder.where(DatabaseColumns.USERNAME, "ILIKE", "%" + filters.get("username") + "%");
		}

		if (filters.containsKey(FilterKey.EMAIL)) {
			queryBuilder.where(DatabaseColumns.EMAIL, "ILIKE", "%" + filters.get("email") + "%");
		}

		if (filters.containsKey(FilterKey.ID)) {
			queryBuilder.where(DatabaseColumns.ID, "=", UUID.fromString(filters.get("id")));
		}

		// Apply sorting
		if (sortBy != null && !sortBy.isEmpty()) {
			String direction = (sortDirection != null && sortDirection.equalsIgnoreCase("ASC")) ? "ASC" : "DESC";
			queryBuilder.orderBy(sortBy, direction);
		} else {
			queryBuilder.orderBy(DatabaseColumns.CREATED_AT, "DESC");
		}

		// Apply pagination
		if (limit != null) {
			queryBuilder.limit(limit);
		}

		if (offset != null) {
			queryBuilder.offset(offset);
		}

		String sql = queryBuilder.buildQuery();
		List<UserDTO> users = new ArrayList<>();

		try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			queryBuilder.setParameters(stmt);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					users.add(DtoMapper.mapToUserDTO(rs));
				}
			}
		} catch (SQLException e) {
			throw new DataAccessException("Error executing dynamic query", e);
		}

		return users;
	}
}
