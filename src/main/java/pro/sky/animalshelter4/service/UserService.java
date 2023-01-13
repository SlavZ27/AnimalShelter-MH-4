package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.entityDto.UserDto;
import pro.sky.animalshelter4.exception.UserNotFoundException;
import pro.sky.animalshelter4.repository.UserRepository;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final DtoMapperService dtoMapperService;
    private final Random random = new Random();

    public UserService(UserRepository userRepository, DtoMapperService dtoMapperService) {
        this.userRepository = userRepository;
        this.dtoMapperService = dtoMapperService;
    }


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

    public List<UserDto> getAllClients() {
        logger.info("Method getAllClients was start for return all Users of Clients");
        return userRepository.getAllClients().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }
    
    public boolean isUserWithTelegramChatIdVolunteer(Long idChatTelegram) {
        logger.info("Method isUserOfVolunteer was start for to check if the User with id = {} is a volunteer", idChatTelegram);
        User user = userRepository.getByIdTelegramChatAndVolunteer(idChatTelegram);
        if (user!=null) {
            logger.debug("Method isVolunteer don't detected volunteer by idUser = {}", idChatTelegram);
            return true;
        }
        logger.debug("Method isVolunteer detected volunteer by idUser = {}", idChatTelegram);
        return false;
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
    
    
}
