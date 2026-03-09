import java.util.*;

public class Main {
    private static LibraryManager manager = new LibraryManager();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        login();
        while (true) {
            printMenu();
            String input = sc.nextLine();
            if (input.isEmpty()) continue;
            int choice;
            try { choice = Integer.parseInt(input); }
            catch (NumberFormatException e) { System.out.println("숫자만 입력 가능합니다."); continue; }

            switch (choice) {
                case 1 -> {
                    if (manager.getCurrentUser().getRole().equals("ADMIN")) addBook();
                    else borrowBook();
                }
                case 2 -> {
                    if (manager.getCurrentUser().getRole().equals("ADMIN")) editOrDeleteBook();
                    else returnBook();
                }
                case 3 -> {
                    if (manager.getCurrentUser().getRole().equals("USER")) showLoanStatus();
                    else System.out.println("[알림] 관리자는 전체 목록(5번)을 확인해 주세요.");
                }
                case 5 -> printAllBooks();
                case 6 -> searchBook();
                case 0 -> { if (exitAndSave()) return; }
                default -> System.out.println("잘못된 메뉴 번호입니다.");
            }
        }
    }

    private static void login() {
        System.out.println("===========================================================");
        System.out.println(" [ 도서 관리 시스템 - LOGIN ]");
        System.out.println("===========================================================");
        System.out.print(" 아이디(ID): "); String id = sc.nextLine();
        System.out.print(" 비밀번호(PW): "); String pw = sc.nextLine();

        if (manager.login(id, pw)) {
            if (manager.getCurrentUser().getRole().equals("ADMIN"))
                System.out.println("\n[확인] 관리자 권한으로 로그인되었습니다.");
            else
                System.out.println("\n[확인] " + id + " 사용자님, 환영합니다.");
        } else {
            System.out.println("\n[오류] 로그인 정보가 틀렸습니다. 다시 시도하세요.");
            login();
        }
    }

    private static void printMenu() {
        System.out.println("\n===========================================================");
        if (manager.getCurrentUser().getRole().equals("ADMIN")) {
            System.out.println(" [ 관리자 전용 메뉴 ]");
            System.out.println("===========================================================");
            System.out.println(" 1. 도서 등록 (Add)");
            System.out.println(" 2. 도서 수정 및 삭제 (Edit/Delete)");
        } else {
            System.out.println(" [ 사용자 전용 메뉴 ]");
            System.out.println("===========================================================");
            System.out.println(" 1. 도서 대출 (Borrow)");
            System.out.println(" 2. 도서 반납 (Return)");
            System.out.println(" 3. 대출 현황 보기 (Status)");
        }
        System.out.println(" 5. 전체 도서 목록 (List)");
        System.out.println(" 6. 도서 검색 (Search)");
        System.out.println(" 0. 종료 (Exit)");
        System.out.println("-----------------------------------------------------------");
        System.out.print(" 명령 입력: ");
    }

    private static void addBook() {
        System.out.println("\n[도서 등록]");
        System.out.print("- 제목: "); String t = sc.nextLine();
        System.out.print("- 저자: "); String a = sc.nextLine();
        manager.addBook(t, a); // DB에 즉시 Insert 됨
        System.out.println("[결과] 등록 완료 (ID: " + manager.getBookCount() + ")");
    }

    private static void borrowBook() {
        System.out.print("- 대출할 도서 ID 입력: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Book b = manager.getBook(id);
            if (b == null) { System.out.println("없는 도서입니다."); return; }
            if (!b.isAvailable()) { System.out.println("[알림] 이미 대출 중인 도서입니다."); return; }

            System.out.printf("[확인] '%s' 도서를 대출하시겠습니까? (Y/N): ", b.getTitle());
            if (sc.nextLine().equalsIgnoreCase("Y")) {
                // [수정] manager 내부 로직을 통해 DB와 메모리를 동시 업데이트
                if (manager.borrowBook(id)) {
                    System.out.println("[결과] 대출이 완료되었습니다.");
                } else {
                    System.out.println("[오류] 대출 처리에 실패했습니다.");
                }
            }
        } catch (Exception e) { System.out.println("잘못된 입력입니다."); }
    }

    private static void returnBook() {
        System.out.print("- 반납할 도서 ID 입력: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Book b = manager.getBook(id);
            if (b == null) { System.out.println("없는 도서입니다."); return; }

            System.out.printf("[확인] '%s' 도서를 반납하시겠습니까? (Y/N): ", b.getTitle());
            if (sc.nextLine().equalsIgnoreCase("Y")) {
                // [수정] manager 내부 로직을 통해 DB와 메모리를 동시 업데이트
                if (manager.returnBook(id)) {
                    System.out.println("[결과] 반납이 완료되었습니다.");
                } else {
                    System.out.println("[오류] 반납 처리에 실패했습니다.");
                }
            }
        } catch (Exception e) { System.out.println("잘못된 입력입니다."); }
    }

    private static void showLoanStatus() {
        System.out.println("===========================================================");
        System.out.println(" [ 나의 현재 대출 현황 ]");
        System.out.printf(" %-5s | %-15s | %-12s \n", "ID", "제목", "대출자");
        System.out.println("-----------------------------------------------------------");

        List<Book> myBooks = manager.getMyBorrowedBooks();
        if (myBooks.isEmpty()) {
            System.out.println(" 대출 중인 도서가 없습니다.");
        } else {
            for (Book b : myBooks) {
                System.out.printf(" %-5d | %-15s | %-12s \n", b.getId(), b.getTitle(), b.getBorrowerId());
            }
        }
        System.out.println("===========================================================");
    }

    private static void printAllBooks() {
        System.out.println("===========================================================");
        System.out.println(" [ 전체 도서 목록 ]");
        System.out.printf(" %-5s | %-15s | %-12s | %-10s \n", "ID", "제목", "저자", "상태");
        System.out.println("-----------------------------------------------------------");
        manager.getAllBooks().forEach((id, b) -> {
            System.out.printf(" %-5d | %-15s | %-12s | %-10s \n", id, b.getTitle(), b.getAuthor(), b.isAvailable() ? "대출 가능" : "대출 중");
        });
        System.out.println("===========================================================");
    }

    private static void searchBook() {
        System.out.print("- 검색할 제목 키워드 입력: ");
        String k = sc.nextLine();
        List<Book> results = manager.searchBooksByTitle(k);
        System.out.println("-----------------------------------------------------------");
        System.out.printf(" %-5s | %-15s | %-12s | %-10s \n", "ID", "제목", "저자", "상태");
        System.out.println("-----------------------------------------------------------");
        if (results.isEmpty()) {
            System.out.println(" 검색 결과가 없습니다.");
        } else {
            for (Book b : results) {
                System.out.printf(" %-5d | %-15s | %-12s | %-10s \n", b.getId(), b.getTitle(), b.getAuthor(), b.isAvailable() ? "대출 가능" : "대출 중");
            }
        }
        System.out.println("-----------------------------------------------------------");
    }

    private static void editOrDeleteBook() {
        System.out.print("- 관리할 도서 ID 입력: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Book b = manager.getBook(id);
            if (b == null) { System.out.println("도서를 찾을 수 없습니다."); return; }

            System.out.print("1.제목수정 2.저자수정 3.삭제 0.취소: ");
            int choice = Integer.parseInt(sc.nextLine());

            if (choice == 1) {
                System.out.print("새 제목: ");
                b.setTitle(sc.nextLine());
                manager.updateBook(b); // DB 반영을 위한 메서드 호출
                System.out.println("수정되었습니다.");
            }
            else if (choice == 2) {
                System.out.print("새 저자: ");
                b.setAuthor(sc.nextLine());
                manager.updateBook(b); // DB 반영을 위한 메서드 호출
                System.out.println("수정되었습니다.");
            }
            else if (choice == 3) {
                manager.removeBook(id);
                System.out.println("삭제되었습니다.");
            }
        } catch (Exception e) { System.out.println("잘못된 입력입니다."); }
    }

    private static boolean exitAndSave() {
        System.out.println("-----------------------------------------------------------");
        System.out.print(" 프로그램을 종료하시겠습니까? (Y/N): ");
        if (sc.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("[완료] 시스템을 종료합니다.");
            return true;
        }
        return false;
    }
}