package alex.probando.shared.storage.adapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import alex.probando.shared.storage.port.FileStoragePort;

@Component
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path rootLocation;

    public LocalFileStorageAdapter(@Value("${application.storage.upload-dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el directorio raíz de almacenamiento: " + uploadDir, e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se puede almacenar un archivo vacío o nulo");
        }

        try {
            Path targetDir = this.rootLocation.resolve(subDirectory).normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Generar nombre único para prevenir colisiones
            String uniqueFilename = UUID.randomUUID().toString() + extension.toLowerCase();
            Path destinationFile = targetDir.resolve(uniqueFilename).normalize();

            // Seguridad: verificar que el destino no escape del directorio raíz
            if (!destinationFile.startsWith(this.rootLocation)) {
                throw new SecurityException("No se puede almacenar un archivo fuera del directorio configurado");
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Retornar ruta web pública relativa (ej: /uploads/productos/uuid.jpg)
            return "/uploads/" + subDirectory + "/" + uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Error al almacenar el archivo: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        try {
            // Ejemplo fileUrl: /uploads/productos/xyz.png -> extraer relativo a uploads/
            String relativePath = fileUrl;
            if (relativePath.startsWith("/uploads/")) {
                relativePath = relativePath.substring("/uploads/".length());
            } else if (relativePath.startsWith("uploads/")) {
                relativePath = relativePath.substring("uploads/".length());
            }

            Path fileToDelete = this.rootLocation.resolve(relativePath).normalize();
            if (fileToDelete.startsWith(this.rootLocation) && Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException ignored) {
            // No fallar la transacción si el archivo físico ya no existía
        }
    }
}
