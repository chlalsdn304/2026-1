import org.sikuli.script.*;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Random;

public class MacroMain {
    public static void main(String[] args) {
        String buttonPath = "C:\\Users\\choim\\OneDrive\\Desktop\\스크린샷 2026-02-11 235137.png";
        Screen screen = new Screen();
        Random random = new Random();

        try {
            Robot robot = new Robot();
            System.out.println("[시스템] 최적화된 추적 매크로를 시작합니다.");

            while (true) {
                // 1. 새로고침 (F5) - 브라우저가 활성화되어 있어야 함
                robot.keyPress(KeyEvent.VK_F5);
                robot.keyRelease(KeyEvent.VK_F5);
                System.out.println("\n[1/3] 화면 새로고침 완료");

                // 2. 랜덤 대기 (3.0초 ~ 3.9초 사이) - 탐색 방지
                int randomDelay = 3000 + random.nextInt(1000);
                System.out.printf("[2/3] %.2f초 동안 불규칙 대기 중...\n", randomDelay / 1000.0);
                Thread.sleep(randomDelay);

                // 3. 실시간 위치 추적 및 정밀 클릭
                System.out.println("[3/3] 버튼 위치 추적 중...");

                // 유사도를 0.6으로 낮춰 인식률을 높이고, 최대 2초간 대기
                Match target = screen.exists(new Pattern(buttonPath).similar(0.6f), 2.0);

                if (target != null) {
                    System.out.println(" >> 버튼 발견! 정밀 클릭 시퀀스 가동");

                    // [추가된 로직 A] 마우스를 해당 위치로 먼저 이동 (자연스럽게)
                    screen.mouseMove(target);
                    Thread.sleep(150); // 이동 후 0.15초 쉼

                    // [추가된 로직 B] 클릭 좌표 미세 랜덤화 (-3 ~ +3 픽셀)
                    // 매번 같은 점을 누르면 매크로로 차단될 수 있음
                    int rx = random.nextInt(7) - 3;
                    int ry = random.nextInt(7) - 3;
                    Location randomPoint = target.getTarget().offset(rx, ry);

                    // 최종 클릭
                    screen.click(randomPoint);
                    System.out.println(" >> [성공] 위치 추적 및 랜덤 좌표 클릭 완료");

                    // 성공 후에는 페이지 전환을 위해 조금 더 대기
                    Thread.sleep(2000);
                } else {
                    System.out.println(" >> [실패] 버튼을 찾지 못함. 다음 회차 진행");
                }
            }
        } catch (Exception e) {
            System.err.println("[오류] 실행 중단: " + e.getMessage());
            e.printStackTrace();
        }
    }
}