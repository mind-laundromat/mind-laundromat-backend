package com.example.mind_laundromat.user.service;

import com.example.mind_laundromat.cbt.entity.EmotionType;
import com.example.mind_laundromat.user.dto.CustomUserDetails;
import com.example.mind_laundromat.user.dto.UpdateUserName;
import com.example.mind_laundromat.user.dto.UpdateUserProfileEmotion;
import com.example.mind_laundromat.user.dto.UserDTO;
import com.example.mind_laundromat.user.entity.Role;
import com.example.mind_laundromat.user.entity.User;
import com.example.mind_laundromat.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void registerUser(UserDTO userDTO) {
        if(userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("This email already exists.");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        User user = User.builder()
                .email(userDTO.getEmail())
                .first_name(userDTO.getFirst_name())
                .last_name(userDTO.getLast_name())
                .password(encodedPassword)
                .role(Role.valueOf("MEMBER"))
                .profile_emotion(EmotionType.HAPPINESS)
                .build();

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return new CustomUserDetails(user);
    }

    // 유저 정보 조회
    public UserDTO findByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return UserDTO.builder()
                .email(user.getEmail())
                .first_name(user.getFirst_name())
                .last_name(user.getLast_name())
                .password(user.getPassword())
                .build();
    }

    // 유저 정보 삭제
    @Transactional
    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }

    // 유저 이름 변경
    @Transactional
    public void updateUserName(UpdateUserName updateUserName) {
        Long userId = userRepository.selectIdByEmail(updateUserName.getEmail());

        if(userRepository.updateUserName(userId, updateUserName.getFirst_name(), updateUserName.getLast_name()) != 1){
            throw new EntityNotFoundException("error updateUserName");
        }
    }

    // 유저 프로필 아이콘 변경
    @Transactional
    public void updateProfileEmotion(UpdateUserProfileEmotion updateUserProfileEmotion) {
        Long userId = userRepository.selectIdByEmail(updateUserProfileEmotion.getEmail());

        EmotionType emotionType;
        try {
            emotionType = EmotionType.valueOf(updateUserProfileEmotion.getEmotion_name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 감정 타입입니다: " + updateUserProfileEmotion.getEmotion_name());
        }

        if(userRepository.updateProfileEmotion(userId, emotionType) != 1){
            throw new EntityNotFoundException("error updateProfileEmotion");
        }
    }
}
