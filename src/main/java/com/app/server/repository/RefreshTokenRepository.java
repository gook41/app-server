package com.app.server.repository;

import com.app.server.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰 문자열로 리프레시 토큰 조회
     * @param token 토큰 문자열
     * @return RefreshToken 엔티티
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자 ID로 리프레시 토큰 조회 (최신순)
     * @param userId 사용자 ID
     * @return RefreshToken 리스트
     */
    List<RefreshToken> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 사용자 ID로 유효한 리프레시 토큰 조회
     * @param userId 사용자 ID
     * @param now 현재 시간
     * @return 유효한 RefreshToken 리스트
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.revoked = false AND rt.expiryDate > :now")
    List<RefreshToken> findValidTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * 사용자의 모든 리프레시 토큰 무효화
     * @param userId 사용자 ID
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.userId = :userId")
    void revokeAllTokensByUserId(@Param("userId") Long userId);

    /**
     * 특정 토큰 무효화
     * @param token 토큰 문자열
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
    void revokeToken(@Param("token") String token);

    /**
     * 만료된 토큰 삭제
     * @param now 현재 시간
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * 무효화된 토큰 삭제
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.revoked = true")
    void deleteRevokedTokens();

    /**
     * 사용자 ID로 토큰 존재 여부 확인
     * @param userId 사용자 ID
     * @return 존재 여부
     */
    boolean existsByUserId(Long userId);

    /**
     * 토큰 문자열로 존재 여부 확인
     * @param token 토큰 문자열
     * @return 존재 여부
     */
    boolean existsByToken(String token);

    /**
     * 사용자별 토큰 개수 조회
     * @param userId 사용자 ID
     * @return 토큰 개수
     */
    long countByUserId(Long userId);

    /**
     * 사용자별 유효한 토큰 개수 조회
     * @param userId 사용자 ID
     * @param now 현재 시간
     * @return 유효한 토큰 개수
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.userId = :userId AND rt.revoked = false AND rt.expiryDate > :now")
    long countValidTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}