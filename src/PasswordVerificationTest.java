public class PasswordVerificationTest {
    public static void main(String[] args) {
        String plaintextPassword = "known";
        String storedPasswordFromDB = "known";

        boolean isMatch = plaintextPassword.equals(storedPasswordFromDB);


        System.out.println("Does the password match? " + isMatch);
    }
}
