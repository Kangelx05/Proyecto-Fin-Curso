package api.utils;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Configuration
public class PasswordEncryption {
    // Hashear una contraseña
    public static String hashPassword(String password) {
        // Generar el hash con un costo por defecto (10)
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Verificar una contraseña
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

}
