package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.*;
import pro.sky.animalshelter4.entityDto.ShelterDto;
import pro.sky.animalshelter4.exception.ChatDontHaveShelterIndex;
import pro.sky.animalshelter4.exception.ShelterNotFoundException;
import pro.sky.animalshelter4.info.*;
import pro.sky.animalshelter4.repository.ShelterRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is necessary to manage actions with Shelters
 * The class must have many dependencies so that it can work correctly.
 * And also respond to requests received from {@link ShelterRepository}
 */
@Service
public class ShelterService {

    private final ShelterRepository shelterRepository;
    private final DtoMapperService dtoMapperService;
    private final AnimalService animalService;
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ShelterService.class);


    public ShelterService(ShelterRepository shelterRepository, DtoMapperService dtoMapperService, AnimalService animalService, UserService userService) {
        this.shelterRepository = shelterRepository;
        this.dtoMapperService = dtoMapperService;
        this.animalService = animalService;
        this.userService = userService;
    }

    /**
     * This method using method repository, allows create AnimalTypeDto
     *
     * @param shelterDto is not null
     * @return AnimalTypeDto
     */
    public ShelterDto createShelter(ShelterDto shelterDto) {
        logger.info("Method createShelter was start for create new Shelter");
        return dtoMapperService.toDto(shelterRepository.save(dtoMapperService.toEntity(shelterDto)));
    }

    /**
     * This method using method repository, allows read AnimalTypeDto
     *
     * @param id is not null
     * @return AnimalTypeDto
     */
    public ShelterDto readShelter(Long id) {
        logger.info("Method readShelter was start for find Shelter by id");
        return dtoMapperService.toDto(
                shelterRepository.findById(id).
                        orElseThrow(() -> new ShelterNotFoundException(String.valueOf(id))));
    }

    /**
     * This method using method repository, allows get all AnimalTypeDto
     *
     * @return List<AnimalTypeDto>
     */
    public List<ShelterDto> getAllDto() {
        logger.info("Method getAllDto was start for get all Shelter");
        return shelterRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public ShelterDto deleteShelter(Long id) {
        Shelter Shelter = new Shelter();
        Shelter.setId(id);
        return dtoMapperService.toDto(deleteShelter(Shelter));
    }

    public Shelter deleteShelter(Shelter shelter) {
        logger.info("Method deleteShelter was start for delete Shelter");
        if (shelter.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of Shelter");
        }
        Shelter shelterFound = shelterRepository.findById(shelter.getId()).
                orElseThrow(() -> new ShelterNotFoundException(String.valueOf(shelter.getId())));
        shelterRepository.delete(shelterFound);
        return shelterFound;
    }


    public Shelter findShelter(Long id) {
        logger.info("Method findShelter was start for find Shelter by id");
        return shelterRepository.findById(id).
                orElseThrow(() -> new ShelterNotFoundException(String.valueOf(id)));
    }

    public Shelter findShelter(String shelterDesignation) {
        logger.info("Method findShelter was start for find Shelter by nameType");
        if (shelterDesignation == null) {
            return null;
        }
        return shelterRepository.getShelterByshelterDesignation(shelterDesignation).
                orElseThrow(()->new ShelterNotFoundException(shelterDesignation));
    }

    public List<Shelter> getAll() {
        return shelterRepository.findAll();
    }

    public List<String> getAllshelterDesignation() {
        return shelterRepository.getAllshelterDesignation();
    }

    private Shelter getShelterOfChat(Chat chat) {
        if (chat == null) {
            throw new IllegalArgumentException();
        }
        if (chat.getShelter() == null || chat.getShelter().getId() == null) {
            throw new ChatDontHaveShelterIndex(chat.getId().toString());
        }
        return findShelter(chat.getShelter().getId());
    }

    public boolean isUserWithTelegramChatIdVolunteerInCurrentShelter(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        return userService.isUserWithTelegramChatIdVolunteerInCurrentShelter(chat, shelter);

    }

    public boolean isUserWithTelegramChatIdOwnerInCurrentShelter(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        return userService.isUserWithTelegramChatIdOwnerInCurrentShelter(chat, shelter);
    }

    public User changeUserPhoneInCurrentShelter(Chat chat, String phone) {
        Shelter shelter = getShelterOfChat(chat);
        return userService.changePhoneUser(chat, shelter, phone);
    }

    public CallRequest createCallRequestForClientWithChatWithRandomVolunteerInCurrentShelter(Chat chatClient) {
        Shelter shelterOfClient = getShelterOfChat(chatClient);
        return userService.createCallRequestClientWithChatAndShelterWithRandomVolunteer(chatClient, shelterOfClient);
    }

    public CallRequest getOpenCallRequestForVolunteerWithChatInCurrentShelter(Chat chatVolunteer) {
        Shelter shelterOfClient = getShelterOfChat(chatVolunteer);
        return userService.getOpenCallRequestForVolunteerWithChatAndShelter(chatVolunteer, shelterOfClient);
    }

    public CallRequest closeCallRequestForVolunteerWithChatInCurrentShelter(Chat chatVolunteer, Long idCallRequest) {
        Shelter shelter = getShelterOfChat(chatVolunteer);
        return userService.closeCallRequestForVolunteerWithChatInShelter(chatVolunteer, shelter, idCallRequest);
    }


    public Animal addAnimalInCurrentShelter(Chat chatVolunteer, String name) {
        Shelter shelter = getShelterOfChat(chatVolunteer);
        return animalService.addAnimalWithShelter(name, shelter);
    }

    public Report getTodayReportOwnerWithChatAndCurrentShelter(Chat chatOwner) {
        Shelter shelterOwner = getShelterOfChat(chatOwner);
        return userService.findOrCreateTodayReportOwnerWithShelter(chatOwner, shelterOwner);
    }

    /**
     * This method using method repository, allows update  AnimalTypeDto
     *
     * @param shelterDto is not null
     * @return AnimalTypeDto
     */
    public ShelterDto updateShelter(ShelterDto shelterDto) {
        logger.info("Method updateShelter was start for update Shelter");
        Shelter newShelter = dtoMapperService.toEntity(shelterDto);
        Shelter oldShelter = findShelter(newShelter.getId());
        if (oldShelter == null) {
            throw new ShelterNotFoundException(String.valueOf(newShelter.getId()));
        }
        oldShelter.setNameShelter(newShelter.getNameShelter());
        oldShelter.setAddress(newShelter.getAddress());
        oldShelter.setPhone(newShelter.getPhone());
        oldShelter.setshelterDesignation(newShelter.getshelterDesignation());
        return dtoMapperService.toDto(shelterRepository.save(oldShelter));
    }

    /**
     * This method using method repository, allows del AnimalTypeDto
     * @param chatOwner is not null
     * @param diet
     * @param feeling
     * @param behavior
     * @param idMedia
     * @return AnimalTypeDto
     */
    public Report updateReportWithChatOwnerAndCurrentShelter(
            Chat chatOwner,
            String diet,
            String feeling,
            String behavior,
            String idMedia) {
        Shelter shelterOwner = getShelterOfChat(chatOwner);
        return userService.updateReportUserWithChatOwnerAndCurrentShelter(
                chatOwner,
                shelterOwner,
                diet,
                feeling,
                behavior,
                idMedia);
    }

    /**
     * This method using method repository, allows del AnimalType
     *
     * @param chatVolunteer is not null
     * @param idReport is not null
     * @param approve is not null
     * @return AnimalType
     */

    public Report approveReportWithIdReportAndChatVolunteerCurrentShelter(Chat chatVolunteer, Long idReport, boolean approve) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.approveReportWithIdReportWithShelter(shelterVolunteer, idReport, approve);
    }

    public Report getOpenAndNotApproveReportWithCurrentShelter(Chat chatVolunteer) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.getOpenAndNotApproveReportWithShelter(shelterVolunteer);
    }

    public List<User> getAllClientsEntityWithCurrentShelter(Chat chatVolunteer) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.getAllClientsEntityWithShelter(shelterVolunteer);
    }

    /**
     * This method using method repository, allows find AnimalType
     *
     * @param chatVolunteer is not null
     * @return AnimalType
     */
    public List<Animal> getAllNotBusyAnimalsWithCurrentShelter(Chat chatVolunteer) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return animalService.getAllNotBusyAnimalsWithShelter(shelterVolunteer);
    }

    /**
     * This method using method repository, allows find AnimalType
     *
     * @param chatVolunteer is not null
     * @param idUserClient is not null
     * @param idAnimal is not null
     * @return AnimalType
     */

    public AnimalOwnership createOwnershipWithClientAndAnimal(Chat chatVolunteer, Long idUserClient, Long idAnimal) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        Animal animal = animalService.findAnimalWithIdNotBusyWithShelter(idAnimal,shelterVolunteer);
        if (animal == null || !shelterVolunteer.getId().equals(animal.getShelter().getId())) {
            return null;
        }
        return userService.createOwnershipAnimalWithShelter(idUserClient, shelterVolunteer, animal);
    }

    public void changeUserDateLastNotificationToNow(User owner) {
        userService.changeUserDateLastNotificationToNow(owner);
    }

    public User getRandomVolunteerWithShelter(Shelter shelter) {
        Shelter shelterVolunteer = findShelter(shelter.getId());
        if (shelterVolunteer == null) {
            throw new ShelterNotFoundException("");
        }
        return userService.getRandomVolunteer(shelterVolunteer);
    }

    public AnimalOwnership getOneNotApproveOpenAnimalOwnershipWithNotTrialWithCurrentShelter(Chat chatVolunteer) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.getOneNotApproveOpenAnimalOwnershipWithNotTrialWithShelter(shelterVolunteer);
    }

    public AnimalOwnership approveAnimalOwnershipWithCurrentShelter(Chat chatVolunteer, Long idAnimalOwnership, boolean approve) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.approveAnimalOwnershipWithShelter(shelterVolunteer, idAnimalOwnership, approve);

    }

    public AnimalOwnership extendAnimalOwnershipWithCurrentShelter(Chat chatVolunteer, Long idAnimalOwnership, int countDays) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.extendAnimalOwnershipWithShelter(shelterVolunteer, idAnimalOwnership, countDays);
    }

    public String getInfoDogsWithDisabilities(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoDogsWithDisabilities.getInfoEn();
            case "CAT":
                return InfoCatWithDisabilities.getInfoEn();
            default:
                return InfoDogsWithDisabilities.getInfoEn();
        }
    }

        public String getInfoListOfDocuments(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoListOfDocuments.getInfoEn();
            case "CAT":
                return InfoListOfDocumentsCat.getInfoEn();
            default:
                return InfoListOfDocuments.getInfoEn();
        }
    }

        public String getInfoRecommendationsHomeDog(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoRecommendationsHomeDog.getInfoEn();
            case "CAT":
                return InfoRecommendationsHomeCat.getInfoEn();
            default:
                return InfoRecommendationsHomeDog.getInfoEn();
        }
    }

        public String getInfoRecommendationsHomeSmallDog(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoRecommendationsHomeSmallDog.getInfoEn();
            case "CAT":
                return InfoRecommendationsHomeSmallCat.getInfoEn();
            default:
                return InfoRecommendationsHomeSmallDog.getInfoEn();
        }
    }

        public String getInfoRefuseDogFromShelter(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoRefuseDogFromShelter.getInfoEn();
            case "CAT":
                return "";
            default:
                return InfoRefuseDogFromShelter.getInfoEn();
        }
    }

        public String getInfoTipsFromDogHandler(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoTipsFromDogHandler.getInfoEn();
            case "CAT":
                return "";
            default:
                return InfoTipsFromDogHandler.getInfoEn();
        }
    }

        public String getInfoTransportationAnimals(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoTransportationAnimals.getInfoEn();
            case "CAT":
                return "";
            default:
                return InfoTransportationAnimals.getInfoEn();
        }
    }

        public String getInfoWhyDoYouNeedDogHandler(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoWhyDoYouNeedDogHandler.getInfoEn();
            case "CAT":
                return "";
            default:
                return InfoWhyDoYouNeedDogHandler.getInfoEn();
        }
    }

        public String getInfoGettingKnowDog(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoGettingKnowDog.getInfoEn();
            case "CAT":
                return InfoGettingKnowCat.getInfoEn();
            default:
                return InfoGettingKnowDog.getInfoEn();
        }
    }

        public String getInfoAboutShelter(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoAboutShelterDog.getInfoEn();
            case "CAT":
                return InfoAboutShelterCat.getInfoEn();
            default:
                return InfoAboutShelterDog.getInfoEn();
        }
    }

    /**
     * This method using method repository, allows get all AnimalType
     *
     * @return  List<AnimalType>
     */

        public String getInfoTakeADog(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        switch (shelter.getshelterDesignation()) {
            case "DOG":
                return InfoTakeADog.getInfoEn();
            case "CAT":
                return "";
            default:
                return InfoTakeADog.getInfoEn();
        }
    }

    @PostConstruct
    private void addDogAndCatShelter() {
            List<Shelter> shelterList = shelterRepository.findAll();
        if (shelterList.size() == 0) {
            Shelter shelterDog = new Shelter();
            shelterDog.setNameShelter("Shelter of dog");
            shelterDog.setPhone("123456789");
            shelterDog.setAddress("123 123 123");
            shelterDog.setshelterDesignation("DOG");
            Shelter shelterCat = new Shelter();
            shelterCat.setNameShelter("Shelter of cat");
            shelterCat.setPhone("987654321");
            shelterCat.setAddress("456 4564 45");
            shelterCat.setshelterDesignation("CAT");
            shelterRepository.save(shelterDog);
            shelterRepository.save(shelterCat);
        }
    }

}
