package com.app.server.service;

import com.app.server.domain.User;
import com.app.server.exception.BadRequestException;
import com.app.server.exception.DuplicateNicknameException;
import com.app.server.exception.ResourceNotFoundException;
import com.app.server.mapper.UserMapper;
import com.app.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;



    @Override
    public User createUser(User.CreateRequest user) {
        if (existsByEmail(user.email())) {
            throw new BadRequestException("Email already exists: " + user.email());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findByDeletedFalse();
    }

    @Override
    public User updateUser(Long id, User.UpdateRequest request) {
        User existingUser = findUserById(id);

        // 닉네임 변경 시 중복 체크.
        if (!existingUser.getNickname().equals(request.nickname()) &&
                userRepository.existsByEmail(request.nickname())) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다: " + request.nickname());
        }
        // MapStruct를 사용해서 DTO의 내용을 엔티티에 반영. 이게 존나 깔끔한 방법.
        userMapper.updateEntity(request, existingUser);

        // updatedAt은 JPA Auditing이 알아서 해주니까 괜히 건드리지 말자
        // existingUser.setUpdatedAt(LocalDateTime.now());

        // 비밀번호 변경은 별도 API로 빼는게 국룰임. 여기선 처리 안함.
        return userRepository.save(existingUser); // @Transactional 덕에 사실 save 안해도 더티체킹으로 반영됨. 근데 명시적으로 씀.
    }


    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.setDeleted(true);
        // updatedAt은 JPA Auditing이 알아서 함.
        // user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(false);
        // updatedAt은 JPA Auditing이 알아서 함.
        // user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByDeletedFalse();
    }
}
