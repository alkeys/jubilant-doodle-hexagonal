package alex.probando.usuarios.infra.adapter.out.security;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import alex.probando.usuarios.application.port.out.PasswordEncoderPort;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String codificar(String contraSinCodificar) {
        return passwordEncoder.encode(contraSinCodificar);
    }

    @Override
    public boolean coincide(String contraSinCodificar, String contraCodificada) {
        return passwordEncoder.matches(contraSinCodificar, contraCodificada);
    }
}