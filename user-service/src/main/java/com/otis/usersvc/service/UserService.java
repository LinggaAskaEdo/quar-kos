package com.otis.usersvc.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.otis.usersvc.kafka.UserEventProducer;
import com.otis.usersvc.model.Role;
import com.otis.usersvc.model.User;
import com.otis.usersvc.model.UserProfile;
import com.otis.usersvc.model.UserWithProfile;
import com.otis.usersvc.repository.UserRepository;
import com.otis.usersvc.util.CorrelationIdFilter;

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

    public List<User> getAllUsers() {
        UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
        LOG.infof("[%s] Getting all users", correlationId);

        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
        LOG.infof("[%s] Getting user by id: %s", correlationId, id);

        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(String username, String email) {
        UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
        LOG.infof("[%s] Creating user: %s", correlationId, username);

        User user = userRepository.create(username, email);
        eventProducer.sendUserCreatedEvent(correlationId, user.getId(), user.getUsername(), user.getEmail());

        return user;
    }

    public Optional<UserWithProfile> getUserWithProfile(UUID userId) {
        return userRepository.findUserWithProfile(userId);
    }

    @Transactional
    public UserProfile createUserProfile(UUID userId, String firstName, String lastName, String phone, String address) {
        UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
        LOG.infof("[%s] Creating profile for user: %s", correlationId, userId);

        UserProfile profile = userRepository.createProfile(userId, firstName, lastName, phone, address);
        eventProducer.sendUserProfileCreatedEvent(correlationId, userId, firstName, lastName);

        return profile;
    }

    public List<Role> getUserRoles(UUID userId) {
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

    public List<User> searchUsers(Map<String, String> filters, String sortBy, String sortDirection, Integer limit,
            Integer offset) {
        UUID correlationId = CorrelationIdFilter.getCurrentCorrelationId();
        LOG.infof("[%s] Searching users with filters: %s", correlationId, filters);

        return userRepository.findByFilters(filters, sortBy, sortDirection, limit, offset);
    }
}
