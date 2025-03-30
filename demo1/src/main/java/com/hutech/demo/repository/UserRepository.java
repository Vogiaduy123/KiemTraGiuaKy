package com.hutech.HaMinhDuc.repository;
import com.hutech.HaMinhDuc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}