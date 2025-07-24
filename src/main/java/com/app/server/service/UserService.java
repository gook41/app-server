package com.app.server.service;

import com.app.server.domain.User;

public interface UserService {
    User saveUser(User user);
    User findUserById(Long id);
}
