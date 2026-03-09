import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconn {
    // MariaDB 연결 설정 (현재 PC 설정값)
    private static final String URL = "jdbc:mariadb://localhost:3306/library";
    private static final String USER = "cjulib";
    private static final String PASSWORD = "security";

    /**
     * 이 메서드가 있어야 LibraryRepository의 빨간 줄이 사라집니다.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // MariaDB 드라이버 로드
            Class.forName("org.mariadb.jdbc.Driver");
            // 연결 수행
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[시스템] MariaDB 연결 성공!");
        } catch (ClassNotFoundException e) {
            System.err.println("[오류] 드라이버를 찾을 수 없습니다.");
        } catch (SQLException e) {
            System.err.println("[오류] DB 연결 실패: " + e.getMessage());
        }
        return conn;
    }

    // 테스트용 실행 메서드
    public static void main(String[] args) {
        getConnection();
    }
}