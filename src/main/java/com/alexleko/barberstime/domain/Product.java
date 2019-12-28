package com.alexleko.barberstime.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "The Product Description is required")
    @Size(min = 3, max = 200, message = "The Product Description must be between 3 and 200 characters")
    private String description;

    @Column(nullable = false)
    @NotEmpty(message = "The Product Price is required")
    @Positive(message = "The Product Price must be greater than zero")
    private Double price;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "Products_Categories",
        joinColumns = @JoinColumn(name = "product_ID"),
        inverseJoinColumns = @JoinColumn(name = "category_ID"))
    private List<Category> categories = new ArrayList<>();


    public Product(Long id, String description, Double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }

}
