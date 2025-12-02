package medicalcentre;

public class Staff extends User {
    public Staff(String id, String name, String password, String gender, String email, String phone, int age) {
        super(id, name, password, gender, email, phone, age);
    }

    @Override
    public String getRole() {
        return "Staff";
    }
}
