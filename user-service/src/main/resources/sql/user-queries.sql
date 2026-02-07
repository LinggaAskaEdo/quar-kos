-- User queries
-- @name: findAllUsers
SELECT id, username, email, created_at 
FROM users u 
ORDER BY u.created_at DESC;

-- @name: findUserById
SELECT id, username, email, created_at 
FROM users 
WHERE id = ?::uuid;

-- @name: findUserByUsername
SELECT id, username, email, created_at 
FROM users 
WHERE username = ?;

-- @name: insertUser
INSERT INTO users (username, email) 
VALUES (?, ?) 
RETURNING id, username, email, created_at;

-- @name: updateUser
UPDATE users 
SET username = ?, email = ? 
WHERE id = ?::uuid 
RETURNING id, username, email, created_at;

-- @name: deleteUser
DELETE FROM users WHERE id = ?::uuid;

-- User Profile queries (One-to-One relationship)
-- @name: findUserWithProfile
SELECT u.id as user_id, u.username, u.email, u.created_at,
       p.id as profile_id, p.first_name, p.last_name, p.phone, p.address
FROM users u
LEFT JOIN user_profiles p ON u.id = p.user_id
WHERE u.id = ?::uuid;

-- @name: findProfileByUserId
SELECT id, user_id, first_name, last_name, phone, address 
FROM user_profiles 
WHERE user_id = ?::uuid;

-- @name: insertUserProfile
INSERT INTO user_profiles (user_id, first_name, last_name, phone, address) 
VALUES (?::uuid, ?, ?, ?, ?) 
RETURNING id, user_id, first_name, last_name, phone, address;

-- @name: updateUserProfile
UPDATE user_profiles 
SET first_name = ?, last_name = ?, phone = ?, address = ? 
WHERE user_id = ?::uuid 
RETURNING id, user_id, first_name, last_name, phone, address;

-- @name: deleteUserProfile
DELETE FROM user_profiles WHERE user_id = ?::uuid;

-- Role queries
-- @name: findAllRoles
SELECT id, name, description FROM roles ORDER BY name;

-- @name: findRoleById
SELECT id, name, description FROM roles WHERE id = ?::uuid;

-- User-Role queries (Many-to-Many relationship)
-- @name: findUserWithRoles
SELECT u.id as user_id, u.username, u.email, u.created_at,
       r.id as role_id, r.name as role_name, r.description as role_description,
       ur.assigned_at
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.id = ?::uuid;

-- @name: findRolesByUserId
SELECT r.id, r.name, r.description, ur.assigned_at
FROM roles r
JOIN user_roles ur ON r.id = ur.role_id
WHERE ur.user_id = ?::uuid;

-- @name: assignRoleToUser
INSERT INTO user_roles (user_id, role_id) 
VALUES (?::uuid, ?::uuid) 
ON CONFLICT (user_id, role_id) DO NOTHING;

-- @name: removeRoleFromUser
DELETE FROM user_roles 
WHERE user_id = ?::uuid AND role_id = ?::uuid;

-- @name: findUsersByRoleId
SELECT u.id, u.username, u.email, u.created_at, ur.assigned_at
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
WHERE ur.role_id = ?::uuid;
