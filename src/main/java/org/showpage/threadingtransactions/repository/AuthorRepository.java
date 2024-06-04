package org.showpage.threadingtransactions.repository;

import org.showpage.threadingtransactions.dbmodel.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Integer> {
    public List<Author> findAll();
}
