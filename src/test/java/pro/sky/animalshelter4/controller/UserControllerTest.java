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
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.entityDto.ChatDto;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.repository.CallRequestRepository;
import pro.sky.animalshelter4.repository.ChatRepository;
import pro.sky.animalshelter4.repository.UserRepository;
import pro.sky.animalshelter4.service.DtoMapperService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {


    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "user";
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
    private UserRepository userRepository;
    @Autowired
    private DtoMapperService dtoMapperService;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private Generator generator = new Generator();

    @BeforeEach
    public void generateData() {
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();

        for (int i = 0; i < 2; i++) {
            Chat chatVolunteer = generator.generateChat(-1L, "", "", "", null, true);
            chatVolunteer = chatRepository.save(chatVolunteer);
            User userVolunteer = generator.generateUser(null, null, chatVolunteer, null, null, true, true);
            userVolunteer = userRepository.save(userVolunteer);
            for (int j = 0; j < 10; j++) {
                Chat chatClient = generator.generateChat(-1L, "", "", "", null, true);
                chatClient = chatRepository.save(chatClient);
                User userClient = generator.generateUser(null, null, chatClient, null, null, false, true);
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
    }


    @Test
    void createUser() {
        User user =
                userRepository.findAll().stream().findAny().orElse(null);
        assertThat(user).isNotNull();

        UserDto userDto = dtoMapperService.toDto(user);

        List<CallRequest> callRequestList = callRequestRepository.findAll();
        callRequestList.forEach(callRequest -> {
            if (Objects.equals(callRequest.getClient().getId(), user.getId()) ||
                    Objects.equals(callRequest.getVolunteer().getId(), user.getId())) {
                callRequestRepository.delete(callRequest);
            }
        });

        userRepository.delete(user);
        assertThat(userRepository.findById(userDto.getId()).orElse(null))
                .isNull();

        final int countUser = userRepository.findAll().size();

        UserDto responseEntity = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        userDto,
                        UserDto.class);
        assertEquals(countUser + 1, userRepository.findAll().size());

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getIdChat()).isEqualTo(userDto.getIdChat());
        assertThat(responseEntity.getAddress()).isEqualTo(userDto.getAddress());
        assertThat(responseEntity.getNameUser()).isEqualTo(userDto.getNameUser());
        assertThat(responseEntity.getPhone()).isEqualTo(userDto.getPhone());
    }

    @Test
    void readUser() {
        User user =
                userRepository.findAll().stream().findAny().orElse(null);
        assertThat(user).isNotNull();

        UserDto userDto = dtoMapperService.toDto(user);

        final int countUser = userRepository.findAll().size();

        UserDto responseEntity = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + user.getId(),
                        UserDto.class);
        assertEquals(countUser, userRepository.findAll().size());

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getIdChat()).isEqualTo(userDto.getIdChat());
        assertThat(responseEntity.getAddress()).isEqualTo(userDto.getAddress());
        assertThat(responseEntity.getNameUser()).isEqualTo(userDto.getNameUser());
        assertThat(responseEntity.getPhone()).isEqualTo(userDto.getPhone());
    }

    @Test
    void updateUser() {
        User user =
                userRepository.findAll().stream().findAny().orElse(null);
        assertThat(user).isNotNull();

        UserDto userDto = dtoMapperService.toDto(user);
        assertThat(userDto).isNotNull();
        assertThat(userRepository.findById(userDto.getId()).orElse(null))
                .isNotNull();
        userDto.setAddress("1234567890");

        final int countUser = userRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        userDto);
        assertEquals(countUser, userRepository.findAll().size());

        User userActual = userRepository.findById(userDto.getId()).orElse(null);

        assertThat(userActual).isNotNull();
        assertThat(userActual.getChatTelegram().getId()).isEqualTo(user.getChatTelegram().getId());
        assertThat(userActual.getAddress()).isEqualTo("1234567890");
        assertThat(userActual.getNameUser()).isEqualTo(user.getNameUser());
        assertThat(userActual.getPhone()).isEqualTo(user.getPhone());
    }


    @Test
    void deleteUser() {
        final int countUser = userRepository.findAll().size();
        User user = userRepository.findAll().stream().findFirst().orElse(null);

        List<CallRequest> callRequestList = callRequestRepository.findAll();
        callRequestList.forEach(callRequest -> {
            if (Objects.equals(callRequest.getClient().getId(), user.getId()) ||
                    Objects.equals(callRequest.getVolunteer().getId(), user.getId())) {
                callRequestRepository.delete(callRequest);
            }
        });
        ResponseEntity<String> responseEntity = testRestTemplate
                .exchange("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + user.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .contains(user.getId().toString())
                .contains(user.getChatTelegram().getId().toString())
                .contains(user.getAddress().toString())
                .contains(user.getNameUser().toString())
                .contains(user.getPhone().toString());
        assertEquals(countUser - 1, userRepository.findAll().size());
        assertThat(userRepository.findById(user.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllUserTest() {
        final long countUser = userRepository.findAll().size();
        UserDto[] userDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        UserDto[].class);
        assertThat(userDtos.length)
                .isEqualTo(countUser);
    }

    @Test
    public void getAllVolunteersTest() {
        List<User> userList = userRepository.findAll();
        final int countUser = userList.size();
        assertTrue(countUser > 0);
        List<User> actual = userList.stream().
                filter(User::isVolunteer).
                collect(Collectors.toList());
        UserDto[] userDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/volunteers/",
                        UserDto[].class);
        assertThat(userDtos.length)
                .isEqualTo(actual.size());
    }

    @Test
    public void getAllClients() {
        List<User> userList = userRepository.findAll();
        final int countUser = userList.size();
        assertTrue(countUser > 0);
        List<User> actual = userList.stream().
                filter(user -> !user.isVolunteer()).
                collect(Collectors.toList());
        UserDto[] userDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/clients/",
                        UserDto[].class);
        assertThat(userDtos.length)
                .isEqualTo(actual.size());
    }

}