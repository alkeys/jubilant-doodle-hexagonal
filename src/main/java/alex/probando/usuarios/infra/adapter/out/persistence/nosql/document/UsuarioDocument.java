package alex.probando.usuarios.infra.adapter.out.persistence.nosql.document;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import alex.probando.usuarios.domain.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Document(collection = "usuarios")
public class UsuarioDocument {
    @Id 
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private int edad;

    
}
