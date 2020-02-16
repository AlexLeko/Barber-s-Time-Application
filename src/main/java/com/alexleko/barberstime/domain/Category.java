package com.alexleko.barberstime.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@ToString(exclude = "id")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String CATEGORY_DESCRIPTION_REQUIRED = "The description is required";
    private static final String CATEGORY_LENGTH_DESCRIPTION = "The Category Description must be between 3 and 80 characters";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = CATEGORY_DESCRIPTION_REQUIRED)
    @Column(nullable = false)
    @Size(min = 3, max = 80, message = CATEGORY_LENGTH_DESCRIPTION)
    private String description;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    private List<Work> works = new ArrayList<>();

    public Category(Long id, String description) {
        this.id = id;
        this.description = description;
    }
}
