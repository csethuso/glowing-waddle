import org.mindrot.jbcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        String password = "kachi";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        System.out.println("Hash for '" + password + "': " + hash);
    }
}
