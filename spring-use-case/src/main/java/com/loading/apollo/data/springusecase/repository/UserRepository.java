package com.loading.apollo.data.springusecase.repository;

import com.loading.apollo.data.springusecase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * desc:
 *
 * @author Lo_ading
 * @version 1.0.0
 * @date 2021/2/26
 */
public interface UserRepository extends JpaRepository<User, Integer> {
}
