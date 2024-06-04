package org.showpage.threadingtransactions.service;

import org.showpage.threadingtransactions.dbmodel.Book;
import org.showpage.threadingtransactions.repository.BookRepository;
import org.showpage.threadingtransactions.uimodel.BookInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SubService {

    @PersistenceContext
    private final EntityManager em;

    private final BookRepository bookRepository;

    /**
     * Worker for the above.
     */
    @Transactional
    public void populate(BookInfo info) {
        List<Book> allBooks = bookRepository.findAll();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        StringBuilder sb = new StringBuilder();

        try {
            for (Book book : allBooks) {
                sb.append(
                        String.format("Name: %s Author: %s\n",
                                book.getName(),
                                book.getAuthor().getName())
                );
            }
        }
        catch (Exception e) {
            log.error("Exception", e);
        }

        info.setData(sb.toString());
        info.setDone(true);
    }

}
