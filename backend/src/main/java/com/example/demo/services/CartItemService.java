package com.example.demo.services;
import com.example.demo.repositories.CartItemRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.SizeRepository;
import com.example.demo.security.AuthService;
import com.example.demo.entities.CartItem;
import com.example.demo.entities.Size;
import com.example.demo.models.CartItemDTO;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SizeService sizeService;

    public CartItemService(final CartItemRepository cartItemRepository, final UserRepository userRepository, final ProductRepository productRepository, final SizeRepository sizeRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> findAllByUserId() {
        return cartItemRepository.findAllByUserId(authService.getCurrentUserId());
    }

    public void deleteAllByUserId() {
        cartItemRepository.deleteAllByUserId(authService.getCurrentUserId());
    }

    public CartItem get(final Long id) {
        return cartItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Can't find cartItem with id: " + id));
    }

    @Transactional
    public CartItem create(CartItemDTO cartItemDTO) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(authService.getCurrentUserId(), cartItemDTO.getProductId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setQuantity(cartItemDTO.getQuantity());
            cartItem.setUser(authService.getCurrentUser());
            cartItem.setProduct(productService.get(cartItemDTO.getProductId()));
            cartItem.setSize(sizeService.get(cartItemDTO.getSize()));
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
        cartItemRepository.save(cartItem);
        return cartItem;
    }

    public CartItem update(Long id, CartItemDTO cartItemDTO) {
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find cartItem with id: " + id + " to update", null));
        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartItemRepository.save(cartItem);
        return cartItem;
    }

    public void delete(Long id) {
        cartItemRepository.deleteById(id);
    }

}