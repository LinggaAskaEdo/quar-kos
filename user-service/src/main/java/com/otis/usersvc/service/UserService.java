package com.otis.usersvc.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.otis.common.util.CorrelationIdFilter;
import com.otis.usersvc.dto.RoleDTO;
import com.otis.usersvc.dto.UserDTO;
import com.otis.usersvc.dto.UserProfileDTO;
import com.otis.usersvc.dto.UserWithProfileDTO;
import com.otis.usersvc.kafka.UserEventProducer;
import com.otis.usersvc.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {
	private static final Logger LOG = Logger.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final UserEventProducer eventProducer;

	public UserService(UserRepository userRepository, UserEventProducer eventProducer) {
		this.userRepository = userRepository;
		this.eventProducer = eventProducer;
	}

	public List<UserDTO> getAllUsers() {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Getting all users", correlationId);

		return userRepository.findAll();
	}

	public Optional<UserDTO> getUserById(UUID id) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Getting user by id: %s", correlationId, id);

		return userRepository.findById(id);
	}

	@Transactional
	public UserDTO createUser(String username, String email) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Creating user: %s", correlationId, username);

		UserDTO user = userRepository.create(username, email);
		eventProducer.sendUserCreatedEvent(correlationId, user.id(), user.username(), user.email());

		return user;
	}

	public Optional<UserWithProfileDTO> getUserWithProfile(UUID userId) {
		return userRepository.findUserWithProfile(userId);
	}

	@Transactional
	public UserProfileDTO createUserProfile(UUID userId, String firstName, String lastName, String phone,
			String address) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Creating profile for user: %s", correlationId, userId);

		UserProfileDTO profile = userRepository.createProfile(userId, firstName, lastName, phone, address);
		eventProducer.sendUserProfileCreatedEvent(correlationId, userId, firstName, lastName);

		return profile;
	}

	public List<RoleDTO> getUserRoles(UUID userId) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Getting roles for user: %s", correlationId, userId);

		return userRepository.findRolesByUserId(userId);
	}

	@Transactional
	public void assignRole(UUID userId, UUID roleId) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Assigning role %s to user %s", correlationId, roleId, userId);

		userRepository.assignRoleToUser(userId, roleId);
	}

	public List<UserDTO> searchUsers(Map<String, String> filters, String sortBy, String sortDirection,
			Integer limit, Integer offset) {
		UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
		LOG.infof("[%s] Searching users with filters: %s", correlationId, filters);

		return userRepository.findByFilters(filters, sortBy, sortDirection, limit, offset);
	}
}
