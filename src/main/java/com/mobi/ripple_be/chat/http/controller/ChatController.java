package com.mobi.ripple_be.chat.http.controller;

import com.mobi.ripple_be.chat.http.controller.reqresp.NewChatRequest;
import com.mobi.ripple_be.chat.http.controller.reqresp.NewChatResponse;
import com.mobi.ripple_be.chat.http.controller.reqresp.NewMessageFileResponse;
import com.mobi.ripple_be.chat.http.controller.reqresp.SimpleChatParticipantsResponse;
import com.mobi.ripple_be.chat.http.model.ChatModel;
import com.mobi.ripple_be.chat.http.service.ChatService;
import com.mobi.ripple_be.service.MediaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ConversionService conversionService;
    private final ChatService chatService;
    private final MediaService mediaService;

    @PostMapping
    public Mono<NewChatResponse> createNewChat(@Valid @RequestBody NewChatRequest request) {
        return chatService.createNewChat(
                Objects.requireNonNull(conversionService.convert(request, ChatModel.class))
        ).mapNotNull(chatModel -> conversionService.convert(chatModel, NewChatResponse.class));
    }

    @GetMapping("/{chatId}/participants")
    public Mono<List<SimpleChatParticipantsResponse>> getChatParticipants(@PathVariable String chatId) {
        return chatService.getAllChatParticipants(chatId)
                .mapNotNull(userView -> conversionService.convert(userView, SimpleChatParticipantsResponse.class))
                .collectList();
    }

    @PostMapping("/{chatId}/message-file")
    public Mono<NewMessageFileResponse> createNewMessageFile(@PathVariable String chatId,
                                                             @RequestPart("file") Mono<FilePart> file,
                                                             @RequestPart("extension") String extension
    ) {
        return mediaService.storeChatFile(chatId, file, extension)
                .map(fileNameWithExtension -> NewMessageFileResponse.builder().filename(fileNameWithExtension).build());
    }

    @GetMapping("/{chatId}/message-file")
    public Mono<ResponseEntity<Flux<DataBuffer>>> getMessageFiles(@PathVariable String chatId,
                                                                  @RequestParam String fileName
    ) {
        return chatService.getMessageFile(chatId, fileName).map(ResponseEntity::ok);
    }
}
