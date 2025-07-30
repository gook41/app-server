package com.app.server.infrastructure.persistence;

import com.app.server.domain.User;
import com.app.server.domain.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresUserRepository extends JpaRepository<User, Long>, UserRepository {
}