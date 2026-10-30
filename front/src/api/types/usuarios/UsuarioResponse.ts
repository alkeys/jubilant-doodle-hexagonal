/*
UsuarioResponse
Collapse allobject

Representación de un usuario en la API

    id
    Expand allstring
    nombre
    Expand allstring
    nombreDeUsuario
    Expand allstring
    apellido
    Expand allstring
    email
    Expand allstring
    edad
    Expand allintegerint32
    activo
    Expand allboolean
    roles
    Expand allarray<string>unique
*/

export interface UsuarioResponse {
    id: string;
    nombre: string;
    nombreDeUsuario: string;
    apellido: string;
    email: string;
    edad: number;
    activo: boolean;
    roles: string[];
}
