package ewm.core.category.repository;

import ewm.core.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
