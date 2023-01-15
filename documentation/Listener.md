### [TelegramBotUpdatesListener]

*  The class implementing the UpdateListener interface of
   the Penegra library.
* Class engaged in communicating with
  Telegram services.
* As well sending the received list of  Update objects to the  [TelegramBotUpdatesService]
  class for processing.

### public int process(List<Update> updates)

* The method sends the received objects as a list
* to the {@link TelegramBotUpdatesService#processUpdate(Update)} for processing.
* At the end of the processing method {@link TelegramBotUpdatesListener#process(List)},
* objects are marked as processed, despite possible errors.
* @param updates

| Name    | Type        | Description |
|---------|-------------|------------|
|updates | List Update | update     |
Return: All updates that are available.



  
