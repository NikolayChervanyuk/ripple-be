package com.mobi.ripple_be.configuration;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class FolderConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${ripple.user-data-dir}")
    private String USERS_DATA_PATH;

    @Value("${ripple.chat-data-dir}")
    private String CHATS_DATA_PATH;

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        try {
            log.info("Current working directory is: {}", System.getProperty("user.dir"));
            var usersPath = new File(USERS_DATA_PATH);
            if (!usersPath.exists()) {
                if (usersPath.mkdirs()) {
                    log.info("Created user data directory");
                    return;
                }
                throw new IOException("Creating directory for user data failed");
            }

            var chatsPath = new File(CHATS_DATA_PATH);
            if (!chatsPath.exists()) {
                if (chatsPath.mkdirs()) {
                    log.info("Created chats data directory");
                    return;
                }
                throw new IOException("Creating directory for chats data failed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
