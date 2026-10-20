package alex.probando.usuarios.application.port.out;

public interface PasswordEncoderPort {
    String codificar(String contraSinCodificar);
    boolean coincide(String contraSinCodificar, String contraCodificada);
}