import java.util.*;
import java.util.stream.Collectors;

public class LibraryManager {
    private Map<Integer, Book> bookMap;
    private Map<String, String> userMap;
    private User currentUser;
    private LibraryRepository repository = new LibraryRepository();
    private int bookCount = 0;

    public LibraryManager() {
        // DB에서 데이터 로드
        bookMap = repository.loadBooks();
        userMap = repository.loadUsers();

        // 최신 ID(bookCount) 설정
        if (bookMap != null && !bookMap.isEmpty()) {
            this.bookCount = Collections.max(bookMap.keySet());
        }
    }

    // [1] 로그인 기능
    public boolean login(String id, String pw) {
        if (id.equals("admin") && pw.equals("1111")) {
            currentUser = new User("admin", "1111", "ADMIN");
            return true;
        } else if (userMap.containsKey(id) && userMap.get(id).equals(pw)) {
            currentUser = new User(id, pw, "USER");
            return true;
        }
        return false;
    }

    // [2] 도서 등록 (DB Insert 포함)
    public void addBook(String title, String author) {
        bookCount++;
        Book newBook = new Book(bookCount, title, author, true, null);
        bookMap.put(bookCount, newBook);
        repository.addBook(newBook);
    }

    // [3] 도서 대출 (DB Update 포함)
    public boolean borrowBook(int id) {
        Book b = bookMap.get(id);
        if (b != null && b.isAvailable() && currentUser != null) {
            b.setAvailable(false);
            b.setBorrowerId(currentUser.getId());
            repository.updateBookStatus(b);
            return true;
        }
        return false;
    }

    // [4] 도서 반납 (DB Update 포함)
    public boolean returnBook(int id) {
        Book b = bookMap.get(id);
        if (b != null && !b.isAvailable()) {
            b.setAvailable(true);
            b.setBorrowerId(null);
            repository.updateBookStatus(b);
            return true;
        }
        return false;
    }

    // [5] 도서 검색 기능 (Main에서 호출)
    public List<Book> searchBooksByTitle(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>(bookMap.values());
        }
        return bookMap.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // [6] 도서 수정 (Main에서 호출)
    public void updateBook(Book b) {
        if (b != null) {
            repository.updateBookStatus(b);
        }
    }

    // [7] 도서 삭제 (Main에서 호출)
    public void removeBook(int id) {
        bookMap.remove(id);
        // 메모리에서만 지우는 것이 아니라 DB에서도 지우고 싶다면
        // repository에 delete 메서드를 추가하여 호출하면 됩니다.
    }

    // [8] 대출 현황 필터링
    public List<Book> getMyBorrowedBooks() {
        if (currentUser == null) return new ArrayList<>();
        return bookMap.values().stream()
                .filter(book -> !book.isAvailable() && currentUser.getId().equals(book.getBorrowerId()))
                .collect(Collectors.toList());
    }

    // Getter 메서드들
    public Book getBook(int id) { return bookMap.get(id); }
    public Map<Integer, Book> getAllBooks() { return bookMap; }
    public User getCurrentUser() { return currentUser; }
    public int getBookCount() { return this.bookCount; }

    // 메인의 종료 메뉴와 호환 (실시간 저장 중이므로 안내 메시지 용도)
    public void save() { }
}