package org.showpage.threadingtransactions.service;

import org.showpage.threadingtransactions.repository.BookRepository;
import org.showpage.threadingtransactions.uimodel.BookInfo;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.showpage.threadingtransactions.dbmodel.Author;
import org.showpage.threadingtransactions.dbmodel.Book;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookInfoService {
    public enum Mode {
        SpawnWillFail,
        SpawnWillPass,
        NoSpawn
    }

    private final BookRepository bookRepository;
    private final EntityManager entityManager;
    private SessionFactory sessionFactory;

    private static int requestId = 0;
    private static List<BookInfo> bookInfoList = new ArrayList<>();

    /**
     * Spawn the request.
     */
    public int makeRequest(Mode mode) {
        BookInfo info = BookInfo
                .builder()
                .requestId(++requestId)
                .build();
        bookInfoList.add(info);

        log.info("Mode: {}", mode);
        switch (mode) {
            case SpawnWillFail: spawnWillFail(info); break;
            case SpawnWillPass: spawnWillPass(info); break;
            case NoSpawn: runNoSpawn(info); break;
        }
        return info.getRequestId();
    }

    public int makeGoodRequest() {
        BookInfo info = BookInfo
                .builder()
                .requestId(++requestId)
                .build();
        bookInfoList.add(info);
        spawnWillFail(info);
        return info.getRequestId();
    }

    public BookInfo getRequest(int id) {
        for (BookInfo bookInfo : bookInfoList) {
            if (bookInfo.getRequestId() == id) {
                return bookInfo;
            }
        }
        return null;
    }

    private void runNoSpawn(BookInfo info) {
        SubService subService = new SubService(entityManager, bookRepository);

        try {
            subService.populate(info);
        }
        catch (Exception e) {
            log.error("Exception", e);
        }
    }

    /**
     * This version will fail with a lazy initialization exception.
     */
    private void spawnWillFail(BookInfo info) {
        List<Book> allBooks = bookRepository.findAll();

        new Thread(() -> {
            log.info("Bad Thread start");
            populate(info, allBooks);
            log.info("Bad Thread done");
        }).start();
    }

    /**
     * This version will pass once I get it to work.
     */
    private void spawnWillPass(BookInfo info) {
        EntityManager em = entityManager
                .getEntityManagerFactory()
                .createEntityManager();

        SessionFactory sessionFactory = em.getEntityManagerFactory().unwrap(SessionFactory.class);

        SubService subService = new SubService(em, bookRepository);

        new Thread(() -> {
            log.info("Good Thread start");

            try {
                Session session = sessionFactory.openSession();
                em.getTransaction().begin();
                subService.populate(info);
                em.getTransaction() .commit();
            }
            catch (Exception e) {
                log.error("Exception", e);
                em.getTransaction().rollback();
            }
            finally {
                em.close();
                sessionFactory.close();
            }

            log.info("Good Thread done");
        }).start();
    }

    /**
     * Worker for the above.
     */
    private void populate(BookInfo info, List<Book> allBooks) {
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
