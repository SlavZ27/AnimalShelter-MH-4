### TelegramBotUpdatesService
* The class deals with the choice of further 
actions depending on the incoming object that came from
* TelegramBotUpdatesListener metod process(List)}.
* The class must have a lot of dependencies,
because other objects receive signals for action from
this class.

#### public void processUpdate(Update update)

* The method deals with the choice of actions depending
    * on the incoming object that came from
  TelegramBotUpdatesListener#process(List)
    * The definition and conversion of a hostile Update
    * to native UpdateDPO is handled by the 
  TelegramMapperService#toDPO(Update).
    * Then, depending on the type of interaction InteractionUnit
    * and command and other parameters, 
  the next action is selected.
    * The method terminates if it detects an Command EMPTY_CALLBACK_DATA_FOR_BUTTON in  Update,
    * or when receiving an unexpected  Update, when null comes from  TelegramMapperService toDPO(Update).

| Name   | Type   | Description                  |
|--------|--------|------------------------------|
| update | Update | Returns command updates |

#### private boolean detectEmptyCommand(Update update)
* The method is needed for detection of the command Command#EMPTY_CALLBACK_DATA_FOR_BUTTON
    * for which the bot will do nothing.
    * The text with this command is located in Update here update.callbackQuery().data()
    * The method should work before laborious parsing of the entire incoming object Update
    * @param update

| Name   | Type   | Description                  |
|--------|--------|------------------------------|
| update | Update | Returns command updates |

  * @return true - if emptyCommand was detected, else false

