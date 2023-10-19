package com.example.demo.entities;

import com.example.demo.entities.Size;
import com.example.demo.entities.Color;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "\"description\"")
    private String description;

    @ManyToMany
    @JoinTable(
        name = "product_size",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "size_id")
    )
    private Set<Size> sizes;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;

    @Column(nullable = false)
    private String materials;

    @Column
    private String image;

    @Column(nullable = false)
    private String instruction;
    
    @Column(nullable = false)
    private Long price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
