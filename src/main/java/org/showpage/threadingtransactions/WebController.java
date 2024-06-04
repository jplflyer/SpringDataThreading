package org.showpage.threadingtransactions;

import lombok.extern.slf4j.Slf4j;
import org.showpage.threadingtransactions.dbmodel.Author;
import org.showpage.threadingtransactions.dbmodel.Book;
import org.showpage.threadingtransactions.repository.AuthorRepository;
import org.showpage.threadingtransactions.repository.BookRepository;
import org.showpage.threadingtransactions.service.BookInfoService;
import org.showpage.threadingtransactions.uimodel.BookInfo;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebController {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final BookInfoService bookInfoService;

    /**
     *
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!\n";
    }

    /**
     *
     */
    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAuthors() {
        List<Author> authors = authorRepository.findAll();
        return ResponseEntity.ok(authors);
    }

    /**
     *
     */
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getBooks() {
        List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/request")
    public ResponseEntity<String> makeRequest(
            @RequestParam(required = false, defaultValue = "false") boolean pass,
            @RequestParam(name = "no_spawn", required = false, defaultValue = "false") boolean noSpawn
    ) {
        log.info("makeRequest({}, {})", pass, noSpawn);
        BookInfoService.Mode mode = noSpawn
                ? BookInfoService.Mode.NoSpawn
                : ( pass ? BookInfoService.Mode.SpawnWillPass : BookInfoService.Mode.SpawnWillFail);
        int requestId = bookInfoService.makeRequest(mode);
        return ResponseEntity.ok(String.format("%d\n", requestId));
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<BookInfo> getInfo( @PathVariable("id") int id ) {
        BookInfo data = bookInfoService.getRequest(id);
        return ResponseEntity.ok(data);
    }
}
