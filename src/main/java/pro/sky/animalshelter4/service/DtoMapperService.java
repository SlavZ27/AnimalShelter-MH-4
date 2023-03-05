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
    private final ShelterRepository shelterRepository;
    private final AnimalOwnershipRepository animalOwnershipRepository;
    private final PhotoRepository photoRepository;

    public DtoMapperService(UserRepository userRepository, ChatRepository chatRepository, AnimalRepository animalRepository, ShelterRepository shelterRepository, AnimalOwnershipRepository animalOwnershipRepository, PhotoRepository photoRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.animalRepository = animalRepository;
        this.shelterRepository = shelterRepository;
        this.animalOwnershipRepository = animalOwnershipRepository;
        this.photoRepository = photoRepository;
    }


    public Chat toEntity(ChatDto chatDto) {
        Chat chat = new Chat();
        chat.setId(chatDto.getId());
        chat.setUserNameTelegram(chatDto.getUserNameTelegram());
        chat.setFirstNameUser(chatDto.getFirstNameUser());
        chat.setLastNameUser(chatDto.getLastNameUser());
        chat.setLastActivity(chatDto.getLastActivity());
        return chat;
    }


    public ChatDto toDto(Chat chat) {
        ChatDto chatDto = new ChatDto();
        chatDto.setId(chat.getId());
        chatDto.setUserNameTelegram(chat.getUserNameTelegram());
        chatDto.setFirstNameUser(chat.getFirstNameUser());
        chatDto.setLastNameUser(chat.getLastNameUser());
        chatDto.setLastActivity(chat.getLastActivity());
        return chatDto;
    }


    public CallRequest toEntity(CallRequestDto callRequestDto, String shelterDesignation) {
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
        if (shelterDesignation == null) {
            throw new IllegalArgumentException();
        } else {
            Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                    new ShelterNotFoundException(shelterDesignation));
            callRequest.setShelter(shelter);
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


    public User toEntity(UserDto userDto, String shelterDesignation) {
        User user = new User();
        user.setId(userDto.getId());
        user.setNameUser(userDto.getNameUser());
        if (userDto.getIdChat() != null) {
            Chat chat = chatRepository.
                    findById(userDto.getIdChat()).
                    orElseThrow(() -> new ChatNotFoundException(String.valueOf(userDto.getIdChat())));
            user.setChatTelegram(chat);
        }
        if (shelterDesignation == null) {
            throw new IllegalArgumentException();
        } else {
            Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                    new ShelterNotFoundException(shelterDesignation));
            user.setShelter(shelter);
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


    public AnimalOwnership toEntity(AnimalOwnershipDto animalOwnershipDto, String shelterDesignation) {
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
        if (shelterDesignation == null) {
            throw new IllegalArgumentException();
        } else {
            Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                    new ShelterNotFoundException(shelterDesignation));
            animalOwnership.setShelter(shelter);
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

    public Shelter toEntity(ShelterDto shelterDto) {
        Shelter shelter = new Shelter();
        shelter.setId(shelterDto.getId());
        shelter.setshelterDesignation(shelterDto.getshelterDesignation());
        shelter.setNameShelter(shelterDto.getNameShelter());
        shelter.setAddress(shelterDto.getAddress());
        shelter.setPhone(shelterDto.getPhone());
        return shelter;
    }

    public ShelterDto toDto(Shelter shelter) {
        ShelterDto shelterDto = new ShelterDto();
        shelterDto.setId(shelter.getId());
        shelterDto.setshelterDesignation(shelter.getshelterDesignation());
        shelterDto.setNameShelter(shelter.getNameShelter());
        shelterDto.setAddress(shelter.getAddress());
        shelterDto.setPhone(shelter.getPhone());
        return shelterDto;
    }


    public Animal toEntity(AnimalDto animalDto, String shelterDesignation) {
        Animal animal = new Animal();
        animal.setId(animalDto.getId());
        animal.setNameAnimal(animalDto.getNameAnimal());
        animal.setBorn(animalDto.getBorn());
        if (shelterDesignation == null) {
            throw new IllegalArgumentException();
        } else {
            Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                    new ShelterNotFoundException(shelterDesignation));
            animal.setShelter(shelter);
        }
        return animal;
    }

    public AnimalDto toDto(Animal animal) {
        AnimalDto animalDto = new AnimalDto();
        animalDto.setId(animal.getId());
        animalDto.setNameAnimal(animal.getNameAnimal());
        animalDto.setBorn(animal.getBorn());
        return animalDto;
    }

    public Report toEntity(ReportDto reportDto, String shelterDesignation) {
        Report report = new Report();
        report.setId(reportDto.getId());
        if (reportDto.getIdAnimalOwnership() != null) {
            AnimalOwnership animalOwnership = animalOwnershipRepository.findById(reportDto.getIdAnimalOwnership()).
                    orElseThrow(() -> new AnimalOwnershipNotFoundException(reportDto.getIdAnimalOwnership().toString()));
            report.setAnimalOwnership(animalOwnership);
        }
        if (shelterDesignation == null) {
            throw new IllegalArgumentException();
        } else {
            Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(() ->
                    new ShelterNotFoundException(shelterDesignation));
            report.setShelter(shelter);
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
