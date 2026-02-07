package com.otis.usersvc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.otis.usersvc.model.User;
import com.otis.usersvc.model.UserProfile;

public class DtoMapper {
    private DtoMapper() {
    }

    public static User mapToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId((UUID) rs.getObject("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        return user;
    }

    public static UserProfile mapToUserProfile(ResultSet rs) throws SQLException {
        UserProfile profile = new UserProfile();
        profile.setId((UUID) rs.getObject("id"));
        profile.setUserId((UUID) rs.getObject("user_id"));
        profile.setFirstName(rs.getString("first_name"));
        profile.setLastName(rs.getString("last_name"));
        profile.setPhone(rs.getString("phone"));
        profile.setAddress(rs.getString("address"));

        return profile;
    }
}
