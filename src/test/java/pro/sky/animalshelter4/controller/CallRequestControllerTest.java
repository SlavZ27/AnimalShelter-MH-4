package pro.sky.animalshelter4.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import pro.sky.animalshelter4.Generator;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Chat;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.repository.CallRequestRepository;
import pro.sky.animalshelter4.repository.ChatRepository;
import pro.sky.animalshelter4.repository.UserRepository;
import pro.sky.animalshelter4.service.DtoMapperService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CallRequestControllerTest {

    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "call_request";
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
    private final Random random = new Random();

    @BeforeEach
    public void generateData() {
        callRequestRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();

        for (int i = 0; i < 2; i++) {
            Chat chatVolunteer = generator.generateChat(-1L, "", "", "", null, true);
            chatVolunteer = chatRepository.save(chatVolunteer);
            User userVolunteer = generator.generateUser(null, null, chatVolunteer, null, null, true, null, true);
            userVolunteer = userRepository.save(userVolunteer);
            for (int j = 0; j < 10; j++) {
                Chat chatClient = generator.generateChat(-1L, "", "", "", null, true);
                chatClient = chatRepository.save(chatClient);
                User userClient = generator.generateUser(null, null, chatClient, null, null, false, null, true);
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
    void createCallRequest() {
        CallRequest callRequest =
                callRequestRepository.findAll().stream().findAny().orElse(null);
        assertThat(callRequest).isNotNull();

        CallRequestDto callRequestDto = dtoMapperService.toDto(callRequest);
        callRequestRepository.delete(callRequest);
        assertThat(callRequestRepository.findById(callRequest.getId()).orElse(null))
                .isNull();

        final int countCallRequest = callRequestRepository.findAll().size();

        CallRequestDto responseEntity = testRestTemplate.
                postForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        callRequestDto,
                        CallRequestDto.class);
        assertEquals(countCallRequest + 1, callRequestRepository.findAll().size());

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getIdClient()).isEqualTo(callRequestDto.getIdClient());
        assertThat(responseEntity.getIdVolunteer()).isEqualTo(callRequestDto.getIdVolunteer());
        assertThat(responseEntity.getLocalDateTimeOpen()).isEqualTo(callRequestDto.getLocalDateTimeOpen());
        assertThat(responseEntity.getLocalDateTimeClose()).isEqualTo(callRequestDto.getLocalDateTimeClose());
        assertThat(responseEntity.isOpen()).isEqualTo(callRequestDto.isOpen());
    }

    @Test
    void readCallRequest() {
        CallRequest callRequest =
                callRequestRepository.findAll().stream().findAny().orElse(null);
        assertThat(callRequest).isNotNull();

        CallRequestDto callRequestDto = dtoMapperService.toDto(callRequest);
        assertThat(callRequestRepository.findById(callRequest.getId()).orElse(null))
                .isNotNull();

        final int countCallRequest = callRequestRepository.findAll().size();

        CallRequestDto responseEntity = testRestTemplate.
                getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + callRequest.getId(),
                        CallRequestDto.class);
        assertEquals(countCallRequest, callRequestRepository.findAll().size());

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getIdClient()).isEqualTo(callRequestDto.getIdClient());
        assertThat(responseEntity.getIdVolunteer()).isEqualTo(callRequestDto.getIdVolunteer());
        assertThat(responseEntity.getLocalDateTimeOpen()).isEqualTo(callRequestDto.getLocalDateTimeOpen());
        assertThat(responseEntity.getLocalDateTimeClose()).isEqualTo(callRequestDto.getLocalDateTimeClose());
        assertThat(responseEntity.isOpen()).isEqualTo(callRequestDto.isOpen());
    }

    @Test
    void updateCallRequest() {
        CallRequest callRequest =
                callRequestRepository.findAll().stream().findAny().orElse(null);
        assertThat(callRequest).isNotNull();

        CallRequestDto callRequestDto = dtoMapperService.toDto(callRequest);
        assertThat(callRequestRepository.findById(callRequest.getId()).orElse(null))
                .isNotNull();

        callRequestDto.setOpen(!callRequestDto.isOpen());

        final int countCallRequest = callRequestRepository.findAll().size();
        testRestTemplate.
                put("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        callRequestDto);

        assertEquals(countCallRequest, callRequestRepository.findAll().size());

        CallRequest callRequestActual = callRequestRepository.findById(callRequestDto.getId()).orElse(null);

        assertThat(callRequest).isNotNull();
        assertThat(callRequest.getClient().getId()).isEqualTo(callRequestActual.getClient().getId());
        assertThat(callRequest.getVolunteer().getId()).isEqualTo(callRequestActual.getVolunteer().getId());
        assertThat(callRequest.getLocalDateTimeOpen()).isEqualTo(callRequestActual.getLocalDateTimeOpen());
        assertThat(callRequest.getLocalDateTimeClose()).isEqualTo(callRequestActual.getLocalDateTimeClose());
        assertThat(callRequest.isOpen()).isEqualTo(!callRequestActual.isOpen());
    }


    @Test
    void deleteCallRequest() {
        final int countCallRequest = callRequestRepository.findAll().size();
        CallRequest callRequest = callRequestRepository.findAll().stream().findFirst().orElse(null);

        ResponseEntity<String> responseEntity = testRestTemplate
                .exchange("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + callRequest.getId()
                        , HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {
                        });

        assertEquals(countCallRequest - 1, callRequestRepository.findAll().size());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .contains(callRequest.getId().toString())
                .contains(callRequest.getClient().getId().toString())
                .contains(callRequest.getVolunteer().getId().toString())
                .contains(callRequest.getLocalDateTimeOpen().toString().substring(0, 17));
        assertThat(callRequestRepository.findById(callRequest.getId()).orElse(null))
                .isNull();
    }

    @Test
    public void getAllCallRequestTest() {
        final long countCallRequest = callRequestRepository.findAll().size();
        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING,
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(countCallRequest);
    }

    @Test
    public void getAllCallRequestVolunteerTest() {
        List<CallRequest> callRequestList = callRequestRepository.findAll();
        final int countCallRequest = callRequestList.size();
        assertTrue(countCallRequest > 0);

        User user = userRepository.findAll().stream().
                filter(User::isVolunteer).findFirst().orElse(null);
        assertThat(user).isNotNull();

        List<CallRequest> actual = callRequestList.stream().
                filter(CallRequest::isOpen).
                filter(callRequest -> callRequest.getVolunteer().getId().equals(user.getId())).
                collect(Collectors.toList());

        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/volunteer/" + user.getId(),
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(actual.size());
    }

    @Test
    public void getAllCallRequestClientTest() {
        List<CallRequest> callRequestList = callRequestRepository.findAll();
        final int countCallRequest = callRequestList.size();
        assertTrue(countCallRequest > 0);

        User user = userRepository.findAll().stream().
                filter(user1 -> user1.isVolunteer() == false).findFirst().orElse(null);
        assertThat(user).isNotNull();

        List<CallRequest> actual = callRequestList.stream().
                filter(CallRequest::isOpen).
                filter(callRequest -> callRequest.getClient().getId().equals(user.getId())).
                collect(Collectors.toList());

        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/client/" + user.getId(),
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(actual.size());
    }

    @Test
    public void getAllOpenCallRequestTest() {
        List<CallRequest> callRequestList = callRequestRepository.findAll();
        final int countCallRequest = callRequestList.size();
        assertTrue(countCallRequest > 0);

        List<CallRequest> actual = callRequestList.stream().
                filter(CallRequest::isOpen).
                collect(Collectors.toList());

        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/open/",
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(actual.size());
    }

    @Test
    public void getAllCloseCallRequestTest() {
        List<CallRequest> callRequestList = callRequestRepository.findAll();
        final int countCallRequest = callRequestList.size();
        assertTrue(countCallRequest > 0);

        List<CallRequest> actual = callRequestList.stream().
                filter(callRequest -> !callRequest.isOpen()).
                collect(Collectors.toList());

        CallRequestDto[] callRequestDtos = testRestTemplate
                .getForObject("http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/close/",
                        CallRequestDto[].class);
        assertThat(callRequestDtos.length)
                .isEqualTo(actual.size());
    }


}