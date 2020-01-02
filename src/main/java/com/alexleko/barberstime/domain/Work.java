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
@Table(name = "Work")
@Getter
@Setter
@ToString(exclude = "id")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Work implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "The Work Description is required")
    @Size(min = 3, max = 200, message = "The Work Description must be between 3 and 200 characters")
    private String description;

    @Column(nullable = false)
    @NotEmpty(message = "The Work Price is required")
    @Positive(message = "The Work Price must be greater than zero")
    private Double price;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "Works_Categories",
        joinColumns = @JoinColumn(name = "work_ID"),
        inverseJoinColumns = @JoinColumn(name = "category_ID"))
    private List<Category> categories = new ArrayList<>();


    public Work(Long id, String description, Double price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }

}
