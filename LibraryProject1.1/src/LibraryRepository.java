import java.sql.*;
import java.util.*;

public class LibraryRepository {

    public Map<Integer, Book> loadBooks() {
        Map<Integer, Book> bookMap = new TreeMap<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DBconn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean available = rs.getBoolean("available");
                String borrowerId = rs.getString("borrower_id");
                bookMap.put(id, new Book(id, title, author, available, borrowerId));
            }
        } catch (SQLException e) {
            System.err.println("[오류] 도서 로드 실패: " + e.getMessage());
        }
        return bookMap;
    }

    public Map<String, String> loadUsers() {
        Map<String, String> userMap = new HashMap<>();
        String sql = "SELECT userid, password FROM users";
        try (Connection conn = DBconn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                userMap.put(rs.getString("userid"), rs.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println("[오류] 사용자 로드 실패: " + e.getMessage());
        }
        return userMap;
    }

    // [중요] 도서 추가 시 DB에 Insert
    public void addBook(Book book) {
        String sql = "INSERT INTO books (id, title, author, available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBconn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, book.getId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setBoolean(4, book.isAvailable());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[오류] 도서 추가 실패: " + e.getMessage());
        }
    }

    // [중요] 대출/반납 시 상태 업데이트
    public void updateBookStatus(Book book) {
        String sql = "UPDATE books SET available = ?, borrower_id = ? WHERE id = ?";
        try (Connection conn = DBconn.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, book.isAvailable());
            pstmt.setString(2, book.getBorrowerId());
            pstmt.setInt(3, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[오류] 도서 업데이트 실패: " + e.getMessage());
        }
    }
}