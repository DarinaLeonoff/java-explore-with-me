package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        select u from User u
        where (:ids is null or u.id in :ids)
        order by u.id
        """)
    List<User> findUsers(
            @Param("ids") List<Long> ids,
            Pageable pageable
    );
}
