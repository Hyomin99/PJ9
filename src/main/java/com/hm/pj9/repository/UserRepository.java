package com.hm.pj9.repository;

import com.hm.pj9.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {


    @Query("SELECT u.userPw FROM User u WHERE u.userId = :userId")
    String getPwById(String userId);

    void deleteByUserId(String userId);

}
