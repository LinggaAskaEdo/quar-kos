package com.otis.usersvc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.otis.common.preference.DatabaseColumns;
import com.otis.usersvc.dto.RoleDTO;
import com.otis.usersvc.dto.UserDTO;
import com.otis.usersvc.dto.UserProfileDTO;
import com.otis.usersvc.dto.UserWithProfileDTO;

public class DtoMapper {
	private DtoMapper() {
	}

	public static UserDTO mapToUserDTO(ResultSet rs) throws SQLException {
		return new UserDTO(
				(UUID) rs.getObject(DatabaseColumns.ID),
				rs.getString(DatabaseColumns.USERNAME),
				rs.getString(DatabaseColumns.EMAIL),
				rs.getTimestamp(DatabaseColumns.CREATED_AT).toLocalDateTime());
	}

	public static UserProfileDTO mapToUserProfileDTO(ResultSet rs) throws SQLException {
		return new UserProfileDTO(
				(UUID) rs.getObject(DatabaseColumns.ID),
				(UUID) rs.getObject(DatabaseColumns.USER_ID),
				rs.getString(DatabaseColumns.FIRST_NAME),
				rs.getString(DatabaseColumns.LAST_NAME),
				rs.getString(DatabaseColumns.PHONE),
				rs.getString(DatabaseColumns.ADDRESS));
	}

	public static RoleDTO mapToRoleDTO(ResultSet rs) throws SQLException {
		return new RoleDTO(
				(UUID) rs.getObject(DatabaseColumns.ID),
				rs.getString(DatabaseColumns.NAME),
				rs.getString(DatabaseColumns.DESCRIPTION),
				rs.getTimestamp(DatabaseColumns.ASSIGNED_AT).toLocalDateTime());
	}

	public static UserWithProfileDTO mapToUserWithProfileDTO(ResultSet rs) throws SQLException {
		return new UserWithProfileDTO(
				(UUID) rs.getObject(DatabaseColumns.USER_ID),
				rs.getString(DatabaseColumns.USERNAME),
				rs.getString(DatabaseColumns.EMAIL),
				rs.getTimestamp(DatabaseColumns.CREATED_AT).toLocalDateTime(),
				new UserProfileDTO(
						(UUID) rs.getObject(DatabaseColumns.PROFILE_ID),
						(UUID) rs.getObject(DatabaseColumns.USER_ID),
						rs.getString(DatabaseColumns.FIRST_NAME),
						rs.getString(DatabaseColumns.LAST_NAME),
						rs.getString(DatabaseColumns.PHONE),
						rs.getString(DatabaseColumns.ADDRESS)));
	}
}
