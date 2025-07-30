package com.app.server.domain.BusinessExceptions;

/**
 * 중복된 닉네임으로 update,가입 시도 시 발생하는 예외
 */
public class DuplicateNicknameException extends BusinessException {
    public DuplicateNicknameException(String nickname) {
        super("이미 사용 중인 닉네임입니다: " + nickname);
    }

    public DuplicateNicknameException() {
        super("이미 사용 중인 닉네임입니다.");

    }

}
