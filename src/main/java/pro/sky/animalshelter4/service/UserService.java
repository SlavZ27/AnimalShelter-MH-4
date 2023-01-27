package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.exception.AnimalNotFoundException;
import pro.sky.animalshelter4.exception.ShelterNotFoundException;
import pro.sky.animalshelter4.exception.UserNotFoundException;
import pro.sky.animalshelter4.exception.VolunteersIsAbsentException;
import pro.sky.animalshelter4.repository.ShelterRepository;
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
    private final ShelterRepository shelterRepository;

    public UserService(UserRepository userRepository, DtoMapperService dtoMapperService, CallRequestService callRequestService, AnimalService animalService, AnimalOwnershipService animalOwnershipService, ShelterRepository shelterRepository) {
        this.userRepository = userRepository;
        this.dtoMapperService = dtoMapperService;
        this.callRequestService = callRequestService;
        this.animalService = animalService;
        this.animalOwnershipService = animalOwnershipService;
        this.shelterRepository = shelterRepository;
    }

    private final Random random = new Random();


    /**
     * The method adds a new user to the repository and returns the same instance
     * Using {@link UserRepository#save(Object)}
     *
     * @param userDto is not by null.
     * @return User
     */
    public UserDto createUser(UserDto userDto, String shelterDesignation) {
        logger.info("Method createUser was start for create new User");
        User user = dtoMapperService.toEntity(userDto, shelterDesignation);
        user.setId(null);
        return dtoMapperService.toDto(userRepository.save(user));
    }

    /**
     * The method adds a new user to the repository and returns the same instance
     * Using {@link UserRepository#save(Object)}
     *
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
     *
     * @param id is not by null.
     * @return User by id
     */
    public UserDto readUser(Long id, String shelterDesignation) {
        logger.info("Method readUser was start for find User by id");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return dtoMapperService.toDto(
                userRepository.getUserByIdAndShelter(id, shelter.getId()).
                        orElseThrow(() -> new UserNotFoundException(String.valueOf(id))));
    }

    /**
     * The method outputs the user from the database using the repository by its chat_id
     * Using {@link UserRepository#findById(Object)}
     *
     * @param id is not by null.
     * @return User by id
     */
    public User findUserWithShelter(Long id, Shelter shelter) {
        logger.info("Method readUser was start for find User by id");
        return userRepository.getUserByIdAndShelter(id, shelter.getId()).
                orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }

    /**
     * The method update a new user to the repository and returns the same instance
     * Using {@link UserRepository#save(Object)}
     *
     * @param userDto is not by null.
     * @return User
     */
    public UserDto updateUser(UserDto userDto, String shelterDesignation) {
        logger.info("Method updateUser was start for update User");
        User newUser = dtoMapperService.toEntity(userDto, shelterDesignation);
        User oldUser = userRepository.getUserByIdAndShelter(
                newUser.getId(),
                newUser.getShelter().getId()).orElseThrow(
                () -> new UserNotFoundException(String.valueOf(newUser.getId()))
        );

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
     *
     * @param id is not by null.
     * @return User by id
     */
    public UserDto deleteUser(Long id, String shelterDesignation) {
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        User user = new User();
        user.setId(id);
        return dtoMapperService.toDto(deleteUserWithShelter(user, shelter));
    }

    /**
     * The method delete the user from the database using the repository by its chat_id
     * Using {@link UserRepository#delete(Object)}
     *
     * @param user is not by null.
     * @return User by id
     */
    public User deleteUserWithShelter(User user, Shelter shelter) {
        logger.info("Method deleteUser was start for delete User");
        if (user.getId() == null) {
            throw new IllegalArgumentException("Incorrect id user");
        }
        User userFound = userRepository.getUserByIdAndShelter(user.getId(), shelter.getId()).orElseThrow(() ->
                new ShelterNotFoundException(user.getId().toString()));
        userRepository.delete(userFound);
        return userFound;
    }

    /**
     * The all method outputs the volunteers from the database using the repository
     * Using {@link UserRepository#getAllVolunteersWithShelter(Long)}
     *
     * @return full volunteers
     */
    public List<UserDto> getAllVolunteers(String shelterDesignation) {
        logger.info("Method getAllVolunteers was start for return all Users of Volunteers");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return userRepository.getAllVolunteersWithShelter(shelter.getId()).stream().
                map(dtoMapperService::toDto).
                collect(Collectors.toList());
    }

    /**
     * The all method outputs the clients from the database using the repository
     * Using {@link UserRepository#getAllClientsWithShelter(Long)}
     *
     * @return full clients
     */
    public List<UserDto> getAllClientsDto(String shelterDesignation) {
        logger.info("Method getAllClientsDto was start for return all Users of Clients");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                new ShelterNotFoundException(shelterDesignation));
        return userRepository.getAllClientsWithShelter(shelter.getId()).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * The all method outputs the clients from the database using the repository
     * Using {@link UserRepository#getAllClientsWithShelter(Long)}
     *
     * @return full clients
     */
    public List<User> getAllClientsEntityWithShelter(Shelter shelter) {
        logger.info("Method getAllClientsEntity was start for return all Users of Clients");
        return new ArrayList<>(userRepository.getAllClientsWithShelter(shelter.getId()));
    }

    /**
     * Method check by user id whether this user is a volunteer
     * using {@link UserRepository#findUserWithTelegramChatIdAndShelterId(Long idChatTelegram, Long idShelter)}
     *
     * @param chat    is not by null
     * @param shelter is not by null
     * @return volonter or null
     */
    public boolean isUserWithTelegramChatIdVolunteerInCurrentShelter(Chat chat, Shelter shelter) {
        logger.info("Method isUserWithTelegramChatIdOwnerInCurrentShelter was start for to check if the User with chatId = {} and shelterId = {}, is a Volunteer",
                chat.getId(), shelter.getId());
        User user = userRepository.findUserWithTelegramChatIdAndShelterId(chat.getId(), shelter.getId());
        if (user != null && user.isVolunteer()) {
            return true;
        }
        return false;

    }

    /**
     * Method check by user id whether this user is owner
     * using {@link UserRepository#findUserWithShelterIdAndTelegramChatIdInAnimalOwnership(Long, Long)}
     *
     * @param chat    is not by null
     * @param shelter is not by null
     * @return owner or null
     */
    public boolean isUserWithTelegramChatIdOwnerInCurrentShelter(Chat chat, Shelter shelter) {
        logger.info("Method isUserWithTelegramChatIdOwnerInCurrentShelter was start for to check if the User with chatId = {} and shelterId = {}, is a Owner",
                chat.getId(), shelter.getId());
        User user = userRepository.findUserWithShelterIdAndTelegramChatIdInAnimalOwnership(chat.getId(), shelter.getId());
        if (user != null) {
            logger.debug("Method isUserWithTelegramChatIdOwner don't detected Owner by idUser = {}", chat.getId());
            return true;
        }
        logger.debug("Method isUserWithTelegramChatIdOwner detected Owner by idUser = {}", chat.getId());
        return false;
    }

    /**
     * The method allows the user to change the phone number
     *
     * @param chat  is not by null
     * @param phone is not by null
     */
    public User changePhoneUser(Chat chat, Shelter shelter, String phone) {
        User user = getUserFromChatAndShelter(chat, shelter);
        logger.info("Method changePhone was start for change phone by User id = {}",
                user.getId());
        user.setPhone(phone);
        return addUser(user);
    }

    /**
     * This method finds a random volunteer
     *
     * @return returns the volunteer if there is one
     */
    public User getRandomVolunteer(Shelter shelter) {
        List<User> userList = userRepository.getAllVolunteersWithShelter(shelter.getId());
        if (userList.size() != 0) {
            return userList.get(random.nextInt(userList.size()));
        }
        throw new VolunteersIsAbsentException();
    }

    /**
     * This metod get user from chat by user id
     *
     * @param chat is not by null
     * @return user or adds matching user for chat
     */
    public User getUserFromChatAndShelter(Chat chat, Shelter shelter) {
        User user = userRepository.findUserWithTelegramChatIdAndShelterId(chat.getId(), shelter.getId());
        if (user == null) {
            return mapChatToUser(chat);
        }
        return user;
    }

    /**
     * adds matching user for chat
     *
     * @param chat is not null
     * @return user
     */
    public User mapChatToUser(Chat chat) {
        User user = new User();
        user.setNameUser(chat.getFirstNameUser() + " " + chat.getLastNameUser());
        user.setChatTelegram(chat);
        user.setVolunteer(false);
        user.setShelter(chat.getShelter());
        return addUser(user);
    }

    /**
     * this method gets the user from the chat takes the nearest volunteer
     * if there is no volunteer now, it gives an error {@link VolunteersIsAbsentException }
     *
     * @param chatClient is not null
     * @return userClient, userVolunteer
     */
    public CallRequest createCallRequestClientWithChatAndShelterWithRandomVolunteer(Chat chatClient, Shelter shelter) {
        User userClient = getUserFromChatAndShelter(chatClient, shelter);
        User userVolunteer = getRandomVolunteer(shelter);
        if (userVolunteer == null) {
            throw new VolunteersIsAbsentException();
        }
        return callRequestService.findOpenOrCreateCallRequest(userClient, userVolunteer);
    }

    /**
     * This method allows you to get a list open call request
     *
     * @param chatVolunteer is not null
     * @return getAllOpenCallRequestVolunteer
     */
    public CallRequest getOpenCallRequestForVolunteerWithChatAndShelter(Chat chatVolunteer, Shelter shelter) {
        User userVolunteer = getUserFromChatAndShelter(chatVolunteer, shelter);
        return callRequestService.getAllOpenCallRequestVolunteer(userVolunteer, shelter);
    }

    /**
     * This method allows you to close a call request
     *
     * @param chatVolunteer is not null
     * @param idCallRequest is not null
     */
    public CallRequest closeCallRequestForVolunteerWithChatInShelter(
            Chat chatVolunteer,
            Shelter shelter,
            Long idCallRequest) {
        User userVolunteer = getUserFromChatAndShelter(chatVolunteer, shelter);
        return callRequestService.closeCallRequestWithShelter(shelter, userVolunteer, idCallRequest);
    }


    /**
     * This method allows update date last notification user to the Present time
     *
     * @return user with a new LocalDateTime
     */
    public void changeUserDateLastNotificationToNow(User user) {
        if (user != null && user.getId() != null) {
            user.setDateLastNotification(LocalDateTime.now());
        }
        userRepository.save(user);
    }

    /**
     * This method attach an animal to a person
     *
     * @param idUserClient is not null
     * @param animal       is not null
     * @return AnimalOwnership
     */
    public AnimalOwnership createOwnershipAnimalWithShelter(Long idUserClient, Shelter shelter, Animal animal) {
        User userClient = findUserWithShelter(idUserClient, shelter);
        if (userClient == null || !userClient.getShelter().getId().equals(shelter.getId())) {
            return null;
        }
        AnimalOwnership animalOwnership = new AnimalOwnership();
        animalOwnership.setOwner(userClient);
        animalOwnership.setAnimal(animal);
        animalOwnership.setDateStartOwn(LocalDate.now());
        animalOwnership.setDateEndTrial(LocalDate.now().plusDays(30));
        return animalOwnershipService.addAnimalOwnership(animalOwnership);
    }

    /**
     * This method allows find or create report
     *
     * @param chatUserOwner is not null
     * @return report create or find owner
     */
    public Report findOrCreateTodayReportOwnerWithShelter(Chat chatUserOwner, Shelter shelter) {
        User userOwner = getUserFromChatAndShelter(chatUserOwner, shelter);
        return animalOwnershipService.findOrCreateTodayReportWithOwner(userOwner);
    }


    /**
     * This method create report on update
     *
     * @param chatOwner is not null
     * @param diet
     * @param feeling
     * @param behavior
     * @param idMedia
     * @return report
     */
    public Report updateReportUserWithChatOwnerAndCurrentShelter(Chat chatOwner,
                                                                 Shelter shelter,
                                                                 String diet,
                                                                 String feeling,
                                                                 String behavior,
                                                                 String idMedia) {
        User userOwner = getUserFromChatAndShelter(chatOwner, shelter);
        return animalOwnershipService.updateReportWithOwner(userOwner, diet, feeling, behavior, idMedia);
    }


    /**
     * This method allows get report, open and not approve
     *
     * @return report open and not approve
     */
    public Report getOpenAndNotApproveReportWithShelter(Shelter shelter) {
        return animalOwnershipService.getOpenAndNotApproveReportWithShelter(shelter);
    }

    /**
     * This method allows you to approve the owner report
     *
     * @param idReport is not null
     * @param approve  try
     * @return report
     */
    public Report approveReportWithIdReportWithShelter(Shelter shelter, Long idReport, boolean approve) {
        User userOwner = userRepository.getUserOwnerReportWithShelter(idReport, shelter.getId());
        if (userOwner == null || userOwner.getShelter() == null || !userOwner.getShelter().equals(shelter)) {
            return null;
        }
        return animalOwnershipService.approveReportWithShelter(idReport, shelter, approve);
    }

    /**
     * Method to Get One Non-Approved Open Possession of an Animal and Take the animals Away
     *
     * @return {@link AnimalOwnershipService#getOneNotApproveOpenAnimalOwnershipWithNotTrialWithShelter(Shelter)}
     */
    public AnimalOwnership getOneNotApproveOpenAnimalOwnershipWithNotTrialWithShelter(Shelter shelter) {
        return animalOwnershipService.getOneNotApproveOpenAnimalOwnershipWithNotTrialWithShelter(shelter);
    }

    /**
     * This method allows you to approve the owner of the animal
     *
     * @param idAnimalOwnership is not null
     * @param approve           is not null
     * @return
     */
    public AnimalOwnership approveAnimalOwnershipWithShelter(Shelter shelter, Long idAnimalOwnership, boolean approve) {
        return animalOwnershipService.approveAnimalOwnershipWithShelter(shelter, idAnimalOwnership, approve);
    }

    /**
     * This method allows you to extend the trial ownership of the animal
     *
     * @param idAnimalOwnership is not null
     * @return {@link AnimalOwnershipService#extendTrialAnimalOwnershipWithShelter(Shelter, Long, int)}
     */
    public AnimalOwnership extendAnimalOwnershipWithShelter(Shelter shelter, Long idAnimalOwnership, int countDays) {
        return animalOwnershipService.extendTrialAnimalOwnershipWithShelter(shelter, idAnimalOwnership, countDays);
    }

}
