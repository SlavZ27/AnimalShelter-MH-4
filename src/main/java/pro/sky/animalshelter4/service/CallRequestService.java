package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.Shelter;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.exception.CallRequestNotFoundException;
import pro.sky.animalshelter4.exception.CantCloseCallRequestException;
import pro.sky.animalshelter4.exception.ShelterNotFoundException;
import pro.sky.animalshelter4.repository.CallRequestRepository;
import pro.sky.animalshelter4.repository.ShelterRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is needed to send requests for communication with the volunteer
 * The class must have many dependencies so that it can work correctly.
 * As well as respond to requests received from {@link TelegramBotSenderService}
 */

@Service
public class CallRequestService {
    public final static String MESSAGE_YOU_HAVE_CALL_REQUEST = "You have call request by ";
    public final static String MESSAGE_YOU_DONT_HAVE_CALL_REQUEST = "You don't have any call request by ";
    public final static String MESSAGE_SUCCESSFUL_CREATION = "OK. Volunteer will call you";
    public final static String MESSAGE_YOU_CAN_CLOSE_CALL_REQUEST = "Press button with ID for close";
    public final static String MESSAGE_CALL_REQUEST_IS_CLOSE = "Call request closed";
    public final static String MESSAGE_YOU_CANT_CLOSE_CALL_REQUEST = "You can't close call request";
    public final static String MESSAGE_CALL_REQUEST_NOT_FOUND = "Call request not found";
    private final CallRequestRepository callRequestRepository;
    private final DtoMapperService dtoMapperService;

    private final Logger logger = LoggerFactory.getLogger(CallRequestService.class);
    private final ShelterRepository shelterRepository;

    public CallRequestService(CallRequestRepository callRequestRepository, DtoMapperService dtoMapperService,
                              ShelterRepository shelterRepository) {
        this.callRequestRepository = callRequestRepository;
        this.dtoMapperService = dtoMapperService;
        this.shelterRepository = shelterRepository;
    }

    /**
     * This method handles requests received from TelegrammBotSenderServes.
     * They will determine which response to the command to send if the volunteer is on site or not.
     * In addition, the method outputs a message {@link TelegramBotSenderService#sendMessage }
     * Method from repository {@link CallRequestRepository#getFirstOpenByUserIdForClientWithShelter(Long, Long)}
     *
     * @param
     */
    public CallRequest findOpenOrCreateCallRequest(User userClient, User userVolunteer) {
        CallRequest callRequest = callRequestRepository.getFirstOpenByUserIdForClientWithShelter(
                userClient.getId(),
                userClient.getShelter().getId());
        if (callRequest != null) {
            return callRequest;
        } else {
            callRequest = new CallRequest();
            callRequest.setOpen(true);
            callRequest.setLocalDateTimeOpen(LocalDateTime.now());
            callRequest.setClient(userClient);
            callRequest.setVolunteer(userVolunteer);
            return addCallRequest(callRequest);
        }
    }

    /**
     * This method close call request by user and id call request
     *
     * @param user          is not null
     * @param idCallRequest is not null
     */
    public CallRequest closeCallRequestWithShelter(Shelter shelter, User user, Long idCallRequest) {
        CallRequest callRequest = callRequestRepository.getByIdWithShelter(idCallRequest, shelter.getId()).orElse(null);
        if (callRequest == null) {
            throw new CallRequestNotFoundException(idCallRequest.toString());
        }
        if (!user.getId().equals(callRequest.getVolunteer().getId())) {
            throw new CantCloseCallRequestException(idCallRequest.toString());
        }
        callRequest.setOpen(false);
        callRequest.setLocalDateTimeClose(LocalDateTime.now());
        return callRequestRepository.save(callRequest);
    }

