public class User {
    private String userid;
    private String password;
    private String role;

    public User(String userid, String password, String role) {
        this.userid = userid;
        this.password = password;
        this.role = role;
    }

    // manager.getCurrentUser().getId() 호출 시 발생하는 오류 해결을 위해 추가
    public String getId() { return userid; }
    public String getUserid() { return userid; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}