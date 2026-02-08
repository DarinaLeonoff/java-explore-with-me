package ru.practicum.ewm.category.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository repo;

    @Test
    void saveTest() {
        Category cat = Category.builder().name("new cat").build();

        Category res = repo.save(cat);

        assertNotNull(res);
        assertEquals(cat.getName(), res.getName());
    }

    @Test
    void updateTest() {
        Category cat = repo.save(Category.builder().name("new cat").build());
        Category updated = repo.save(Category.builder().id(cat.getId()).name("updated").build());

        Category check = repo.findById(cat.getId()).get();
        assertEquals(check.getName(), updated.getName());
    }

    @Test
    void removeTest() {
        Category cat = repo.save(Category.builder().name("new cat").build());
        int id = cat.getId();

        repo.deleteById(id);
        Category check = repo.findById(id).orElseGet(() -> Category.builder().name("Not found").build());
        assertEquals("Not found", check.getName());
    }

    @Test
    void findCategoriesTest() {
        Pageable pageable = PageRequest.of(0 / 10, 10, Sort.by("id").ascending());
        Category cat1 = repo.save(Category.builder().name("cat 1").build());
        Category cat2 = repo.save(Category.builder().name("cat 2").build());
        Category cat3 = repo.save(Category.builder().name("cat 3").build());

        List<Category> res = repo.findCategories(pageable);

        assertEquals(3, res.size());
        assertEquals(cat1.getId(), res.get(0).getId());
        assertEquals(cat2.getId(), res.get(1).getId());
        assertEquals(cat3.getId(), res.get(2).getId());

    }

}
