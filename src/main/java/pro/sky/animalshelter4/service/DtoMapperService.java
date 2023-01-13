package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.exception.ChatNotFoundException;
import pro.sky.animalshelter4.exception.UserNotFoundException;
import pro.sky.animalshelter4.repository.ChatRepository;
import pro.sky.animalshelter4.repository.UserRepository;

/**
 *This class is required for distilling data back and forth.
 * Which are necessary for a more convenient implementation of the project architecture.
 */
@Service
public class DtoMapperService {
    private final Logger logger = LoggerFactory.getLogger(DtoMapperService.class);
    public final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public DtoMapperService(UserRepository userRepository,
                            ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }


    public Chat toEntity(ChatDto chatDto) {
        Chat chat = new Chat();
        chat.setId(chatDto.getId());
        chat.setUserNameTelegram(chatDto.getUserNameTelegram());
        chat.setFirstNameUser(chatDto.getFirstNameUser());
        chat.setLastNameUser(chatDto.getLastNameUser());
        chat.setLast_activity(chatDto.getLast_activity());
        return chat;
    }


    public ChatDto toDto(Chat chat) {
        ChatDto chatDto = new ChatDto();
        chatDto.setId(chat.getId());
        chatDto.setUserNameTelegram(chat.getUserNameTelegram());
        chatDto.setFirstNameUser(chat.getFirstNameUser());
        chatDto.setLastNameUser(chat.getLastNameUser());
        chatDto.setLast_activity(chat.getLast_activity());
        return chatDto;
    }


    public CallRequest toEntity(CallRequestDto callRequestDto) {
        CallRequest callRequest = new CallRequest();
        callRequest.setId(callRequestDto.getId());
        if (callRequestDto.getIdClient() != null) {
            User user = userRepository.
                    findById(callRequestDto.getIdClient()).
                    orElseThrow(() -> new UserNotFoundException(String.valueOf(callRequestDto.getIdClient())));
            callRequest.setClient(user);
        }
        if (callRequestDto.getIdVolunteer() != null) {
            User user = userRepository.
                    findById(callRequestDto.getIdVolunteer()).
                    orElseThrow(() -> new UserNotFoundException(String.valueOf(callRequestDto.getIdVolunteer())));
            callRequest.setVolunteer(user);
        }
        callRequest.setOpen(callRequestDto.isOpen());
        callRequest.setLocalDateTimeOpen(callRequestDto.getLocalDateTimeOpen());
        callRequest.setLocalDateTimeClose(callRequestDto.getLocalDateTimeClose());
        return callRequest;
    }


    public CallRequestDto toDto(CallRequest callRequest) {
        CallRequestDto callRequestDto = new CallRequestDto();
        callRequestDto.setId(callRequest.getId());
        if (callRequest.getClient() != null) {
            callRequestDto.setIdClient(callRequest.getClient().getId());
        }
        if (callRequest.getVolunteer() != null) {
            callRequestDto.setIdVolunteer(callRequest.getVolunteer().getId());
        }
        callRequestDto.setOpen(callRequest.isOpen());
        callRequestDto.setLocalDateTimeOpen(callRequest.getLocalDateTimeOpen());
        callRequestDto.setLocalDateTimeClose(callRequest.getLocalDateTimeClose());
        return callRequestDto;
    }


    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setNameUser(userDto.getNameUser());
        if (userDto.getIdChat() != null) {
            Chat chat = chatRepository.
                    findById(userDto.getIdChat()).
                    orElseThrow(() -> new ChatNotFoundException(String.valueOf(userDto.getIdChat())));
            user.setChatTelegram(chat);
        }
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setVolunteer(userDto.isVolunteer());
        return user;
    }


    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setNameUser(user.getNameUser());
        if (user.getChatTelegram() != null) {
            userDto.setIdChat(user.getChatTelegram().getId());
        }
        userDto.setPhone(user.getPhone());
        userDto.setAddress(user.getAddress());
        userDto.setVolunteer(user.isVolunteer());
        return userDto;
    }
}
