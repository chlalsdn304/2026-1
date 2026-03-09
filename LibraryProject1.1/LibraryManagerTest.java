import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class LibraryManagerTest {
    private LibraryManager manager;

    @BeforeEach
    void setUp() {
        manager = new LibraryManager();
        manager.login("admin", "1111"); // 테스트 환경 보장
    }

    @Test
    void testAddBook() {
        int before = manager.getBookCount();
        manager.addBook("최종 테스트 도서", "저자");

        int after = manager.getBookCount();
        assertTrue(after > before, "ID가 증가하지 않았습니다.");

        Book book = manager.getBook(after);
        assertNotNull(book);
        assertEquals("최종 테스트 도서", book.getTitle());
    }

    @Test
    void testBorrowAndReturn() {
        manager.addBook("대출용 도서", "작가");
        int id = manager.getBookCount();

        assertTrue(manager.borrowBook(id), "대출 실패");
        assertFalse(manager.getBook(id).isAvailable());

        assertTrue(manager.returnBook(id), "반납 실패");
        assertTrue(manager.getBook(id).isAvailable());
    }
}