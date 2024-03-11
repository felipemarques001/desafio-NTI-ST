package com.felipemarques.desafioNTIST.services;

import com.felipemarques.desafioNTIST.dtos.UserRegisterDTO;
import com.felipemarques.desafioNTIST.exceptions.FieldAlreadyInUseException;
import com.felipemarques.desafioNTIST.models.User;
import com.felipemarques.desafioNTIST.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(UserRegisterDTO dto) {
        if(userRepository.findByEmail(dto.email()) != null)
            throw new FieldAlreadyInUseException("email", dto.email());

        User newUser = new User(
                UUID.randomUUID(),
                dto.name(),
                dto.email(),
                dto.password()
        );

        userRepository.save(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username);
    }
}
