package medicalcentre;

public class Customer extends User {
    public Customer(String id, String name, String password, String gender, String email, String phone, int age) {
        super(id, name, password, gender, email, phone, age);
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public String toString() {
        return "ID: " + getId() + ", Name: " + getName() + ", Email: " + getEmail() + ", Phone: " + getPhone() +
               ", Gender: " + getGender() + ", Age: " + getAge();
    }
}
