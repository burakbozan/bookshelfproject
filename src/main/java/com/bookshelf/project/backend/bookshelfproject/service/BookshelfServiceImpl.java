package com.bookshelf.project.backend.bookshelfproject.service;

import com.bookshelf.project.backend.bookshelfproject.model.Bookshelf;
import com.bookshelf.project.backend.bookshelfproject.repository.BookshelfRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class BookshelfServiceImpl implements BookshelfService{

    private final BookshelfRepo bookshelfRepo;

    @Override
    public ResponseEntity<List<Bookshelf>> getAllBooks(String firstName) {
        try {
            List<Bookshelf> bookshelfList = new ArrayList<>();

            if(null == firstName)
                bookshelfRepo.findAll().forEach(bookshelfList::add);
            else
                bookshelfRepo.findByFirstNameContaining(firstName).forEach(bookshelfList::add);

            if(bookshelfList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return  new ResponseEntity<>(bookshelfList, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Bookshelf> getBookById(Integer id) {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<Bookshelf> createBook(Bookshelf bookshelf) {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<Bookshelf> updateBook(Integer id, Bookshelf bookshelf) {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> deleteBook(Integer id) {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<HttpStatus> deleteAllBooks() {
        //TODO
        return null;
    }

    @Override
    public ResponseEntity<List<Bookshelf>> findByConfirmed() {
        //TODO
        return null;
    }
}
