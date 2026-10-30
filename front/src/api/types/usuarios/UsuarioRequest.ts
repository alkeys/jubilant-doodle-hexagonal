/*
UsuarioRequest
Collapse allobject

Solicitud para el registro y creación de un nuevo usuario cliente especificamente

    nombre
    Expand allstring≥ 1 characters
    apellido
    Expand allstring≥ 1 characters
    email
    Expand allstring≥ 1 characters
    edad
    Expand allinteger≥ 0int32
    nombreDeUsuario
    Expand allstring≥ 1 characters
    password
    Expand allstring≥ 1 characters
*/

export interface UsuarioRequest {
    nombre: string;
    apellido: string;
    email: string;
    edad: number;
    nombreDeUsuario: string
    password: string;
    roles?: string[];
}

