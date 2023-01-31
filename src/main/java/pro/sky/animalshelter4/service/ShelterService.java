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
     * This method using method repository, allows create ShelterDto
     *
     * @param shelterDto is not null
     * @return ShelterDto
     */
    public ShelterDto createShelter(ShelterDto shelterDto) {
        logger.info("Method createShelter was start for create new Shelter");
        return dtoMapperService.toDto(shelterRepository.save(dtoMapperService.toEntity(shelterDto)));
    }

    /**
     * This method using method repository, allows read ShelterDto
     *
     * @param id is not null
     * @return ShelterDto
     */
    public ShelterDto readShelter(Long id) {
        logger.info("Method readShelter was start for find Shelter by id");
        return dtoMapperService.toDto(
                shelterRepository.findById(id).
                        orElseThrow(() -> new ShelterNotFoundException(String.valueOf(id))));
    }

    /**
     * This method using method repository, allows get all ShelterDto
     *
     * @return List<ShelterDto>
     */
    public List<ShelterDto> getAllDto() {
        logger.info("Method getAllDto was start for get all Shelter");
        return shelterRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * This method using method repository, allows del ShelterDto
     *
     * @param id is not null
     * @return ShelterDto
     */
    public ShelterDto deleteShelter(Long id) {
        Shelter Shelter = new Shelter();
        Shelter.setId(id);
        return dtoMapperService.toDto(deleteShelter(Shelter));
    }

    /**
     * This method delete Shelter uses method repository
     * @param shelter is not null
     * @return delete found shelter
     */
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


    /**
     * This method find Shelter uses method repository
     * @param id is not null
     * @return Shelter
     */
    public Shelter findShelter(Long id) {
        logger.info("Method findShelter was start for find Shelter by id");
        return shelterRepository.findById(id).
                orElseThrow(() -> new ShelterNotFoundException(String.valueOf(id)));
    }

    /**
     * This method find Shelter uses method repository
     * @param shelterDesignation is not null
     * @return Shelter
     */
    public Shelter findShelter(String shelterDesignation) {
        logger.info("Method findShelter was start for find Shelter by nameType");
        if (shelterDesignation == null) {
            return null;
        }
        return shelterRepository.getShelterByshelterDesignation(shelterDesignation).
                orElseThrow(()->new ShelterNotFoundException(shelterDesignation));
    }

    /**
     * This method find all Shelter uses method repository
     * @return List<Shelter>
     */
    public List<Shelter> getAll() {
        return shelterRepository.findAll();
    }

    /**
     * This method find all Shelter designation uses method repository
     * @return List<String>
     */
    public List<String> getAllshelterDesignation() {
        return shelterRepository.getAllshelterDesignation();
    }

    /**
     * This method allows get shelter of chat
     * @param chat is not null
     * @return Shelter
     */
    private Shelter getShelterOfChat(Chat chat) {
        if (chat == null) {
            throw new IllegalArgumentException();
        }
        if (chat.getShelter() == null || chat.getShelter().getId() == null) {
            throw new ChatDontHaveShelterIndex(chat.getId().toString());
        }
        return findShelter(chat.getShelter().getId());
    }

    /**
     * This method allows learn is user with telegram chat id volunteer in current shelter
     * @param chat is not null
     * @return userService.isUserWithTelegramChatIdVolunteerInCurrentShelter(chat, shelter);
     */
    public boolean isUserWithTelegramChatIdVolunteerInCurrentShelter(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        return userService.isUserWithTelegramChatIdVolunteerInCurrentShelter(chat, shelter);

    }

    /**
     * This method allows learn is user with telegram chat id volunteer in current shelter
     * @param chat is not null
     * @return userService.isUserWithTelegramChatIdOwnerInCurrentShelter(chat, shelter);
     */
    public boolean isUserWithTelegramChatIdOwnerInCurrentShelter(Chat chat) {
        Shelter shelter = getShelterOfChat(chat);
        return userService.isUserWithTelegramChatIdOwnerInCurrentShelter(chat, shelter);
    }

    /**
     * This method, uses method repository, allows change user phone in current shelter
     * @param chat is  not null
     * @param phone is not null
     * @return userService.changePhoneUser(chat, shelter, phone);
     */
    public User changeUserPhoneInCurrentShelter(Chat chat, String phone) {
        Shelter shelter = getShelterOfChat(chat);
        return userService.changePhoneUser(chat, shelter, phone);
    }

    /**
     * This method, uses method repository,create a call request for a client using a chat with a random volunteer at the current shelter
     * @param chatClient is not null
     * @return userService.createCallRequestClientWithChatAndShelterWithRandomVolunteer(chatClient, shelterOfClient);
     */
    public CallRequest createCallRequestForClientWithChatWithRandomVolunteerInCurrentShelter(Chat chatClient) {
        Shelter shelterOfClient = getShelterOfChat(chatClient);
        return userService.createCallRequestClientWithChatAndShelterWithRandomVolunteer(chatClient, shelterOfClient);
    }

    /**
     * This method, uses method repository, get open call request for volunteer with chat in current shelter
     * @param chatVolunteer is not null
     * @return userService.getOpenCallRequestForVolunteerWithChatAndShelter(chatVolunteer, shelterOfClient)
     */
    public CallRequest getOpenCallRequestForVolunteerWithChatInCurrentShelter(Chat chatVolunteer) {
        Shelter shelterOfClient = getShelterOfChat(chatVolunteer);
        return userService.getOpenCallRequestForVolunteerWithChatAndShelter(chatVolunteer, shelterOfClient);
    }

    /**
     * This method, uses method repository,close call request for volunteer with chat in current shelter
     * @param chatVolunteer is not null
     * @param idCallRequest is not null
     * @return userService.closeCallRequestForVolunteerWithChatInShelter(chatVolunteer, shelter, idCallRequest)
     */
    public CallRequest closeCallRequestForVolunteerWithChatInCurrentShelter(Chat chatVolunteer, Long idCallRequest) {
        Shelter shelter = getShelterOfChat(chatVolunteer);
        return userService.closeCallRequestForVolunteerWithChatInShelter(chatVolunteer, shelter, idCallRequest);
    }


    /**
     * This method, uses method repository, add animal in current shelter
     * @param chatVolunteer is not null
     * @param name is not null
     * @return animalService.addAnimalWithShelter(name, shelter);
     */
    public Animal addAnimalInCurrentShelter(Chat chatVolunteer, String name) {
        Shelter shelter = getShelterOfChat(chatVolunteer);
        return animalService.addAnimalWithShelter(name, shelter);
    }

    /**
     * This method, uses method repository, get today report owner with chat and current shelter
     * @param chatOwner is not  null
     * @return userService.findOrCreateTodayReportOwnerWithShelter(chatOwner, shelterOwner);
     */
    public Report getTodayReportOwnerWithChatAndCurrentShelter(Chat chatOwner) {
        Shelter shelterOwner = getShelterOfChat(chatOwner);
        return userService.findOrCreateTodayReportOwnerWithShelter(chatOwner, shelterOwner);
    }

    /**
     * This method using method repository, allows update  shelterDto
     *
     * @param shelterDto is not null
     * @return dtoMapperService.toDto(shelterRepository.save(oldShelter));
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
     * This method using method repository,allows update report with chat owner and current shelter
     * @param chatOwner is not null
     * @param diet is not null
     * @param feeling is not null
     * @param behavior is not null
     * @param idMedia is not null
     * @return userService.updateReportUserWithChatOwnerAndCurrentShelter
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
     * This method using method repository,allows approve report with id report and chat volunteer current shelter
     * @param chatVolunteer is not null
     * @param idReport is not null
     * @param approve is not null
     * @return userService.approveReportWithIdReportWithShelter(shelterVolunteer, idReport, approve);
     */
    public Report approveReportWithIdReportAndChatVolunteerCurrentShelter(Chat chatVolunteer, Long idReport, boolean approve) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.approveReportWithIdReportWithShelter(shelterVolunteer, idReport, approve);
    }

    /**
     * This method using method repository, get open and not approve report with current shelter
     * @param chatVolunteer is not null
     * @return userService.getOpenAndNotApproveReportWithShelter(shelterVolunteer);
     */
    public Report getOpenAndNotApproveReportWithCurrentShelter(Chat chatVolunteer) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.getOpenAndNotApproveReportWithShelter(shelterVolunteer);
    }

    /**
     * This method using method repository, get all clients entity with current shelter
     * @param chatVolunteer is not null
     * @return userService.getAllClientsEntityWithShelter(shelterVolunteer);
     */
    public List<User> getAllClientsEntityWithCurrentShelter(Chat chatVolunteer) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.getAllClientsEntityWithShelter(shelterVolunteer);
    }

    /**
     * This method using method repository, allows get all not busy animals with current shelter
     *
     * @param chatVolunteer is not null
     * @return animalService.getAllNotBusyAnimalsWithShelter(shelterVolunteer)
     */
    public List<Animal> getAllNotBusyAnimalsWithCurrentShelter(Chat chatVolunteer) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return animalService.getAllNotBusyAnimalsWithShelter(shelterVolunteer);
    }

    /**
     * This method using method repository, create ownership with client and animal
     *
     * @param chatVolunteer is not null
     * @param idUserClient is not null
     * @param idAnimal is not null
     * @return userService.createOwnershipAnimalWithShelter(idUserClient, shelterVolunteer, animal);
     */

    public AnimalOwnership createOwnershipWithClientAndAnimal(Chat chatVolunteer, Long idUserClient, Long idAnimal) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        Animal animal = animalService.findAnimalWithIdNotBusyWithShelter(idAnimal,shelterVolunteer);
        if (animal == null || !shelterVolunteer.getId().equals(animal.getShelter().getId())) {
            return null;
        }
        return userService.createOwnershipAnimalWithShelter(idUserClient, shelterVolunteer, animal);
    }

    /**
     * This method using method repository,change user date last notification to now
     * @param owner is not null
     */
    public void changeUserDateLastNotificationToNow(User owner) {
        userService.changeUserDateLastNotificationToNow(owner);
    }

    /**
     * This method using method repository, get random volunteer with shelter
     * @param shelter is not null
     * @return userService.getRandomVolunteer(shelterVolunteer);
     */
    public User getRandomVolunteerWithShelter(Shelter shelter) {
        Shelter shelterVolunteer = findShelter(shelter.getId());
        if (shelterVolunteer == null) {
            throw new ShelterNotFoundException(shelter.getId().toString());
        }
        return userService.getRandomVolunteer(shelterVolunteer);
    }

    /**
     * This method using method repository, get one not approve open animal ownership with not trial with current shelter
     * @param chatVolunteer is not null
     * @return userService.getOneNotApproveOpenAnimalOwnershipWithNotTrialWithShelter(shelterVolunteer);
     */
    public AnimalOwnership getOneNotApproveOpenAnimalOwnershipWithNotTrialWithCurrentShelter(Chat chatVolunteer) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.getOneNotApproveOpenAnimalOwnershipWithNotTrialWithShelter(shelterVolunteer);
    }

    /**
     * This method using method repository, approve animal ownership with current shelter
     * @param chatVolunteer is not null
     * @param idAnimalOwnership is not null
     * @param approve is not null
     * @return userService.approveAnimalOwnershipWithShelter(shelterVolunteer, idAnimalOwnership, approve);
     */
    public AnimalOwnership approveAnimalOwnershipWithCurrentShelter(Chat chatVolunteer, Long idAnimalOwnership, boolean approve) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.approveAnimalOwnershipWithShelter(shelterVolunteer, idAnimalOwnership, approve);

    }

    /**
     * This method using method repository, extend animal ownership with current shelter
     * @param chatVolunteer is not null
     * @param idAnimalOwnership is not null
     * @param countDays is not null
     * @return userService.extendAnimalOwnershipWithShelter(shelterVolunteer, idAnimalOwnership, countDays);
     */
    public AnimalOwnership extendAnimalOwnershipWithCurrentShelter(Chat chatVolunteer, Long idAnimalOwnership, int countDays) {
        Shelter shelterVolunteer = getShelterOfChat(chatVolunteer);
        return userService.extendAnimalOwnershipWithShelter(shelterVolunteer, idAnimalOwnership, countDays);
    }

    /**
     * This method allows get info dogs with disabilities dog and cat
     * @param chat is not null
     * @return InfoDogsWithDisabilities.getInfoEn();
     * @return InfoCatWithDisabilities.getInfoEn();
     */
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

    /**
     * This method allows get info list of documents  dog and cat
     * @param chat is not null
     * @return InfoListOfDocuments.getInfoEn();
     * @return InfoListOfDocumentsCat.getInfoEn();
     */
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

    /**
     * This method allows get info recommendations home dog and cat
     * @param chat is not null
     * @return InfoRecommendationsHomeDog.getInfoEn();
     * @return InfoRecommendationsHomeCat.getInfoEn();
     */
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

    /**
     * This method allows get info recommendations home small dog and small cat
     * @param chat is not null
     * @return InfoRecommendationsHomeSmallDog.getInfoEn();
     * @return InfoRecommendationsHomeSmallCat.getInfoEn();
     */
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

    /**
     * This method allows get info tips from dog handler dog and cat
     * @param chat is not null
     * @return InfoRefuseDogFromShelter.getInfoEn();
     * @return " "
     */
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

    /**
     * This method allows get info tips from dog handler dog and cat
     *
     * @param chat is not null
     * @return  InfoTipsFromDogHandler.getInfoEn();
     * @return  ""
     */
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

    /**
     * This method allows get info transportation animals dog and cat
     *
     * @param chat is not null
     * @return  InfoWhyDoYouNeedDogHandler.getInfoEn();
     * @return  ""
     */
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

    /**
     * This method allows get info why do you need dog handler dog and cat
     *
     * @param chat is not null
     * @return  InfoWhyDoYouNeedDogHandler.getInfoEn();
     * @return  ""
     */
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

    /**
     * This method allows get info getting a dog and cat
     *
     * @param chat is not null
     * @return  InfoGettingKnowDog.getInfoEn()
     * @return  InfoGettingKnowCat.getInfoEn();
     */
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

    /**
     * This method allows get info take a shelter dog and cat
     *
     * @param chat is not null
     * @return  InfoAboutShelterDog.getInfoEn()
     * @return  InfoAboutShelterDog.getInfoEn()
     */
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
     * This method allows get info take a cat and dog
     *
     * @param chat is not null
     * @return  InfoTakeADog.getInfoEn()
     * @return  InfoTakeADog.getInfoEn()
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

    /**
     * This method using method repository add dog and cat shelter
     * Using{@link ShelterRepository#findAll()}
     * Using{@link ShelterRepository#save(Object)}
     */
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
