package com.sp.ordermanagement.service.impl;

import com.sp.ordermanagement.dto.CreateOrderRequestDTO;
import com.sp.ordermanagement.dto.OrderResponseDTO;
import com.sp.ordermanagement.dto.UpdateOrderStatusDTO;
import com.sp.ordermanagement.dto.CartItemDTO;
import com.sp.ordermanagement.enitity.Book;
import com.sp.ordermanagement.enitity.Order;
import com.sp.ordermanagement.enitity.OrderItem;
import com.sp.ordermanagement.enitity.User;
import com.sp.ordermanagement.kafka.KafkaProducer;
import com.sp.ordermanagement.repository.BookRepository;
import com.sp.ordermanagement.repository.OrderItemRepository;
import com.sp.ordermanagement.repository.OrderRepository;
import com.sp.ordermanagement.repository.UserRepository;
import com.sp.ordermanagement.service.OrderService;
import com.sp.ordermanagement.utils.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaProducer kafkaProducer;


    @Override
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        // Create the order
        Order order = new Order();
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setId(request.getUserId());
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING.name());
        order.setTotalAmount(calculateTotalAmount(request.getCartItems()));
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));

        // Add items from the cart to the order
        for (CreateOrderRequestDTO.CartItemDTO cartItem : request.getCartItems()) {
            OrderItem orderItem = new OrderItem();
           Book book = bookRepository.findById(cartItem.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            book.setId(cartItem.getBookId());
            orderItem.setBook(book);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());
            order.getOrderItems().add(orderItem);
        }

        // Save the order to the database
        order = orderRepository.save(order);

        //call kafka producer
        kafkaProducer.sendMessage("order_events","Order placed with ID: " + order.getOrderId());

        // Convert to DTO for response
        return convertToOrderResponseDTO(order);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, UpdateOrderStatusDTO statusDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus(statusDTO.getStatus());
        order = orderRepository.save(order);

        // Convert to DTO for response
        return convertToOrderResponseDTO(order);
    }

    @Override
    public OrderResponseDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setOrderStatus("Cancelled");
        order = orderRepository.save(order);

        // Convert to DTO for response
        return convertToOrderResponseDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getOrdersForUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(convertToOrderResponseDTO(order));
        }
        return responseList;
    }
    @Override
    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToOrderResponseDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderResponseDTO> responseList = new ArrayList<>();
        for (Order order : orders) {
            responseList.add(convertToOrderResponseDTO(order));
        }
        return responseList;
    }

    // Convert Order entity to OrderResponseDTO
    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setOrderId(order.getOrderId());
        responseDTO.setUserId(order.getUser().getId());
        responseDTO.setOrderStatus(order.getOrderStatus());
        responseDTO.setTotalAmount(order.getTotalAmount());
        responseDTO.setOrderDate(order.getOrderDate());

        List<OrderResponseDTO.OrderItemDTO> orderItems = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            OrderResponseDTO.OrderItemDTO itemDTO = new OrderResponseDTO.OrderItemDTO();
            itemDTO.setOrderItemId(item.getOrderItemId());
            itemDTO.setBookId(item.getBook().getId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            itemDTO.setSubtotal(item.getSubtotal());
            orderItems.add(itemDTO);
        }
        responseDTO.setOrderItems(orderItems);

        return responseDTO;
    }
    // Helper method to calculate total amount
    private Double calculateTotalAmount(List<CreateOrderRequestDTO.CartItemDTO> cartItems) {
        double total = 0;
        for (CreateOrderRequestDTO.CartItemDTO item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }


}
