package com.bookshelf.project.backend.bookshelfproject.service;

import com.bookshelf.project.backend.bookshelfproject.model.Bookshelf;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookshelfService {

    ResponseEntity<List<Bookshelf>> getAllBooks(String firstName, String lastName);

    ResponseEntity<Bookshelf> getBookById(Integer id);

    ResponseEntity<Bookshelf> createBook(Bookshelf bookshelf);

    ResponseEntity<Bookshelf> updateBook(Integer id, Bookshelf bookshelf);

    ResponseEntity<HttpStatus> deleteBook(Integer id);

    ResponseEntity<HttpStatus> deleteAllBooks();

    ResponseEntity<List<Bookshelf>> findByConfirmed();
}
