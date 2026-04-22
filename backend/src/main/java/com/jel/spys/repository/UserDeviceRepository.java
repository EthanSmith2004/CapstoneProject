package com.jel.spys.repository;

import com.jel.spys.entity.UserDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDeviceEntity, Long> {
    Optional<UserDeviceEntity> findByEndpoint(String endpoint);
    List<UserDeviceEntity> findByUserId(Long userId);
    List<UserDeviceEntity> findByIsActiveTrue();
}
