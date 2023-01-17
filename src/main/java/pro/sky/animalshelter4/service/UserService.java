package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.exception.AnimalOwnershipNotFoundException;
import pro.sky.animalshelter4.exception.UserNotFoundException;
import pro.sky.animalshelter4.exception.VolunteersIsAbsentException;
import pro.sky.animalshelter4.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A class that handles user management.
 * Contains methods according to the res architecture.
 * And additional methods to simplify data management.
 */
@Service
public class UserService {

    public final static String MESSAGE_BAD_PHONE = "Bad phone. Try again, please";
    public final static String CAPTION_SELECT_USER = "Select user";
    public final static String MESSAGE_VOLUNTEERS_IS_ABSENT = "Sorry. All volunteers are absent";
    public final static String MESSAGE_CLIENTS_IS_ABSENT = "Sorry. Clients are absent";
    public final static String MESSAGE_CLIENT_NOT_FOUND = "Sorry. Client not found";


    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final DtoMapperService dtoMapperService;
    private final CallRequestService callRequestService;
    private final AnimalService animalService;
    private final AnimalOwnershipService animalOwnershipService;

    public UserService(UserRepository userRepository, DtoMapperService dtoMapperService, CallRequestService callRequestService, AnimalService animalService, AnimalOwnershipService animalOwnershipService) {
        this.userRepository = userRepository;
        this.dtoMapperService = dtoMapperService;
        this.callRequestService = callRequestService;
        this.animalService = animalService;
        this.animalOwnershipService = animalOwnershipService;
    }

    private final Random random = new Random();


    public UserDto createUser(UserDto userDto) {
        logger.info("Method createUser was start for create new User");
        return dtoMapperService.toDto(userRepository.save(dtoMapperService.toEntity(userDto)));
    }

    public User addUser(User user) {
        logger.info("Method addUser was start for create new User");
        return userRepository.save(user);
    }


    public UserDto readUser(Long id) {
        logger.info("Method readUser was start for find User by id");
        return dtoMapperService.toDto(
                userRepository.findById(id).
                        orElseThrow(() -> new UserNotFoundException(String.valueOf(id))));
    }

    public User findUser(Long id) {
        logger.info("Method readUser was start for find User by id");
        return userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }

    public UserDto updateUser(UserDto userDto) {
        logger.info("Method updateUser was start for update User");
        User newUser = dtoMapperService.toEntity(userDto);
        User oldUser = findUser(newUser.getId());
        if (oldUser == null) {
            throw new UserNotFoundException(String.valueOf(newUser.getId()));
        }
        oldUser.setNameUser(newUser.getNameUser());
        oldUser.setVolunteer(newUser.isVolunteer());
        oldUser.setChatTelegram(newUser.getChatTelegram());
        oldUser.setAddress(newUser.getAddress());
        oldUser.setPhone(newUser.getPhone());
        return dtoMapperService.toDto(userRepository.save(oldUser));
    }

    public UserDto deleteUser(Long id) {
        User user = new User();
        user.setId(id);
        return dtoMapperService.toDto(deleteUser(user));
    }


    public User deleteUser(User user) {
        logger.info("Method deleteUser was start for delete User");
        if (user.getId() == null) {
            throw new IllegalArgumentException("Incorrect id user");
        }
        User userFound = userRepository.findById(user.getId()).
                orElseThrow(() -> new UserNotFoundException(String.valueOf(user.getId())));
        userRepository.delete(userFound);
        return userFound;
    }

