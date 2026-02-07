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

import com.otis.usersvc.exception.CreationFailedException;
import com.otis.usersvc.exception.DataAccessException;
import com.otis.usersvc.model.Role;
import com.otis.usersvc.model.User;
import com.otis.usersvc.model.UserProfile;
import com.otis.usersvc.model.UserWithProfile;
import com.otis.usersvc.preference.DatabaseColumns;
import com.otis.usersvc.util.DtoMapper;
import com.otis.usersvc.util.DynamicQueryBuilder;
import com.otis.usersvc.util.SqlQueryLoader;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository {
    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String QUERY_FILE = "user-queries.sql";

    public List<User> findAll() {
        String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findAllUsers");
        List<User> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(DtoMapper.mapToUser(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all users", e);
        }

        return users;
    }

    public Optional<User> findById(UUID id) {
        String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findUserById");

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(DtoMapper.mapToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by id", e);
        }

        return Optional.empty();
    }

    public User create(String username, String email) {
        String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "insertUser");

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return DtoMapper.mapToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new CreationFailedException("Error creating user", e);
        }

        throw new CreationFailedException("Failed to create user");
    }

    public Optional<UserWithProfile> findUserWithProfile(UUID userId) {
        String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findUserWithProfile");

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserWithProfile uwp = new UserWithProfile();
                    uwp.setUserId((UUID) rs.getObject(DatabaseColumns.USER_ID));
                    uwp.setUsername(rs.getString(DatabaseColumns.USERNAME));
                    uwp.setEmail(rs.getString(DatabaseColumns.EMAIL));
                    uwp.setCreatedAt(rs.getTimestamp(DatabaseColumns.CREATED_AT).toLocalDateTime());

                    Object profileIdObj = rs.getObject(DatabaseColumns.PROFILE_ID);
                    if (profileIdObj != null) {
                        UserProfile profile = new UserProfile();
                        profile.setId((UUID) profileIdObj);
                        profile.setUserId((UUID) rs.getObject(DatabaseColumns.USER_ID));
                        profile.setFirstName(rs.getString(DatabaseColumns.FIRST_NAME));
                        profile.setLastName(rs.getString(DatabaseColumns.LAST_NAME));
                        profile.setPhone(rs.getString(DatabaseColumns.PHONE));
                        profile.setAddress(rs.getString(DatabaseColumns.ADDRESS));
                        uwp.setProfile(profile);
                    }

                    return Optional.of(uwp);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user with profile", e);
        }

        return Optional.empty();
    }

    public UserProfile createProfile(UUID userId, String firstName, String lastName, String phone, String address) {
        String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "insertUserProfile");

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phone);
            stmt.setString(5, address);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return DtoMapper.mapToUserProfile(rs);
                }
            }
        } catch (SQLException e) {
            throw new CreationFailedException("Error creating user profile", e);
        }

        throw new CreationFailedException("Failed to create user profile");
    }

    public List<Role> findRolesByUserId(UUID userId) {
        String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "findRolesByUserId");
        List<Role> roles = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Role role = new Role();
                    role.setId((UUID) rs.getObject(DatabaseColumns.ID));
                    role.setName(rs.getString(DatabaseColumns.NAME));
                    role.setDescription(rs.getString(DatabaseColumns.DESCRIPTION));
                    role.setAssignedAt(rs.getTimestamp(DatabaseColumns.ASSIGNED_AT).toLocalDateTime());
                    roles.add(role);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding roles for user", e);
        }

        return roles;
    }

    public void assignRoleToUser(Long userId, Long roleId) {
        String sql = SqlQueryLoader.loadQuery(QUERY_FILE, "assignRoleToUser");

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error assigning role to user", e);
        }
    }

    // Dynamic filter method
    public List<User> findByFilters(Map<String, String> filters, String sortBy, String sortDirection,
            Integer limit, Integer offset) {
        DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder("users");
        queryBuilder.select(DatabaseColumns.ID, DatabaseColumns.USERNAME, DatabaseColumns.EMAIL,
                DatabaseColumns.CREATED_AT);

        // Apply filters
        if (filters.containsKey(DatabaseColumns.USERNAME)) {
            queryBuilder.where(DatabaseColumns.USERNAME, "ILIKE", "%" + filters.get("username") + "%");
        }

        if (filters.containsKey(DatabaseColumns.EMAIL)) {
            queryBuilder.where(DatabaseColumns.EMAIL, "ILIKE", "%" + filters.get("email") + "%");
        }

        if (filters.containsKey(DatabaseColumns.ID)) {
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
        List<User> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            queryBuilder.setParameters(stmt);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(DtoMapper.mapToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error executing dynamic query", e);
        }

        return users;
    }
}
