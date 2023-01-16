package pro.sky.animalshelter4.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.animalshelter4.entity.CallRequest;
import pro.sky.animalshelter4.entity.User;
import pro.sky.animalshelter4.entityDto.CallRequestDto;
import pro.sky.animalshelter4.exception.CallRequestNotFoundException;
import pro.sky.animalshelter4.exception.CantCloseCallRequestException;
import pro.sky.animalshelter4.repository.CallRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public CallRequestService(CallRequestRepository callRequestRepository, DtoMapperService dtoMapperService) {
        this.callRequestRepository = callRequestRepository;
        this.dtoMapperService = dtoMapperService;
    }

    /**
     * This method handles requests received from TelegrammBotSenderServes.
     * They will determine which response to the command to send if the volunteer is on site or not.
     * In addition, the method outputs a message {@link TelegramBotSenderService#sendMessage }
     * And {@link ChatService#getChatByIdOrNew(Long)}
     * Method from repository {@link CallRequestRepository#getFirstOpenByUserIdForClient(Long)}
     *
     * @param
     */
    public CallRequest createCallRequest(User userClient, User userVolunteer) {
        CallRequest callRequest = callRequestRepository.getFirstOpenByUserIdForClient(userClient.getId());
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

    public void closeCallRequest(User user, Long idCallRequest) {
        CallRequest callRequest = findCallRequest(idCallRequest);
        if (callRequest == null) {
            throw new CallRequestNotFoundException(idCallRequest.toString());
        }
        if (!user.getId().equals(callRequest.getVolunteer().getId())) {
            throw new CantCloseCallRequestException(idCallRequest.toString());
        }
        callRequest.setOpen(false);
        callRequest.setLocalDateTimeClose(LocalDateTime.now());
        callRequestRepository.save(callRequest);
    }

    /**
     * This method outputs all the Hat_ids to the volunteer.
     * Using the repository method {@link CallRequestRepository#getAllOpenByUserIdForClient(Long)}
     *
     * @param idUser must be not null
     * @return
     */
    public List<CallRequest> getAllOpenByClient(Long idUser) {
        return callRequestRepository.getAllOpenByUserIdForClient(idUser);
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

    public CallRequestDto createCallRequest(CallRequestDto callRequestDto) {
        logger.info("Method createCallRequest was start for create new CallRequest");
        return dtoMapperService.toDto(callRequestRepository.save(dtoMapperService.toEntity(callRequestDto)));
    }

    public CallRequestDto readCallRequest(Long id) {
        logger.info("Method readCallRequest was start for find CallRequest by id");
        return dtoMapperService.toDto(
                callRequestRepository.findById(id).
                        orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(id))));
    }

    public CallRequest findCallRequest(Long id) {
        logger.info("Method findCallRequest was start for find CallRequest by id");
        return callRequestRepository.findById(id).
                orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(id)));
    }

    public CallRequestDto updateCallRequest(CallRequestDto callRequestDto) {
        logger.info("Method updateCallRequest was start for update callRequest");
        CallRequest newCallRequest = dtoMapperService.toEntity(callRequestDto);
        CallRequest oldCallRequest = findCallRequest(newCallRequest.getId());
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

    public CallRequestDto deleteCallRequest(Long id) {
        CallRequest callRequest = new CallRequest();
        callRequest.setId(id);
        return dtoMapperService.toDto(deleteCallRequest(callRequest));
    }

    public CallRequest deleteCallRequest(CallRequest callRequest) {
        logger.info("Method deleteCallRequest was start for delete CallRequest");
        if (callRequest.getId() == null) {
            throw new IllegalArgumentException("Incorrect id of callRequest");
        }
        CallRequest callRequestFound = callRequestRepository.findById(callRequest.getId()).
                orElseThrow(() -> new CallRequestNotFoundException(String.valueOf(callRequest.getId())));
        callRequestRepository.delete(callRequestFound);
        return callRequestFound;
    }

    public List<CallRequestDto> getAll() {
        logger.info("Method getAll was start for return all CallRequest");
        return callRequestRepository.findAll().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<CallRequestDto> getAllOpenCallRequestVolunteer(Long id) {
        logger.info(
                "Method getAllOpenCallRequestVolunteer was start for return all CallRequest Volunteer with id = {}"
                , id);
        return callRequestRepository.getAllOpenByUserIdForVolunteer(id).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<CallRequest> getAllOpenCallRequestVolunteer(User userVolunteer) {
        logger.info(
                "Method getAllOpenCallRequestVolunteer was start for return all CallRequest Volunteer with id = {}"
                , userVolunteer.getId());
        return new ArrayList<>(callRequestRepository.getAllOpenByUserIdForVolunteer(userVolunteer.getId()));
    }

    public List<CallRequestDto> getAllOpenCallRequestClient(Long id) {
        logger.info(
                "Method getAllCallRequestUser was start for return all CallRequest Client with id = {}"
                , id);
        return callRequestRepository.getAllOpenByUserIdForClient(id).stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<CallRequestDto> getAllOpenCallRequest() {
        logger.info("Method getAllOpenCallRequest was start for return all open CallRequest");
        return callRequestRepository.getAllOpenCallRequest().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }

    public List<CallRequestDto> getAllCloseCallRequest() {
        logger.info("Method getAllOpenCallRequest was start for return all close CallRequest");
        return callRequestRepository.getAllCloseCallRequest().stream().
                map(dtoMapperService::toDto).collect(Collectors.toList());
    }
}
