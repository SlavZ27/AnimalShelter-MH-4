package pro.sky.animalshelter4.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pro.sky.animalshelter4.Generator;
import pro.sky.animalshelter4.entity.Animal;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.AnimalDto;
import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.exception.ChatNotFoundException;
import pro.sky.animalshelter4.repository.CallRequestRepository;
import pro.sky.animalshelter4.repository.ChatRepository;
import pro.sky.animalshelter4.repository.UserRepository;
import pro.sky.animalshelter4.service.ChatService;
import pro.sky.animalshelter4.service.DtoMapperService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerTest {

    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "chat";
    @Autowired
    private CallRequestController callRequestController;
    @Autowired
    private ChatController chatController;
    @Autowired
    private UserController userController;
    @Autowired
    private CallRequestRepository callRequestRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DtoMapperService dtoMapperService;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private Generator generator = new Generator();
    private final Random random = new Random();

    @BeforeEach
    public void generateData() {
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();

        for (int i = 0; i < 2; i++) {
            Chat chatVolunteer = generator.generateChat(-1L, "", "", "", null, true);
            chatVolunteer = chatRepository.save(chatVolunteer);
            User userVolunteer = generator.generateUser(null, null, chatVolunteer, null, null, true, null,true);
            userVolunteer = userRepository.save(userVolunteer);
            for (int j = 0; j < 10; j++) {
                Chat chatClient = generator.generateChat(-1L, "", "", "", null, true);
                chatClient = chatRepository.save(chatClient);
                User userClient = generator.generateUser(null, null, chatClient, null, null, false, null,true);
                userClient = userRepository.save(userClient);
                CallRequest callRequest = new CallRequest();
                callRequest.setVolunteer(userVolunteer);
                callRequest.setClient(userClient);
                callRequest.setOpen(generator.generateBool());
                callRequest.setLocalDateTimeClose(generator.generateDateTime(true, LocalDateTime.now()));
                callRequest.setLocalDateTimeOpen(generator.generateDateTime(true, callRequest.getLocalDateTimeClose()));
                callRequestRepository.save(callRequest);
            }
        }
    }

    @AfterEach
    public void clearData() {
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(callRequestController).isNotNull();
        assertThat(chatController).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(callRequestRepository).isNotNull();
        assertThat(chatRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(dtoMapperService).isNotNull();
        assertThat(testRestTemplate).isNotNull();
        assertThat(chatService).isNotNull();
    }


    @Test
    void createChat() {
        Chat chat =
                chatRepository.findAll().stream().findAny().orElse(null);
        assertThat(chat).isNotNull();

        ChatDto chatDto = dtoMapperService.toDto(chat);

        List<CallRequest> callRequestList = callRequestRepository.findAll();
        callRequestList.forEach(callRequest -> {
            if (Objects.equals(callRequest.getClient().getChatTelegram().getId(), chat.getId()) ||
                    Objects.equals(callRequest.getVolunteer().getChatTelegram().getId(), chat.getId())) {
                callRequestRepository.delete(callRequest);
            }
        });

        List<User> userList = userRepository.findAll();
        userList.forEach(user -> {
            if (Objects.equals(user.getChatTelegram().getId(), chat.getId())) {
                user.setChatTelegram(null);
                userRepository.save(user);
            }
        });

        chatRepository.delete(chat);
        assertThat(chatRepository.findById(chat.getId()).orElse(null))
                .isNull();

        final int countChat = chatRepository.findAll().size();

        ChatDto responseEntity = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        chatDto,
                        ChatDto.class);
        assertEquals(countChat + 1, chatRepository.findAll().size());

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getFirstNameUser()).isEqualTo(chatDto.getFirstNameUser());
        assertThat(responseEntity.getLast_activity()).isEqualTo(chatDto.getLast_activity());
        assertThat(responseEntity.getUserNameTelegram()).isEqualTo(chatDto.getUserNameTelegram());
        assertThat(responseEntity.getLast_activity()).isEqualTo(chatDto.getLast_activity());
    }

    @Test
    void readChat() {
        Chat chat =
                chatRepository.findAll().stream().findAny().orElse(null);
        assertThat(chat).isNotNull();

        ChatDto chatDto = dtoMapperService.toDto(chat);
        assertThat(chatRepository.findById(chatDto.getId()).orElse(null))
                .isNotNull();

        final int countChat = chatRepository.findAll().size();

        ChatDto responseEntity = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + chat.getId(),
                        ChatDto.class);
        assertEquals(countChat, chatRepository.findAll().size());

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getFirstNameUser()).isEqualTo(chatDto.getFirstNameUser());
        assertThat(responseEntity.getLast_activity()).isEqualTo(chatDto.getLast_activity());
        assertThat(responseEntity.getUserNameTelegram()).isEqualTo(chatDto.getUserNameTelegram());
        assertThat(responseEntity.getLast_activity()).isEqualTo(chatDto.getLast_activity());
    }

    @Test
    void readChatNegative() {
        List<Long> chatIdList = chatRepository.findAll().stream().map(Chat::getId).collect(Collectors.toList());
        Long index = (long) random.nextInt(chatIdList.size());
        while (chatIdList.contains(index)) {
            index = (long) random.nextInt(chatIdList.size());
        }

        ChatDto responseEntity = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + index,
                        ChatDto.class);

        Long finalIndex = index;
        assertThatExceptionOfType(ChatNotFoundException.class).isThrownBy(() -> chatService.readChat(finalIndex));
    }

    @Test
    void updateChat() {
        Chat chat =
                chatRepository.findAll().stream().findAny().orElse(null);
        assertThat(chat).isNotNull();

        ChatDto chatDto = dtoMapperService.toDto(chat);
        assertThat(chatDto).isNotNull();
        assertThat(chatRepository.findById(chatDto.getId()).orElse(null))
                .isNotNull();
        chatDto.setFirstNameUser("1234567890");

        final int countChat = chatRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        chatDto);
        assertEquals(countChat, chatRepository.findAll().size());

        Chat chatActual = chatRepository.findById(chatDto.getId()).orElse(null);

        assertThat(chatActual).isNotNull();
        assertThat(chatActual.getFirstNameUser()).isEqualTo("1234567890");
        assertThat(chatActual.getLast_activity()).isEqualTo(chat.getLast_activity());
        assertThat(chatActual.getUserNameTelegram()).isEqualTo(chat.getUserNameTelegram());
        assertThat(chatActual.getLast_activity()).isEqualTo(chat.getLast_activity());
    }


    @Test
    void deleteChat() {
        final int countChat = chatRepository.findAll().size();
        Chat chat = chatRepository.findAll().stream().findFirst().orElse(null);

        List<CallRequest> callRequestList = callRequestRepository.findAll();
        callRequestList.forEach(callRequest -> {
            if (Objects.equals(callRequest.getClient().getChatTelegram().getId(), chat.getId()) ||
                    Objects.equals(callRequest.getVolunteer().getChatTelegram().getId(), chat.getId())) {
                callRequestRepository.delete(callRequest);
            }
        });

        List<User> userList = userRepository.findAll();
        userList.forEach(user -> {
            if (Objects.equals(user.getChatTelegram().getId(), chat.getId())) {
                user.setChatTelegram(null);
                userRepository.save(user);
            }
        });


        ResponseEntity<String> responseEntity = testRestTemplate
                .exchange("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + chat.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .contains(chat.getId().toString())
                .contains(chat.getLastNameUser().toString())
                .contains(chat.getFirstNameUser().toString())
                .contains(chat.getUserNameTelegram().toString())
                .contains(chat.getLast_activity().toString());
        assertEquals(countChat - 1, chatRepository.findAll().size());
        assertThat(chatRepository.findById(chat.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllChatTest() {
        final long countChat = chatRepository.findAll().size();
        ChatDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        ChatDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(countChat);
    }

}