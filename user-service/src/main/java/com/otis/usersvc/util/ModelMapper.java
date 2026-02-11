package com.otis.usersvc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.otis.common.preference.DatabaseColumns;
import com.otis.usersvc.model.User;
import com.otis.usersvc.model.UserProfile;

public class ModelMapper {
	private ModelMapper() {
	}

	public static User mapToUser(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId((UUID) rs.getObject(DatabaseColumns.ID));
		user.setUsername(rs.getString(DatabaseColumns.USERNAME));
		user.setEmail(rs.getString(DatabaseColumns.EMAIL));
		user.setCreatedAt(rs.getTimestamp(DatabaseColumns.CREATED_AT).toLocalDateTime());

		return user;
	}

	public static UserProfile mapToUserProfile(ResultSet rs) throws SQLException {
		UserProfile profile = new UserProfile();
		profile.setId((UUID) rs.getObject(DatabaseColumns.ID));
		profile.setUserId((UUID) rs.getObject(DatabaseColumns.USER_ID));
		profile.setFirstName(rs.getString(DatabaseColumns.FIRST_NAME));
		profile.setLastName(rs.getString(DatabaseColumns.LAST_NAME));
		profile.setPhone(rs.getString(DatabaseColumns.PHONE));
		profile.setAddress(rs.getString(DatabaseColumns.ADDRESS));

		return profile;
	}
}
