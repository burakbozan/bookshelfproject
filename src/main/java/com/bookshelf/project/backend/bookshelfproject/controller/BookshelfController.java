package com.bookshelf.project.backend.bookshelfproject.controller;

import com.bookshelf.project.backend.bookshelfproject.model.Bookshelf;
import com.bookshelf.project.backend.bookshelfproject.service.BookshelfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class BookshelfController {

    private final BookshelfService bookshelfService;

    public BookshelfController(BookshelfService bookshelfService) {
        this.bookshelfService = bookshelfService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<Bookshelf>> getAllBooks(@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
        return bookshelfService.getAllBooks(firstName, lastName);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Bookshelf> getBookById(@PathVariable("id") Integer id) {
        return bookshelfService.getBookById(id);
    }

    @PostMapping("/bookshelf")
    public ResponseEntity<Bookshelf> createBook(@RequestBody Bookshelf bookshelf) {
        return bookshelfService.createBook(bookshelf);
    }

    @PutMapping("/bookshelf/{id}")
    public ResponseEntity<Bookshelf> updateBook(@PathVariable("id") Integer id, @RequestBody Bookshelf bookshelf) {
        return bookshelfService.updateBook(id, bookshelf);
    }

    @DeleteMapping("/bookshelf/{id}")
    public ResponseEntity<HttpStatus> deleteBook(@PathVariable("id") Integer id) {
        return bookshelfService.deleteBook(id);
    }

    @DeleteMapping("/bookshelf")
    public ResponseEntity<HttpStatus> deleteAllBooks() {
        return bookshelfService.deleteAllBooks();
    }

}
