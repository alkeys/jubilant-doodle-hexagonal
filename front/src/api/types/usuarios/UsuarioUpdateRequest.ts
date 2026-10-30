/**
 * UsuarioUpdateRequest
Collapse allobject

Solicitud para actualizar los datos de un usuario

    nombre
    Expand allstring≥ 1 characters
    apellido
    Expand allstring≥ 1 characters
    email
    Expand allstring≥ 1 characters
    nombreDeUsuario
    Expand allstring≥ 1 characters
    edad
    Expand allinteger≥ 0int32
    activo
    Expand allboolean
    password
    Expand allstring
 */

export interface UsuarioUpdateRequest {
    nombre: string;
    apellido: string;
    email: string;
    nombreDeUsuario: string;
    edad: number;
    activo: boolean;
    password: string;
}
