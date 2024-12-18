package com.sp.ordermanagement.repository;

import com.sp.ordermanagement.enitity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
