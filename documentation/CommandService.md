[CommandService.md]
* The class contains methods for working with Command.
* The logic of executing and issuing only
commands available to entity.Chat.
* Depending on the Chat getId()
#### public boolean approveLaunchCommand(Command command, Long idChat)
* The method checks whether the command is available to the entity.Chat
  for execution. The data for the solution is taken from Command.
  The method must be executed after receiving and parsing the necessary data and before executing commands
  using  UserService#isUserWithTelegramChatIdVolunteer(Long)


    Param command must be not null
    Param idChat  must be not null

| Name   | Type    | Description                |
|--------|---------|----------------------------|
| command | Command | class Command on is comand |
| idChat | Long    | the chat id of the user who <br/>logged into the tg bot|

    Return true if the command is available to the user, else false

#### public String getAllTitlesAsListExcludeHide(Long idChat)

The method outputs a string consisting 
of Command getTextCommand() in the form of a list,
depending on the user's rights 
if the id of the volunteer using CommandService 
getAllTextCommandAsListForVolunteerExcludeHide()
if else using CommandService 
getAllTextCommandAsListForClientExcludeHide()
using UserService#isUserWithTelegramChatIdVolunteer(Long)}

    Param:idChat must be not null

| Name   | Type    | Description                |
|--------|---------|----------------------------|
| idChat | Long    | the chat id of the user who <br/>logged into the tg bot|

    Return: String as list of {@link Command#getTextCommand()}


#### public Pair<List<String>, List<String>> getPairListsForButtonExcludeHide(Long idChat)

The method outputs Pair<List<String>, 
List<String>>.First list contain names for the buttons,
second contain data for the buttons.
For example, this use when sending buttons available 
to the user in the method
TelegramBotSenderService#sendButtonsCommandForChat(Long)
if the id of the volunteer using
CommandService 
getListsNameButtonAndListsDataButtonForVolunteerExcludeHide()
if else using CommandService 
getListsNameButtonAndListsDataButtonForClientExcludeHide()
using  UserService isUserWithTelegramChatIdVolunteer(Long)}
  
    Param: idChat must be not null

| Name   | Type    | Description                |
|--------|---------|----------------------------|
| idChat | Long    | the chat id of the user who <br/>logged into the tg bot|

  Return: Pair<List < nameButtons>, List<dataButtons>>


* using {@link Command#getOnlyShowCommandForClient()}

#### private String getAllTextCommandAsListForClientExcludeHide()

    Return The method outputs String consisting of 
    Command getTextCommand() as a list available to
    not volunteer.

#### private String getAllTextCommandAsListForVolunteerExcludeHide()
Using Command getOnlyShowCommandForVolunteer()}

    Return The method outputs String consisting of  
    Command#getTextCommand() as a list available to 
    volunteer.

#### private List<String> getAllTextCommandForClientExcludeHide()

    Command get Only Show Command For Client()
    List of String consisting of Command#getText Command()
    from the list available to a non-volunteer
#### private List<String> getAllTextCommandForVolunteerExcludeHide()

    Using Command get Only Show Command For Volunteer()    
    Return List of String consisting of Command getText 
    Command() from the list available to a volunteer.

#### private Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForClientExcludeHide()

    Return: Pair<List < String>, List<String>> for the client.
    First list contains the names of the buttons Command#getNameButton(),
    second one contains data for buttons Command#getTextCommand()
#### private Pair<List<String>, List<String>> getListsNameButtonAndListsDataButtonForVolunteerExcludeHide() 

    Return Pair<List < String>, List<String>> for the volunteer.
    First list contains the names of the buttons Command#getNameButton(),
    second one contains data for buttons {@link Command#getTextCommand()