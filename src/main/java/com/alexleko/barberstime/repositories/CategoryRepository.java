package com.alexleko.barberstime.repositories;

import com.alexleko.barberstime.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByDescription(String description);

}
