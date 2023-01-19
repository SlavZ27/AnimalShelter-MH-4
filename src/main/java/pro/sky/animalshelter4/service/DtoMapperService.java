package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.*;
import pro.sky.animalshelter4.exception.*;
import pro.sky.animalshelter4.repository.*;

/**
 * This class is required for distilling data back and forth.
 * Which are necessary for a more convenient implementation of the project architecture.
 */
@Service
public class DtoMapperService {
    private final Logger logger = LoggerFactory.getLogger(DtoMapperService.class);
    public final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final AnimalRepository animalRepository;
    private final AnimalTypeRepository animalTypeRepository;
    private final AnimalOwnershipRepository animalOwnershipRepository;
    private final PhotoRepository photoRepository;

    public DtoMapperService(UserRepository userRepository,
                            ChatRepository chatRepository, AnimalRepository animalRepository, AnimalTypeRepository animalTypeRepository,
                            AnimalOwnershipRepository animalOwnershipRepository,
                            PhotoRepository photoRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.animalRepository = animalRepository;
        this.animalTypeRepository = animalTypeRepository;
        this.animalOwnershipRepository = animalOwnershipRepository;
        this.photoRepository = photoRepository;
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


    public AnimalOwnership toEntity(AnimalOwnershipDto animalOwnershipDto) {
        AnimalOwnership animalOwnership = new AnimalOwnership();
        animalOwnership.setId(animalOwnershipDto.getId());
        if (animalOwnershipDto.getIdOwner() != null) {
            User user = userRepository.findById(animalOwnershipDto.getIdOwner()).orElseThrow(() ->
                    new UserNotFoundException(animalOwnershipDto.getIdOwner().toString()));
            animalOwnership.setOwner(user);
        }
        if (animalOwnershipDto.getIdAnimal() != null) {
            Animal animal = animalRepository.findById(animalOwnershipDto.getIdAnimal()).orElseThrow(() ->
                    new AnimalNotFoundException(animalOwnershipDto.getIdAnimal().toString()));
            animalOwnership.setAnimal(animal);
        }
        animalOwnership.setDateStartOwn(animalOwnershipDto.getDateStartOwn());
        animalOwnership.setDateEndTrial(animalOwnershipDto.getDateEndTrial());
        animalOwnership.setApprove(animalOwnershipDto.getApprove());
        animalOwnership.setOpen(animalOwnershipDto.isOpen());
        return animalOwnership;
    }


    public AnimalOwnershipDto toDto(AnimalOwnership animalOwnership) {
        AnimalOwnershipDto animalOwnershipDto = new AnimalOwnershipDto();
        animalOwnershipDto.setId(animalOwnership.getId());
        if (animalOwnership.getOwner() != null) {
            animalOwnershipDto.setIdOwner(animalOwnership.getOwner().getId());
        }
        if (animalOwnership.getAnimal() != null) {
            animalOwnershipDto.setIdAnimal(animalOwnership.getAnimal().getId());
        }
        animalOwnershipDto.setDateStartOwn(animalOwnership.getDateStartOwn());
        animalOwnershipDto.setDateEndTrial(animalOwnership.getDateEndTrial());
        animalOwnershipDto.setApprove(animalOwnership.isApprove());
        animalOwnershipDto.setOpen(animalOwnership.isOpen());
        return animalOwnershipDto;
    }

    public AnimalType toEntity(AnimalTypeDto animalTypeDto) {
        AnimalType animalType = new AnimalType();
        animalType.setId(animalTypeDto.getId());
        animalType.setTypeAnimal(animalTypeDto.getTypeAnimal());
        return animalType;
    }

    public AnimalTypeDto toDto(AnimalType animalType) {
        AnimalTypeDto animalTypeDto = new AnimalTypeDto();
        animalTypeDto.setId(animalType.getId());
        animalTypeDto.setTypeAnimal(animalType.getTypeAnimal());
        return animalTypeDto;
    }

    public Animal toEntity(AnimalDto animalDto) {
        Animal animal = new Animal();
        animal.setId(animalDto.getId());
        animal.setNameAnimal(animalDto.getNameAnimal());
        animal.setBorn(animalDto.getBorn());
        if (animalDto.getIdAnimalType() != null) {
            AnimalType animalType = animalTypeRepository.findById(animalDto.getIdAnimalType()).orElseThrow(() ->
                    new AnimalTypeNotFoundException(animalDto.getIdAnimalType().toString()));
            animal.setAnimalType(animalType);
        }
        return animal;
    }

    public AnimalDto toDto(Animal animal) {
        AnimalDto animalDto = new AnimalDto();
        animalDto.setId(animal.getId());
        animalDto.setNameAnimal(animal.getNameAnimal());
        animalDto.setBorn(animal.getBorn());
        animalDto.setIdAnimalType(animal.getAnimalType().getId());
        return animalDto;
    }

    public Report toEntity(ReportDto reportDto) {
        Report report = new Report();

        report.setId(reportDto.getId());

        if (reportDto.getIdAnimalOwnership() != null) {
            AnimalOwnership animalOwnership = animalOwnershipRepository.findById(reportDto.getIdAnimalOwnership()).
                    orElseThrow(() -> new AnimalOwnershipNotFoundException(reportDto.getIdAnimalOwnership().toString()));
            report.setAnimalOwnership(animalOwnership);
        }
        report.setReportDate(reportDto.getReportDate());
        report.setDiet(reportDto.getDiet());
        report.setFeeling(reportDto.getFeeling());
        report.setBehavior(reportDto.getBehavior());
        if (reportDto.getIdPhoto() != null) {
            Photo photo = photoRepository.findById(reportDto.getIdPhoto()).
                    orElseThrow(() -> new PhotoNotFoundException(reportDto.getIdPhoto().toString()));
            report.setPhoto(photo);
        }
        report.setApprove(reportDto.getApprove());
        return report;
    }

    public ReportDto toDto(Report report) {
        ReportDto reportDto = new ReportDto();
        reportDto.setId(report.getId());
        if (report.getAnimalOwnership() != null) {
            reportDto.setIdAnimalOwnership(report.getAnimalOwnership().getId());
        }
        reportDto.setReportDate(report.getReportDate());
        reportDto.setDiet(report.getDiet());
        reportDto.setFeeling(report.getFeeling());
        reportDto.setBehavior(report.getBehavior());
        if (report.getPhoto() != null) {
            reportDto.setIdPhoto(report.getPhoto().getId());
            reportDto.setLinkPhoto("http://localhost:8080/photo/" + report.getPhoto().getId());
        }
        reportDto.setApprove(report.isApprove());
        return reportDto;
    }

}
