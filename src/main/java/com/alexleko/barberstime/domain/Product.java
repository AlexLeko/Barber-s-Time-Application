package com.alexleko.barberstime.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "The Product Description is required")
    @Size(min = 3, max = 200, message = "The Product Description must be between 3 and 200 characters")
    private String description;

    @NotEmpty(message = "The Product Price is required")
    @Positive(message = "The Product Price must be greater than zero")
    private Double price;

    public Product(Long id, String description, Double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }
}
