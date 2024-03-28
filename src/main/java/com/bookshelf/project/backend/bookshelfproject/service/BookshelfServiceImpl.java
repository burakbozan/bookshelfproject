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
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class BookshelfServiceImpl implements BookshelfService{

    private final BookshelfRepo bookshelfRepo;

    @Override
    public ResponseEntity<List<Bookshelf>> getAllBooks(String firstName, String lastName) {
        try {
            List<Bookshelf> bookshelfList = new ArrayList<>();

            if(null == firstName)
                bookshelfRepo.findAll().forEach(bookshelfList::add);
            else if (null != firstName && null == lastName)
                bookshelfRepo.findByFirstNameContaining(firstName).forEach(bookshelfList::add);
            else
                bookshelfRepo.findByLastNameContaining(lastName).forEach(bookshelfList::add);

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
        Optional<Bookshelf> bookshelfOptional = bookshelfRepo.findById(id);

        if(bookshelfOptional.isPresent()){
            return new ResponseEntity<>(bookshelfOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Bookshelf> createBook(Bookshelf bookshelf) {
        try {
            Bookshelf books = bookshelfRepo.save(new Bookshelf(bookshelf.getFirstName(), bookshelf.getLastName(), false));
            return new ResponseEntity<>(books, HttpStatus.CREATED);
        }catch (Exception e){
            return  new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Bookshelf> updateBook(Integer id, Bookshelf bookshelf) {
        Optional<Bookshelf> bookshelfOptional = bookshelfRepo.findById(id);

        if(bookshelfOptional.isPresent()){
            Bookshelf bookshelfObj =bookshelfOptional.get();
            bookshelfObj.setFirstName(bookshelf.getFirstName());
            bookshelfObj.setLastName(bookshelf.getLastName());
            bookshelfObj.setConfirmed(bookshelf.isConfirmed());

            return new ResponseEntity<>(bookshelfRepo.save(bookshelfObj), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> deleteBook(Integer id) {
        try {
            bookshelfRepo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> deleteAllBooks() {
        try {
            bookshelfRepo.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<Bookshelf>> findByConfirmed() {
        /*
        TODO
         */
        return null;
    }
}
