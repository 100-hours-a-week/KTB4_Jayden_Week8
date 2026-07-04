//package com.example.spring_rest_api.common.config;
//
//import com.example.spring_rest_api.user.entity.User;
//import com.example.spring_rest_api.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.stream.IntStream;
//
//@Configuration
//@Profile("development")
//@RequiredArgsConstructor
//public class SeedConfig {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Bean
//    ApplicationRunner seedRunner() {
//        return args -> seed();
//    }
//
//    @Transactional
//    void seed() {
//        if (userRepository.count() >= 20) return;
//
//        IntStream.rangeClosed(1, 10).forEach(i -> {
//            String rawPassword = "12341234aS!" + i;
//            User user = User.create("test" + i + "@abc.com", passwordEncoder.encode(rawPassword), "tester" + i, null);
//            userRepository.save(user);
//        });
//    }
//}