    /**
     * This method outputs all the Hat_ids to the volunteer.
     * Using the repository method {@link CallRequestRepository#getAllOpenByUserIdForClientWithShelter(Long, Long)}
     *
     * @param idUser must be not null
     * @return
     */
    public List<CallRequest> getAllOpenByClientWithShelter(Long idUser, Shelter shelter) {
        return callRequestRepository.getAllOpenByUserIdForClientWithShelter(idUser, shelter.getId());
    }

    /**
     * The method saves the call requests.
     * Using the repository metod {@link CallRequestRepository#save(Object)}
     *
     * @param callRequest
     * @return returns a call Request
     */
    public CallRequest addCallRequest(CallRequest callRequest) {
        logger.info("Method addCallRequest was start for create new CallRequest");
        return callRequestRepository.save(callRequest);
    }

    /**
     * This method create call request by callRequestDto
     *
     * @param callRequestDto is not null
     * @return new callRequest
     */
    public CallRequestDto createCallRequest(CallRequestDto callRequestDto, String shelterDesignation) {
        logger.info("Method createCallRequest was start for create new CallRequest");
        return dtoMapperService.toDto(callRequestRepository.save(dtoMapperService.toEntity(
                callRequestDto,
                shelterDesignation)));
    }

    /**
     * This method, using method repository allows you to find a call request
     * Using: {@link CallRequestRepository#findById(Object)}
     *
     * @param id is not null
     * @return CallRequestDto
     * @Exception CallRequestNotFoundException
     */
    public CallRequestDto readCallRequest(Long id, String shelterDesignation) {
        logger.info("Method readCallRequest was start for find CallRequest by id");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(
                () -> new ShelterNotFoundException(shelterDesignation));
        return dtoMapperService.toDto(
                callRequestRepository.getByIdWithShelter(id, shelter.getId()).
                        orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(id))));
    }

    /**
     * This method, using method repository allows you to find a call request
     * Using: {@link CallRequestRepository#findById(Object)}
     *
     * @param id is not null
     * @return CallRequestDto
     * @Exception CallRequestNotFoundException
     */
    public CallRequest findCallRequest(Long id, Shelter shelter) {
        logger.info("Method findCallRequest was start for find CallRequest by id");
        return callRequestRepository.getByIdWithShelter(id, shelter.getId()).
                orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(id)));
    }

    /**
     * This method,using method repository update call request by callRequestDto
     * Using: {@link DtoMapperService#toEntity(CallRequestDto, String)}
     * Using: {@link CallRequestRepository#save(Object)}
     *
     * @param callRequestDto is not null
     * @return CallRequestDto
     */
    public CallRequestDto updateCallRequest(CallRequestDto callRequestDto, String shelterDesignation) {
        logger.info("Method updateCallRequest was start for update callRequest");
        CallRequest newCallRequest = dtoMapperService.toEntity(callRequestDto, shelterDesignation);
        CallRequest oldCallRequest = findCallRequest(newCallRequest.getId(), newCallRequest.getShelter());
        if (oldCallRequest == null) {
            throw new CallRequestNotFoundException(String.valueOf(newCallRequest.getId()));
        }
        oldCallRequest.setOpen(newCallRequest.isOpen());
        oldCallRequest.setVolunteer(newCallRequest.getVolunteer());
        oldCallRequest.setClient(newCallRequest.getClient());
        oldCallRequest.setLocalDateTimeOpen(newCallRequest.getLocalDateTimeOpen());
        oldCallRequest.setLocalDateTimeClose(newCallRequest.getLocalDateTimeClose());
        return dtoMapperService.toDto(callRequestRepository.save(oldCallRequest));
    }

    /**
     * This method, using method class to delete call request by id
     * Using: {@link CallRequestRepository#save(Object)}
     *
     * @param id is not null
     * @return del CallRequestDto
     */
    public CallRequestDto deleteCallRequest(Long id, String shelterDesignation) {
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(
                () -> new ShelterNotFoundException(shelterDesignation));
        CallRequest callRequest = new CallRequest();
        callRequest.setId(id);
        return dtoMapperService.toDto(deleteCallRequest(callRequest, shelter));
    }

    /**
     * This method finds the call request that needs to be deleted, using method repository
     * Using: {@link CallRequestRepository#findById(Object)}
     *
     * @param callRequest is not null
     * @return callRequestFound
     * @Exception IllegalArgumentException, CallRequestNotFoundException
     */
    public CallRequest deleteCallRequest(CallRequest callRequest, Shelter shelter) {
        logger.info("Method deleteCallRequest was start for delete CallRequest");
        if (callRequest.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of callRequest");
        }
        CallRequest callRequestFound = callRequestRepository.getByIdWithShelter(
                        callRequest.getId(),
                        shelter.getId()).
                orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(callRequest.getId())));
        callRequestRepository.delete(callRequestFound);
        return callRequestFound;
    }


    /**
     * This method, using method repository allow get all open call request volunter
     * Using: {@link CallRequestRepository#getAllOpenByUserIdForVolunteerWithShelter(Long, Long)}
     *
     * @return List<CallRequestDto>
     */
    public List<CallRequestDto> getAllOpenCallRequestVolunteer(Long id, String shelterDesignation) {
        logger.info(
                "Method getAllOpenCallRequestVolunteer was start for return all CallRequest Volunteer with id = {}"
                , id);
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(
                () -> new ShelterNotFoundException(shelterDesignation));
        return callRequestRepository.getAllOpenByUserIdForVolunteerWithShelter(id, shelter.getId()).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * This method, using method repository allow get all open call request volunteer
     * Using: {@link CallRequestRepository#getOpenByUserIdForVolunteerWithShelter(Long, Long)}
     *
     * @param userVolunteer is not null
     * @return List<CallRequest>
     */
    public CallRequest getAllOpenCallRequestVolunteer(User userVolunteer, Shelter shelter) {
        logger.info(
                "Method getAllOpenCallRequestVolunteer was start for return all CallRequest Volunteer with id = {}"
                , userVolunteer.getId());
        return callRequestRepository.getOpenByUserIdForVolunteerWithShelter(
                userVolunteer.getShelter().getId(),
                userVolunteer.getId());
    }

    /**
     * This method, using method repository allow receive full call request client by id
     *
     * @param id is not null
     * @return List<CallRequestDto>
     */
    public List<CallRequestDto> getAllOpenCallRequestClient(Long id, String shelterDesignation) {
        logger.info(
                "Method getAllCallRequestUser was start for return all CallRequest Client with id = {}"
                , id);
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(
                () -> new ShelterNotFoundException(shelterDesignation));
        return callRequestRepository.getAllOpenByUserIdForClientWithShelter(
                        id,
                        shelter.getId()).
                stream().map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * This method, using method repository allow get all open call request
     * Using: {@link CallRequestRepository#getAllOpenCallRequestWithShelter(Long)}
     *
     * @return List<CallRequestDto>
     */
    public List<CallRequestDto> getAllOpenCallRequest(String shelterDesignation) {
        logger.info("Method getAllOpenCallRequest was start for return all open CallRequest");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(
                () -> new ShelterNotFoundException(shelterDesignation));
        return callRequestRepository.getAllOpenCallRequestWithShelter(shelter.getId()).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    /**
     * This method, using method repository allow get all close call request
     * Using: {@link CallRequestRepository#getAllOpenCallRequestWithShelter(Long)}
     *
     * @return List<CallRequestDto>
     */
    public List<CallRequestDto> getAllCloseCallRequest(String shelterDesignation) {
        logger.info("Method getAllOpenCallRequest was start for return all close CallRequest");
        Shelter shelter = shelterRepository.getShelterByshelterDesignation(shelterDesignation).orElseThrow(
                () -> new ShelterNotFoundException(shelterDesignation));
        return callRequestRepository.getAllCloseCallRequestWithShelter(shelter.getId()).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }
}
