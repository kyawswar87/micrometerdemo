package com.micrometer.test.controller;

import com.micrometer.test.service.Book;
import com.micrometer.test.service.BookService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter.Builder restfulMethodCounterBuilder;

    private Timer.Sample timer;
    private static final String HTTP_METHOD_COUNTER = "http_method_counter";

    private static final String HTTP_METHOD_TIMER = "http_method_timer";


    @PostConstruct
    public void init() {
        restfulMethodCounterBuilder = getCounterBuilder();

    }

    @GetMapping("/")
    public List<Book> getAllBooks() {
        timer = Timer.start(meterRegistry);
        restfulMethodCounterBuilder
                .tag("method", "GET")
                .tag("path", "/all")
                .register(meterRegistry).increment();
        List<Book> books = bookService.getBookList();

        timer.stop(Timer.builder(HTTP_METHOD_TIMER)
                .description("http method processing timer")
                .tags(List.of(
                        Tag.of("method", "GET"),
                        Tag.of("path", "/all")
                ))
                .register(meterRegistry));
        return books;
    }

    @GetMapping("/{id}")
    public Book getAllBooks(@PathVariable int id) {
        timer = Timer.start(meterRegistry);
        restfulMethodCounterBuilder
                .tag("method", "GET")
                .tag("path", "/")
                .tag("id", String.valueOf(id))
                .register(meterRegistry).increment();
        Book book = bookService.getBookById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        timer.stop(Timer.builder(HTTP_METHOD_TIMER)
                .description("http method processing timer")
                .tags(List.of(
                        Tag.of("method", "GET"),
                        Tag.of("path", "/"),
                        Tag.of("id", String.valueOf(id))
                ))
                .register(meterRegistry));
        return book;
    }

    @PostMapping
    public void insertBook(@RequestBody Book book) {
        timer = Timer.start(meterRegistry);
        restfulMethodCounterBuilder
                .tag("method", "POST")
                .tag("path", "/")
                .register(meterRegistry).increment();
        bookService.insertBook(book);
        timer.stop(Timer.builder(HTTP_METHOD_TIMER)
                .description("http method processing timer")
                .tags(List.of(
                        Tag.of("method", "POST"),
                        Tag.of("path", "/")
                ))
                .register(meterRegistry));
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable int id) {
        timer = Timer.start(meterRegistry);
        restfulMethodCounterBuilder
                .tag("method", "DELETE")
                .tag("path", "/")
                .tag("id", String.valueOf(id))
                .register(meterRegistry).increment();
        bookService.deleteBook(id);

        timer.stop(Timer.builder(HTTP_METHOD_TIMER)
                .description("http method processing timer")
                .tags(List.of(
                        Tag.of("method", "DELETE"),
                        Tag.of("path", "/"),
                        Tag.of("id", String.valueOf(id))
                ))
                .register(meterRegistry));
    }

    private Counter.Builder getCounterBuilder() {
        return Counter
                .builder(HTTP_METHOD_COUNTER)
                .description("indicates count of http methods");
    }

    private Timer.Builder getTimerBuilder() {
        return Timer.builder("service_books_find")
                .description("books searching timer");
    }
}
