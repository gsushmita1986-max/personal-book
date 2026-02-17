package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import com.example.demo.google.GoogleVolume;

import jakarta.transaction.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;
    
    @Autowired
    public BookService(BookRepository bookRepository, GoogleBookService googleBookService) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
    }

    @Transactional
    public Book addBookFromGoogle(String googleId) {

        GoogleVolume googleVolume = googleBookService.getBookById(googleId);

        if (googleVolume == null || googleVolume.volumeInfo() == null) {
            throw new IllegalArgumentException("Invalid Google Book ID");
        }

        GoogleBook.VolumeInfo info = googleVolume.volumeInfo();

        String firstAuthor = null;
        if (info.authors() != null && !info.authors().isEmpty()) {
            firstAuthor = info.authors().get(0);
        }

        Book book = new Book(
                googleVolume.id(),
                info.title(),
                firstAuthor,
                info.pageCount()
        );

        return bookRepository.save(book);
    }


    @Transactional
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
