export interface AuthResponse {
    mensaje: string;
    token?: string;
    tipoToken?: string;
    username?: string;
    roles?: string[];
}

