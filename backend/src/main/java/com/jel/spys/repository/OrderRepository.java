package com.jel.spys.repository;

import com.jel.spys.entity.OrderEntity;
import com.jel.spys.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByUser(UserEntity user);

    List<OrderEntity> findByUserOrderByCreatedAtDesc(UserEntity user);

    List<OrderEntity> findAllByOrderByCreatedAtDesc();
}
