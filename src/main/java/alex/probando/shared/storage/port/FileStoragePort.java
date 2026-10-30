package alex.probando.shared.storage.port;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    /**
     * Guarda el archivo en el almacenamiento y retorna la URL relativa o pública para accederlo.
     */
    String storeFile(MultipartFile file, String subDirectory);

    /**
     * Elimina el archivo correspondiente a la URL proporcionada.
     */
    void deleteFile(String fileUrl);
}
