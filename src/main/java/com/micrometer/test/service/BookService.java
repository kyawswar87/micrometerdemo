package com.micrometer.test.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

@Service
public class BookService {

    private List<Book> bookList = new ArrayList<>();

    public List<Book> getBookList() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(200, 400));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return bookList;
    }

    public Optional<Book> getBookById(int id) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(200, 400));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return bookList.stream().filter(book -> book.getId() == id).findFirst();
    }

    public void insertBook(Book book) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(200, 400));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        bookList.add(book);
    }

    public void deleteBook(int id) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(200, 400));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        bookList.removeIf(book -> book.getId() == id);
    }
}
