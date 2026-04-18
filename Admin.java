public class Admin extends User {

    private String privileges;

    public Admin(String username, String email, String password) {
        super(username, email, password, "ADMIN");
        this.privileges = "FULL";
    }

    public void approveUser(String email) {
        UserStore.unblockUser(email);
    }

    public void blockUser(String email) {
        UserStore.blockUser(email);
    }

    public String getPrivileges() {
        return privileges;
    }
}