    public List<UserDto> getAll() {
        logger.info("Method getAll was start for return all Users");
        return userRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<UserDto> getAllVolunteers() {
        logger.info("Method getAllVolunteers was start for return all Users of Volunteers");
        return userRepository.getAllVolunteers().stream().
                map(dtoMapperService::toDto).
                collect(Collectors.toList());
    }

    public List<UserDto> getAllClientsDto() {
        logger.info("Method getAllClientsDto was start for return all Users of Clients");
        return userRepository.getAllClients().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<User> getAllClientsEntity() {
        logger.info("Method getAllClientsEntity was start for return all Users of Clients");
        return new ArrayList<>(userRepository.getAllClients());
    }

    public boolean isUserWithTelegramChatIdVolunteer(Long idChatTelegram) {
        logger.info("Method isUserOfVolunteer was start for to check if the User with id = {} is a volunteer", idChatTelegram);
        User user = userRepository.getByIdTelegramChatAndVolunteer(idChatTelegram);
        if (user != null) {
            logger.debug("Method isUserWithTelegramChatIdVolunteer don't detected volunteer by idUser = {}", idChatTelegram);
            return true;
        }
        logger.debug("Method isUserWithTelegramChatIdVolunteer detected volunteer by idUser = {}", idChatTelegram);
        return false;
    }

    public boolean isUserWithTelegramChatIdOwner(Long idChatTelegram) {
        logger.info("Method isUserWithTelegramChatIdOwner was start for to check if the User with id = {} is a Owner",
                idChatTelegram);
        User user = userRepository.getByIdTelegramChatAndOwner(idChatTelegram);
        if (user != null) {
            logger.debug("Method isUserWithTelegramChatIdOwner don't detected Owner by idUser = {}", idChatTelegram);
            return true;
        }
        logger.debug("Method isUserWithTelegramChatIdOwner detected Owner by idUser = {}", idChatTelegram);
        return false;
    }

    public void changePhone(Chat chat, String phone) {
        User user = getUserWithTelegramUserId(chat.getId());
        logger.info("Method changePhone was start for change phone by User id = {}",
                user.getId());
        user.setPhone(phone);
        addUser(user);
    }

    public User getUserWithTelegramUserId(Long idUser) {
        logger.info("Method getUserWithTelegramUserId was start for to find user with telegram User id = {}", idUser);
        return userRepository.getByIdTelegramChat(idUser);
    }

    /**
     * @return User belonging to a volunteer or null
     */
    public User getRandomVolunteer() {
        List<User> userList = userRepository.getAllVolunteers();
        if (userList.size() != 0) {
            return userList.get(random.nextInt(userList.size()));
        }
        return null;
    }

    public User mapChatToUser(Chat chat) {
        User user = new User();
        user.setNameUser(chat.getFirstNameUser() + " " + chat.getLastNameUser());
        user.setChatTelegram(chat);
        user.setVolunteer(false);
        return addUser(user);
    }

    public User getUserFromChat(Chat chat) {
        User user = getUserWithTelegramUserId(chat.getId());
        if (user == null) {
            return mapChatToUser(chat);
        }
        return user;
    }

    public CallRequest createCallRequest(Chat chatClient) {
        User userClient = getUserFromChat(chatClient);
        User userVolunteer = getRandomVolunteer();
        if (userVolunteer == null) {
            throw new VolunteersIsAbsentException();
        }
        return callRequestService.createCallRequest(userClient, userVolunteer);
    }

    public List<CallRequest> getListOpenCallRequests(Chat chatVolunteer) {
        User userVolunteer = getUserFromChat(chatVolunteer);
        return callRequestService.getAllOpenCallRequestVolunteer(userVolunteer);
    }

    public void closeCallRequest(Chat chatVolunteer, Long idCallRequest) {
        User userVolunteer = getUserFromChat(chatVolunteer);
        callRequestService.closeCallRequest(userVolunteer, idCallRequest);
    }

    public AnimalOwnership createOwnershipAnimal(Long idUserClient, Long idAnimal) {
        User userClient = userRepository.findById(idUserClient).orElseThrow(() ->
                new UserNotFoundException(String.valueOf(idUserClient)));
        Animal animal = animalService.findAnimal(idAnimal);

        AnimalOwnership animalOwnership = new AnimalOwnership();
        animalOwnership.setOwner(userClient);
        animalOwnership.setAnimal(animal);
        animalOwnership.setDateStartOwn(LocalDate.now());
        animalOwnership.setDateEndTrial(LocalDate.now().plusDays(30));
        return animalOwnershipService.addAnimalOwnership(animalOwnership);
    }

    public Report findOrCreateActualReport(Chat chatUserOwner) {
        User userOwner = getUserFromChat(chatUserOwner);
        return animalOwnershipService.findOrCreateActualReport(userOwner);
    }

    public Report createUpdateReport(Chat chatUserOwner, String diet, String feeling, String behavior, String idMedia) {
        User userOwner = getUserFromChat(chatUserOwner);
        return animalOwnershipService.createReport(userOwner, diet, feeling, behavior, idMedia);
    }


    public Report getOpenAndNotApproveReport() {
        return animalOwnershipService.getOpenAndNotApproveReport();
    }

    public Report approveReport(Long idReport, boolean approve) {
        return animalOwnershipService.approveReport(idReport, approve);
    }
}
