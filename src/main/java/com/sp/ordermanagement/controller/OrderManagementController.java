package com.sp.ordermanagement.controller;

import com.sp.ordermanagement.dto.CreateOrderRequestDTO;
import com.sp.ordermanagement.dto.OrderResponseDTO;
import com.sp.ordermanagement.dto.UpdateOrderStatusDTO;
import com.sp.ordermanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderManagementController {

    @Autowired
    private OrderService orderService;

    // 1. Place an Order (from cart)
    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponseDTO> createOrder(@PathVariable Long userId, @RequestBody CreateOrderRequestDTO cart) {
        cart.setUserId(userId); // Set the userId in the request DTO
        OrderResponseDTO order = orderService.createOrder(cart);
        return ResponseEntity.ok(order);
    }

    // 2. Update Order Status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody UpdateOrderStatusDTO statusDTO) {
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(orderId, statusDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    // 3. Cancel Order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long orderId) {
        OrderResponseDTO cancelledOrder = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(cancelledOrder);
    }

    // 4. Get Orders for a User
    @GetMapping("/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersForUser(@PathVariable Long userId) {
        List<OrderResponseDTO> orders = orderService.getOrdersForUser(userId);
        return ResponseEntity.ok(orders);
    }

    // 5. Get Order Details by Order ID (Get details of a single order)
    @GetMapping("/{orderId}/details")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        OrderResponseDTO orderDetails = orderService.getOrderById(orderId);
        return ResponseEntity.ok(orderDetails);
    }

    // 6. Get All Orders (Admin functionality to get all orders)
    @GetMapping("/")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> allOrders = orderService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }
}
