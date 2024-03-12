package com.bookshelf.project.backend.bookshelfproject.repository;

import com.bookshelf.project.backend.bookshelfproject.model.Bookshelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookshelfRepo extends JpaRepository<Bookshelf, Integer> {

    List<Bookshelf> findByConfirmed(boolean confirmed);

    List<Bookshelf> findByFirstNameContaining(String firstName);

    List<Bookshelf> findByLastNameContaining(String lastName);
}
