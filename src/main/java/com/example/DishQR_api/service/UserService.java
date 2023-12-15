package com.example.DishQR_api.service;

import com.example.DishQR_api.model.User;
import com.example.DishQR_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public User save(User newUser) {
        if (newUser.getId() == null) {
            newUser.setCreatedAt(LocalDateTime.now());
        }

        newUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(newUser);
    }

    public void updateUserLastDiscountOrderNumber(String userId, int lastDiscountOrderNumber) {
        Optional<User> userOptional = userRepository.findById(userId);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            user = user.toBuilder().lastDiscountOrderNumber(lastDiscountOrderNumber).build();
            userRepository.save(user);
        }
    }

    public Integer getUserLastDiscountOrderNumber(String userId) {
        Optional<User> user = userRepository.findById(userId);

        if(user.isPresent()){
            return user.get().getLastDiscountOrderNumber();
        } else {
            return 0;
        }
    }
}
