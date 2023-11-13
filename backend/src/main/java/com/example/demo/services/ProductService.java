package com.example.demo.services;

import com.example.demo.entities.Product;
import com.example.demo.entities.Category;
import com.example.demo.entities.Color;
import com.example.demo.models.ProductDTO;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.OrderItemRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.ColorRepository;
import com.example.demo.repositories.SizeRepository;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            ColorRepository colorRepository, SizeRepository sizeRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.colorRepository = colorRepository;
        this.sizeRepository = sizeRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product get(final Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find product with id: " + id));
    }

    public Long create(final ProductDTO productDTO) {
        final Product product = mapToEntity(productDTO, new Product());
        return productRepository.save(product).getId();
    }
    
    public List<Product> search(final String query) {
        List<Product> products = productRepository.searchProducts(query);
        return products;
    }

    public Product update(Long id, final ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find product with id: " + id));
        product = mapToEntity(productDTO, product);
        productRepository.save(product);
        return product;
    }
    public void delete(final Long id) {
        productRepository.deleteById(id);
    }
    
    public Product mapToEntity(final ProductDTO productDTO, Product product) {
        productDTO.setId(product.getId());
        product = modelMapper.map(productDTO, Product.class);
        if (productDTO.getCategory() != null && (product.getCategory() == null || !product.getCategory().getId().equals(productDTO.getCategory()))) {
            final Category category = categoryRepository.findById(productDTO.getCategory())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "category not found"));
            product.setCategory(category);
        }
        if (productDTO.getColor() != null && (product.getColor() == null || !product.getColor().getId().equals(productDTO.getColor()))) {
            final Color color = colorRepository.findById(productDTO.getColor())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "color not found"));
            product.setColor(color);
        }
        if (productDTO.getListSizes() != null) {
            product.setListSizes(sizeRepository.findBySizeIdIn(productDTO.getListSizes()));
        }
        return product;
    }

}