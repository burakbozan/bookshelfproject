package com.bookshelf.project.backend.bookshelfproject.controller;

import com.bookshelf.project.backend.bookshelfproject.model.Bookshelf;
import com.bookshelf.project.backend.bookshelfproject.service.BookshelfService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class BookshelfController {

    private BookshelfService bookshelfService;

    public BookshelfController(BookshelfService bookshelfService){
        this.bookshelfService = bookshelfService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<Bookshelf>> getAllBooks(@RequestParam(required = false) String firstName){
        return bookshelfService.getAllBooks(firstName);
    }
}
