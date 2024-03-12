package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.exceptions.InvalidPasswordException;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(UserRegisterDTO dto) {
        if(userRepository.findByEmail(dto.email()) != null)
            throw new FieldAlreadyInUseException("email", dto.email());

        validatePassword(dto.password());

        User newUser = new User(
                UUID.randomUUID(),
                dto.name(),
                dto.email(),
                passwordEncoder.encode(dto.password())
        );

        userRepository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
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
