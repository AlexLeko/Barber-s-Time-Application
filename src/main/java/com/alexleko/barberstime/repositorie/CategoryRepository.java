package com.alexleko.barberstime.repositorie;

import com.alexleko.barberstime.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
