### [CallRequestService]
* This class is needed to send requests for 
* communication with the volunteer
* The class must have many dependencies so that
* it can work correctly.
* As well as respond to requests received from 
* [TelegramBotSenderService](./documentation/TelegramBotSenderService.md)



public void process(UpdateDPO updateDpo)
This method handles requests received from TelegrammBotSenderServes.
* They will determine which response to the command to send if the volunteer is on site or not.
* In addition, the method outputs a message  Telegram Bot Sender Service }
* As well as methods from the following classes Call Request Service method sendNotificationAboutAllCallRequestToTelegram(User)}
Chat Service method getChatByIdOrNew(Long).
* Method from repository  Call Request Repository metod get First Open By User Id For Client(Long).

The class implements several commands with messages.

| Name    | Type        | Message |
|---------|-------------|------------|
|MESSAGE_VOLUNTEERS_IS_ABSENT | Stringe | Sorry. All volunteers is absent     |
|MESSAGE_OK_VOLUNTEERS_FOUND | String | OK. Volunteer will call you  |

__param: updateDpo__ 

#### public void sendNotificationAboutAllCallRequestToTelegram(User user) 

* This method sends all the call requests that are 
available to the volunteer.
* The method from telegrammBotSendlerServes 
TelegramBotSenderService metod sendMessage(Long, String)
* The request list must be greater than 0.

The class implements several commands with messages.

| Name    | Type        | Message |
|---------|-------------|------------|
|MESSAGE_ABOUT_CALL_REQUEST | String | You have call request by|

#### public List<CallRequest> getAllOpenByClient(Long idUser)
* This method outputs all the Hat_ids to the volunteer.
* Using the repository method CallRequestRepository 
metod getAllOpenByUserIdForClient(Long)

| Name   | Type | Description   |
|--------|------|---------------|
| idUser | Long | this is the user id  |

Return:  get All Open By Client in id User;

All other queries use the repository to retrieve or import data from the knowledge base via commands.


