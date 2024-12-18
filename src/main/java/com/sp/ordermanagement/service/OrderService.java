package com.sp.ordermanagement.service;

import com.sp.ordermanagement.dto.CreateOrderRequestDTO;
import com.sp.ordermanagement.dto.OrderResponseDTO;
import com.sp.ordermanagement.dto.UpdateOrderStatusDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(CreateOrderRequestDTO cart);

    OrderResponseDTO updateOrderStatus(Long orderId, UpdateOrderStatusDTO statusDTO);

    OrderResponseDTO cancelOrder(Long orderId);

    List<OrderResponseDTO> getOrdersForUser(Long userId);

    OrderResponseDTO getOrderById(Long orderId);

    List<OrderResponseDTO> getAllOrders();
}
