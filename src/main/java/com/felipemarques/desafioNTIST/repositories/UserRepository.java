package com.felipemarques.desafioNTIST.repositories;

import com.felipemarques.desafioNTIST.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(User user) {
        String sql = "INSERT INTO TB_USER (id, name, email, password) VALUES (?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                user.getId(), user.getName(), user.getEmail(), user.getPassword());
    }

    public UserDetails findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM TB_USER WHERE email = ?";

        List<User> userFounded = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getObject("id", UUID.class));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            return user;
        }, email);

        return userFounded.isEmpty() ? null : userFounded.get(0);
    }
}