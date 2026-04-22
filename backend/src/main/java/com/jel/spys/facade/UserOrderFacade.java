package com.jel.spys.facade;

import com.jel.spys.entity.OrderItemStatus;
import com.jel.spys.entity.UserEntity;
import com.jel.spys.entity.UserEventType;
import com.jel.spys.model.OrderItemDTO;
import com.jel.spys.model.OrderDTO;
import com.jel.spys.model.PlaceOrderRequest;
import com.jel.spys.service.OrderService;
import com.jel.spys.service.UserEventService;
import com.jel.spys.service.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserOrderFacade {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    public List<OrderItemDTO> getUserOrders() {
        UserEntity currentUser = userService.getCurrentUser();
        return orderService.getUserOrderItemsByStatus(currentUser, List.of(
            OrderItemStatus.IN_DELIVERY,
            OrderItemStatus.IN_PROGRESS,
            OrderItemStatus.PAID
            ));
    }

    public List<OrderItemDTO> getUserOrderHistory() {
        UserEntity currentUser = userService.getCurrentUser();
        return orderService.getUserOrderItemsByStatus(currentUser, 
        List.of(
            OrderItemStatus.CANCELLED,
            OrderItemStatus.DELIVERED,
            OrderItemStatus.REFUNDED
        ));
    }

    public OrderDTO createOrder(@Valid PlaceOrderRequest orderCreateDTO) {
        UserEntity currentUser = userService.getCurrentUser();
        OrderDTO order = orderService.placeOrder(currentUser, orderCreateDTO);
        
        // Log order created event
        userEventService.logEvent(currentUser, UserEventType.ORDER_CREATED);
        
        return order;
    }

    public void cancelOrderItem(Long orderItemId) {
        UserEntity currentUser = userService.getCurrentUser();
        orderService.cancelOrderItem(currentUser, orderItemId);
        
        // Log order cancelled event
        userEventService.logEvent(currentUser, UserEventType.ORDER_CANCELLED);
    }

}
