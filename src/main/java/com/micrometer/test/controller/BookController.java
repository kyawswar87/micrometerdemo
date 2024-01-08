package com.micrometer.test.controller;

import com.micrometer.test.interceptor.MetricAspectAnnotation;
import com.micrometer.test.service.Book;
import com.micrometer.test.service.BookService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostConstruct
    public void init() {

    }

    @GetMapping("/")
    @MetricAspectAnnotation
    public List<Book> getAllBooks() {
        List<Book> books = bookService.getBookList();
        return books;
    }

    @GetMapping("/{id}")
    @MetricAspectAnnotation
    public Book getAllBooks(@PathVariable int id) {
        Book book = bookService.getBookById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        return book;
    }

    @PostMapping
    @MetricAspectAnnotation
    public void insertBook(@RequestBody Book book) {
        bookService.insertBook(book);
    }

    @DeleteMapping("/{id}")
    @MetricAspectAnnotation
    public void deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
    }
}
