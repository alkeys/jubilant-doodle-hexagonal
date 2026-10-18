package alex.probando.usuarios.infra.adapter.out.persistence.jpa.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id 
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column (name = "nombre", nullable = false)
    private String nombre;
    
    @Column (name = "apellido", nullable = false)
    private String apellido;

    @Column (name = "email", nullable = false, unique = true)
    private String email;

    @Column (name = "edad", nullable = false, columnDefinition = "int default 0")
    private int edad;


}
