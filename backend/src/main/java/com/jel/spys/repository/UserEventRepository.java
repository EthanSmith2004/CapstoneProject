package com.jel.spys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jel.spys.entity.UserEventEntity;

@Repository
public interface UserEventRepository extends JpaRepository<UserEventEntity, Long> {
}
