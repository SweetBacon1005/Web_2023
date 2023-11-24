package com.example.demo.services;

import com.example.demo.repositories.OrderItemRepository;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.OrderStatusRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.AuthService;
import com.example.demo.entities.Order;
import com.example.demo.models.OrderDTO;
import com.example.demo.entities.OrderItem;
import com.example.demo.entities.OrderStatus;
import com.example.demo.entities.User;
import com.example.demo.entities.CartItem;
import com.example.demo.services.SizeService;


import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.springframework.web.server.ResponseStatusException;


@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderStatusRepository orderStatusRepository;
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.userRepository = userRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAllByUserId(authService.getCurrentUserId());
    }

    public Order get(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    
    public Long create(final OrderDTO orderDTO) {
        final Order order = mapToEntity(orderDTO, new Order());
        return orderRepository.save(order).getId();
    }

    @Transactional
    public Long createOrderFromCart(OrderDTO orderDTO) {
        Order order = new Order();
        order.setPhone(authService.getCurrentUser().getPhone());
        order.setUser(authService.getCurrentUser());
        Long totalPrice = 0L;
        order.setTotal(totalPrice);
        order.setFirstName(orderDTO.getFirstName());
        order.setLastName(orderDTO.getLastName());
        order.setAddress(orderDTO.getAddress());
        order.setStatus(orderStatusRepository.findById(orderDTO.getStatus()).orElseThrow(() -> new RuntimeException("Can't find orderStatus with id: " + orderDTO.getStatus() + " to update")));
        Order updateOrder = orderRepository.save(order);
        List<OrderItem> orderItems = new ArrayList<>();
        List<CartItem> cartItems = cartItemService.findAllByUserId();
        for (CartItem c : cartItems) {
            OrderItem o = new OrderItem();
            o.setName(c.getProduct().getName());
            o.setPrice(c.getProduct().getPrice());
            o.setQuantity(c.getQuantity());
            o.setOrder(updateOrder);
            o.setProduct(c.getProduct());
            o.setSize(c.getSize());
            OrderItem orderItemSave = orderItemRepository.save(o);
            orderItems.add(orderItemSave);
            totalPrice += o.getPrice() * o.getQuantity();
        }
        updateOrder.setTotal(totalPrice);
        Set<OrderItem> os = new HashSet<>(orderItems);
        updateOrder.setOrderItems(os);
        cartItemService.deleteAllByUserId();
        return updateOrder.getId();
    }

    public void update(final Long id, final OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        order = mapToEntity(orderDTO, order);
        orderRepository.save(order);
    }

    public void delete(final Long id) {
        orderRepository.deleteById(id);
    }


    private Order mapToEntity(final OrderDTO orderDTO, Order order) {
        order.setAddress(orderDTO.getAddress());
        order.setFirstName(orderDTO.getFirstName());
        order.setLastName(orderDTO.getLastName());
        order.setStatus(orderStatusRepository.findById(orderDTO.getStatus()).orElseThrow(() -> new RuntimeException("Can't find orderStatus with id: " + orderDTO.getStatus() + " to update")));
        order.setUser(userRepository.findById(orderDTO.getUser()).orElseThrow(() -> new RuntimeException("Can't find user with id: " + orderDTO.getUser() + " to update")));
        return order;
    }

}
    