import axios from "axios";

/** URL base de la API */
const apiUrl = "http://localhost:8080/api/v1/";

/** Instancia preconfigurada de Axios */
export const apiClient = axios.create({
    baseURL: apiUrl,
    headers: { "Content-Type": "application/json" },
    withCredentials: true,
});
