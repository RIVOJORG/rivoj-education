package uz.rivoj.education.service.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.rivoj.education.entity.UserEntity;
import uz.rivoj.education.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findUserEntityByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Username not found: " + phoneNumber));
    }
}
