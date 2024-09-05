package com.mobi.ripple_be.service;

import com.mobi.ripple_be.util.FileExtension;
import com.mobi.ripple_be.util.MediaUtils;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaService {

    private final PathService pathService;

    @Value("${ripple.constraints.max-small-image-bytes}")
    private Integer MAX_SMALL_IMAGE_BYTES;

    public Optional<byte[]> getImage(@NotNull Path imageDirectory, String imageName) {
        try {
            Path imagePath = imageName == null ? imageDirectory : imageDirectory.resolve(imageName);
            if (Files.exists(imagePath)) {
                return Optional.of(Files.readAllBytes(imagePath));
            }
        } catch (Exception e) {
            log.error("Getting image from directory {} failed: {}", imageDirectory, e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<byte[]> getImage(@NotNull Path imageFilePath) {
        try {
            if (Files.exists(imageFilePath)) {
                return Optional.of(Files.readAllBytes(imageFilePath));
            }
        } catch (Exception e) {
            log.error("Getting image {} failed: {}", imageFilePath, e.getMessage());
        }
        return Optional.empty();
    }

    public Mono<Boolean> storePostImage(@NonNull Mono<FilePart> image, String filename) {

        return pathService.getAuthUserPostsPathMono()
                .map(authUserPostPath -> authUserPostPath.resolve(filename + ".jpg"))
                .flatMap(filepath -> image.flatMap(fp -> fp.transferTo(filepath))
                        .then(pathService.getAuthUserSmallPostsPathMono())
                        .map(smallImagePath ->
                                MediaUtils.JPG.resizeImage(
                                        filepath.toFile(),
                                        smallImagePath.resolve(filename + ".jpg").toFile(),
                                        512
                                )
                        )
                        .map(smallImageSaved -> smallImageSaved || deleteImage(filepath))
                )
                .doOnError(Throwable::printStackTrace)
                .onErrorReturn(false);
    }

    public boolean deleteImage(Path imageFilepath) {
        try {
            if (Files.exists(imageFilepath)) {
                Files.delete(imageFilepath);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            log.error("Deleting image failed: {}", e.getMessage());
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
