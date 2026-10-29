package alex.probando.auth.application.port.in;

import org.springframework.security.core.userdetails.UserDetails;

public interface LoginUseCase {
    UserDetails authenticate(String username, String password);
}
