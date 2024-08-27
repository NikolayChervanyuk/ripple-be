package com.mobi.ripple_be.service;

import com.mobi.ripple_be.util.FileExtension;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class MediaService {

    private final PathService pathService;

    public Optional<byte[]> getImage(@NotNull String imageDirectory, String imageName) {
        try {
            Path imagePath = imageName == null ? Path.of(imageDirectory) : Path.of(imageDirectory, imageName);
            if (Files.exists(imagePath)) {
                return Optional.of(Files.readAllBytes(imagePath));
            }
        } catch (Exception e) {
            log.error("Getting image failed: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public Mono<Boolean> storePostImage(@NonNull Mono<FilePart> image, String filename) {

        return pathService.getAuthUserPostsPathMono()
                .flatMap(authUserPostPath -> {
                    var imageFilePath = authUserPostPath.resolve(filename + ".jpg");
                    return image.flatMap(fp -> fp.transferTo(imageFilePath));
                })
                .thenReturn(true)
                .doOnError(Throwable::printStackTrace)
                .onErrorReturn(false);
    }

    public boolean deleteImage(String imageDirectory, String imageName) throws IOException {
        Path imagePath = Path.of(imageDirectory, imageName);

        if (Files.exists(imagePath)) {
            Files.delete(imagePath);
            return true;
        } else {
            return false;
        }
    }

    public Mono<String> storeChatFile(String chatId, Mono<FilePart> filePartToStore, String extension) {
        //TODO: check for file size
        return filePartToStore.flatMap(filePart -> {
            String fileExtension = extension;
            if (extension == null || extension.isBlank()) fileExtension = FileExtension.UNKNOWN.extensionName;
            var fileName = UUID.randomUUID() + "." + fileExtension;
            var filePath = pathService.getChatFilePath(chatId).resolve(fileName);


            return filePart.transferTo(filePath).thenReturn(fileName);
        });
    }
}
