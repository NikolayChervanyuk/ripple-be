package com.mobi.ripple_be.chat.websocket.converter;

import com.mobi.ripple_be.chat.entity.mongo.Message;
import com.mobi.ripple_be.chat.websocket.dto.GenericMessageDTO;
import com.mobi.ripple_be.chat.websocket.dto.content.*;
import com.mobi.ripple_be.chat.websocket.dto.contentdto.*;
import com.mobi.ripple_be.chat.websocket.dto.message.*;
import com.mobi.ripple_be.converter.BaseConverter;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
public class MessageToGenericMessageDTO extends BaseConverter<Message, GenericMessageDTO> {
    @Override
    public GenericMessageDTO convert(Message source) {
         switch (source.getEventType()) {
            case NEW_MESSAGE -> {
                var content = (NewMessageContent) source.getMessageContent();

                var contentDTO = NewMessageContentDTO.builder()
                        .senderId(content.getSenderId())
                        .chatId(content.getChatId())
                        .message(content.getMessage())
                        .fileName(content.getFileName())
                        .fileExtension(content.getFileExtension())
                        .build();

                var messageDTO = new NewMessageDTO();
                messageDTO.setEventType(source.getEventType());
                messageDTO.setSentDate(source.getSentDate());
                messageDTO.setContent(contentDTO);

                return messageDTO;
            }
            case CHAT_OPENED -> {
                var content = (ChatOpenedContent) source.getMessageContent();

                var contentDTO = ChatOpenedContentDTO.builder()
                        .userId(content.getUserId())
                        .chatId(content.getChatId())
                        .build();

                var messageDTO = new ChatOpenedDTO();

                messageDTO.setEventType(source.getEventType());
                messageDTO.setSentDate(source.getSentDate());
                messageDTO.setContent(contentDTO);

                return messageDTO;
            }
            case CHAT_CREATED -> {
                var content = (ChatCreatedContent) source.getMessageContent();

                var contentDTO = ChatCreatedContentDTO.builder()
                        .creatorId(content.getCreatorId())
                        .chatId(content.getChatId())
                        .chatName(content.getChatName())
                        .build();

                var messageDTO = new ChatCreatedDTO();

                messageDTO.setEventType(source.getEventType());
                messageDTO.setSentDate(source.getSentDate());
                messageDTO.setContent(contentDTO);

                return messageDTO;
            }
            case NEW_PARTICIPANT -> {
                var content = (NewParticipantContent) source.getMessageContent();
                var contentDTO = NewParticipantContentDTO.builder()
                        .inviterId(content.getInviterId())
                        .participantId(content.getParticipantId())
                        .chatId(content.getChatId())
                        .build();

                var messageDTO = new NewParticipantDTO();

                messageDTO.setEventType(source.getEventType());
                messageDTO.setSentDate(source.getSentDate());
                messageDTO.setContent(contentDTO);

                return messageDTO;
            }
            case PARTICIPANT_LEFT -> {
                var content = (ParticipantLeftContent) source.getMessageContent();
                var contentDTO = ParticipantLeftContentDTO.builder()
                        .chatId(content.getChatId())
                        .participantId(content.getParticipantId())
                        .build();

                var messageDTO = new ParticipantLeftDTO();

                messageDTO.setEventType(source.getEventType());
                messageDTO.setSentDate(source.getSentDate());
                messageDTO.setContent(contentDTO);

                return messageDTO;
            }
            case PARTICIPANT_REMOVED -> {
                var content = (ParticipantRemovedContent) source.getMessageContent();
                var contentDTO = ParticipantRemovedContentDTO.builder()
                        .chatId(content.getChatId())
                        .removerId(content.getRemoverId())
                        .removedUserId(content.getRemovedParticipantId())
                        .build();

                var messageDTO = new ParticipantRemovedDTO();

                messageDTO.setEventType(source.getEventType());
                messageDTO.setSentDate(source.getSentDate());
                messageDTO.setContent(contentDTO);

                return messageDTO;
            }
             default -> throw new IllegalArgumentException("No known event type provided");
        }
    }
}
