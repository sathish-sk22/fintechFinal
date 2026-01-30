package com.example.demo.repository;


import com.example.demo.Util.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDetails,Long> {
    Optional<UserDetails> findByEmail(String email);
    Optional<UserDetails> findByUsername(String username);


}
