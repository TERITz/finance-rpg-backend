package com.financerpg.backend.service;

import com.financerpg.backend.entity.LineLinkCode;
import com.financerpg.backend.entity.User;
import com.financerpg.backend.repository.LineLinkCodeRepository;
import com.financerpg.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LineLinkService {

    private final LineLinkCodeRepository lineLinkCodeRepository;
    private final UserRepository userRepository;

    public String generateLinkCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("ไม่พบผู้ใช้"));

        lineLinkCodeRepository.deleteByUserId(user.getId());

        String code = String.format("%06d", new Random().nextInt(999999));

        LineLinkCode linkCode = LineLinkCode.builder()
                .user(user)
                .code(code)
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .build();

        lineLinkCodeRepository.save(linkCode);
        return code;
    }

    @Transactional
    public boolean linkLineUser(String code, String lineUserId) {
        LineLinkCode linkCode = lineLinkCodeRepository.findByCode(code)
                .orElse(null);

        if (linkCode == null) return false;
        if (linkCode.getExpiredAt().isBefore(LocalDateTime.now())) return false;

        User user = linkCode.getUser();
        user.setLineUserId(lineUserId);
        userRepository.save(user);

        lineLinkCodeRepository.delete(linkCode);
        return true;
    }

    public User getUserByLineId(String lineUserId) {
        return userRepository.findByLineUserId(lineUserId)
                .orElse(null);
    }
}