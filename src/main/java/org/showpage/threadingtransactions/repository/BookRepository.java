package org.showpage.threadingtransactions.repository;

import org.showpage.threadingtransactions.dbmodel.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    public List<Book> findAll();
}
