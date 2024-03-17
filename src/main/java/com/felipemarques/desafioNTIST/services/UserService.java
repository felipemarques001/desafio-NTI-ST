package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.exceptions.InvalidPasswordException;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserRegisterDTO dto) {
        if(userRepository.findByEmail(dto.getEmail()) != null) {
            throw new FieldAlreadyInUseException("email", dto.getEmail());
        }

        validatePassword(dto.getPassword());

        User newUser = new User(
                UUID.randomUUID(),
                dto.getName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword())
        );

        userRepository.save(newUser);
    }

    private void validatePassword(String password) {
        Pattern specialCharPattern = Pattern.compile("[!@#$%&*]");
        Matcher matcher = specialCharPattern.matcher(password);

        if(!password.matches(".*[A-Z].*") ||
           !password.matches(".*[a-z].*") ||
           !password.matches(".*\\d.*") ||
           !matcher.find()) {
            throw new InvalidPasswordException("The password must contain at least one uppercase letter," +
                    " one lowercase letter, one number, and one special character.");
        }
    }
}
