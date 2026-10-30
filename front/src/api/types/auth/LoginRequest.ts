/**
 * LoginRequest
Collapse allobject

Credenciales para inicio de sesión

    username
    Collapse allstring≥ 1 characters

    Nombre de usuario o email registrado
    Example"juanbodoque"
    password
    Expand allstring≥ 1 characters
 */

export interface LoginRequest {
    username: string;
    password: string;
}
