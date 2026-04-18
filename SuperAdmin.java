public class SuperAdmin extends Admin {

    private int adminLevel;

    public SuperAdmin(String username, String email, String password) {
        super(username, email, password);
        this.adminLevel = 2;
    }

    public void setAdminPrivileges(Admin admin, String privileges) {
        System.out.println("Privileges set for: " + admin.getUsername());
    }

    public int getAdminLevel() {
        return adminLevel;
    }
}
