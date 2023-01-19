package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.exception.UserNotFoundException;
import pro.sky.animalshelter4.exception.VolunteersIsAbsentException;
import pro.sky.animalshelter4.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public final static String MESSAGE_PHONE_IS_OK = "Phone has been changed";
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


    /**
     * The method adds a new user to the repository and returns the same instance
     * Using {@link UserRepository#save(Object)}
     * @param userDto is not by null.
     * @return User
     */
    public UserDto createUser(UserDto userDto) {
        logger.info("Method createUser was start for create new User");
        return dtoMapperService.toDto(userRepository.save(dtoMapperService.toEntity(userDto)));
    }

    /**
     * The method adds a new user to the repository and returns the same instance
     * Using {@link UserRepository#save(Object)}
     * @param user is not by null.
     * @return User
     */
    public User addUser(User user) {
        logger.info("Method addUser was start for create new User");
        return userRepository.save(user);
    }

    /**
     * The method outputs the user from the database using the repository by its chat_id
     * Using {@link UserRepository#findById(Object)}
     * @param id is not by null.
     * @return User by id
     */
    public UserDto readUser(Long id) {
        logger.info("Method readUser was start for find User by id");
        return dtoMapperService.toDto(
                userRepository.findById(id).
                        orElseThrow(() -> new UserNotFoundException(String.valueOf(id))));
    }

    /**
     * The method outputs the user from the database using the repository by its chat_id
     * Using {@link UserRepository#findById(Object)}
     * @param id is not by null.
     * @return User by id
     */
    public User findUser(Long id) {
        logger.info("Method readUser was start for find User by id");
        return userRepository.findById(id).
                orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }

    /**
     * The method update a new user to the repository and returns the same instance
     * Using {@link UserRepository#save(Object)}
     * @param userDto is not by null.
     * @return User
     */
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

    /**
     * The method delete the user from the database using the repository by its chat_id
     * Using {@link UserRepository#delete(Object)}
     * @param id is not by null.
     * @return User by id
     */
    public UserDto deleteUser(Long id) {
        User user = new User();
        user.setId(id);
        return dtoMapperService.toDto(deleteUser(user));
    }

    /**
     * The method delete the user from the database using the repository by its chat_id
     * Using {@link UserRepository#delete(Object)}
     * @param user is not by null.
     * @return User by id
     */
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

    /**
     * The all method outputs the user from the database using the repository
     * Using {@link UserRepository#findAll()}
     * @return full user
     */
    public List<UserDto> getAll() {
        logger.info("Method getAll was start for return all Users");
        return userRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * The all method outputs the volunteers from the database using the repository
     * Using {@link UserRepository#getAllVolunteers()}
     * @return full volunteers
     */
    public List<UserDto> getAllVolunteers() {
        logger.info("Method getAllVolunteers was start for return all Users of Volunteers");
        return userRepository.getAllVolunteers().stream().
                map(dtoMapperService::toDto).
                collect(Collectors.toList());
    }

    /**
     * The all method outputs the clients from the database using the repository
     * Using {@link UserRepository#getAllClients()}
     * @return full clients
     */
    public List<UserDto> getAllClientsDto() {
        logger.info("Method getAllClientsDto was start for return all Users of Clients");
        return userRepository.getAllClients().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**The all method outputs the clients from the database using the repository
     * Using {@link UserRepository#getAllClients()}
     * @return full clients
     */
    public List<User> getAllClientsEntity() {
        logger.info("Method getAllClientsEntity was start for return all Users of Clients");
        return new ArrayList<>(userRepository.getAllClients());
    }

    /**
     * Method check by user id whether this user is a volunteer
     * using {@link UserRepository#getByIdTelegramChatAndVolunteer(Long)}
     * @param idChatTelegram is not by null
     * @return volonter or null
     */
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

    /**
     * Method check by user id whether this user is owner
     * using {@link UserRepository#getByIdTelegramChatAndOwner(Long)}
     * @param idChatTelegram is not by null
     * @return owner or null
     */
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
    /**
     * The method allows the user to change the phone number
     * @param chat is not by null
     * @param phone is not by null
     */
    public void changePhone(Chat chat, String phone) {
        User user = getUserWithTelegramUserId(chat.getId());
        logger.info("Method changePhone was start for change phone by User id = {}",
                user.getId());
        user.setPhone(phone);
        addUser(user);
    }

    /**
     * The method allows you to get a chat user by User id
     * using {@link UserRepository#getByIdTelegramChat(Long)}
     * @param idUser is not null
     * @return User
     */
    public User getUserWithTelegramUserId(Long idUser) {
        logger.info("Method getUserWithTelegramUserId was start for to find user with telegram User id = {}", idUser);
        return userRepository.getByIdTelegramChat(idUser);
    }

    /**
     * This method finds a random volunteer
     * @return returns the volunteer if there is one
     */
    public User getRandomVolunteer() {
        List<User> userList = userRepository.getAllVolunteers();
        if (userList.size() != 0) {
            return userList.get(random.nextInt(userList.size()));
        }
        return null;
    }

    /**
     * adds matching user for chat
     * @param chat is not null
     * @return user
     */
    public User mapChatToUser(Chat chat) {
        User user = new User();
        user.setNameUser(chat.getFirstNameUser() + " " + chat.getLastNameUser());
        user.setChatTelegram(chat);
        user.setVolunteer(false);
        return addUser(user);
    }

    /**
     * This metod get user from chat by user id
     * @param chat is not by null
     * @return user or adds matching user for chat
     */
    public User getUserFromChat(Chat chat) {
        User user = getUserWithTelegramUserId(chat.getId());
        if (user == null) {
            return mapChatToUser(chat);
        }
        return user;
    }

    /** this method gets the user from the chat takes the nearest volunteer
     * if there is no volunteer now, it gives an error {@link VolunteersIsAbsentException }
     * @param chatClient is not null
     * @return userClient, userVolunteer
     */
    public CallRequest createCallRequest(Chat chatClient) {
        User userClient = getUserFromChat(chatClient);
        User userVolunteer = getRandomVolunteer();
        if (userVolunteer == null) {
            throw new VolunteersIsAbsentException();
        }
        return callRequestService.createCallRequest(userClient, userVolunteer);
    }

    /**
     * This method allows you to get a list open call request
     * @param chatVolunteer is not null
     * @return getAllOpenCallRequestVolunteer
     */
    public List<CallRequest> getListOpenCallRequests(Chat chatVolunteer) {
        User userVolunteer = getUserFromChat(chatVolunteer);
        return callRequestService.getAllOpenCallRequestVolunteer(userVolunteer);
    }

    /**
     * This method allows you to close a call request
     * @param chatVolunteer is not null
     * @param idCallRequest is not null
     */
    public void closeCallRequest(Chat chatVolunteer, Long idCallRequest) {
        User userVolunteer = getUserFromChat(chatVolunteer);
        callRequestService.closeCallRequest(userVolunteer, idCallRequest);
    }

    /**
     * This method attach an animal to a person
     * @param idUserClient is not null
     * @param idAnimal is not null
     * @return AnimalOwnership
     */
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

    /**
     * This method allows find or create report
     * @param chatUserOwner is not null
     * @return report create or find owner
     */
    public Report findOrCreateActualReport(Chat chatUserOwner) {
        User userOwner = getUserFromChat(chatUserOwner);
        return animalOwnershipService.findOrCreateActualReport(userOwner);
    }


    /**
     * This method create report on update
     * @param chatUserOwner is not null
     * @param diet
     * @param feeling
     * @param behavior
     * @param idMedia
     * @return report
     */
    public Report createUpdateReport(Chat chatUserOwner, String diet, String feeling, String behavior, String idMedia) {
        User userOwner = getUserFromChat(chatUserOwner);
        return animalOwnershipService.createReport(userOwner, diet, feeling, behavior, idMedia);
    }


    /**
     * This method allows get report, open and not approve
     * @return report open and not approve
     */
    public Report getOpenAndNotApproveReport() {
        return animalOwnershipService.getOpenAndNotApproveReport();
    }

    /**
     *
     * This method allows you to approve the owner report
     * @param idReport is not null
     * @param approve try
     * @return report
     */
    public Report approveReport(Long idReport, boolean approve) {
        return animalOwnershipService.approveReport(idReport, approve);
    }
    /**
     * This method allows update date last notification user to the Present time
     * @return user with a new LocalDateTime
     */
    public void changeUserDateLastNotificationToNow(Chat chat) {
        User user = getUserFromChat(chat);
        user.setDateLastNotification(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Method to Get One Non-Approved Open Possession of an Animal and Take the animals Away
     * @return {@link AnimalOwnershipService#getOneNotApproveOpenAnimalOwnershipWithNotTrial()}
     */
    public AnimalOwnership getOneNotApproveOpenAnimalOwnershipWithNotTrial() {
        return animalOwnershipService.getOneNotApproveOpenAnimalOwnershipWithNotTrial();
    }

    /**
     * This method allows you to approve the owner of the animal
     * @param idAnimalOwnership is not null
     * @param approve is not null
     * @return
     */
    public AnimalOwnership approveAnimalOwnership(Long idAnimalOwnership, boolean approve) {
        return animalOwnershipService.approveAnimalOwnership(idAnimalOwnership, approve);
    }

    /**
     * This method allows you to extend the trial ownership of the animal
     * @param idAnimalOwnership is not null
     * @return {@link AnimalOwnershipService#extendTrialAnimalOwnershipForAWeek(Long)}  }
     */
    public AnimalOwnership extendTrialAnimalOwnership(Long idAnimalOwnership) {
        return animalOwnershipService.extendTrialAnimalOwnershipForAWeek(idAnimalOwnership);
    }
}
