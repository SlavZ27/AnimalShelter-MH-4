### [TelegramBotSenderService]
* The class is needed for the simplicity of sending
a message to telegram chats using 
TelegramBot#execute(BaseRequest).
* The class contains both a universal method 
TelegramBotSenderService#sendMessage(Long, String),
and ready-made methods for sending prepared messages, or buttons, or other.

#### public void sendMessage(Long idChat, String textMessage)
* A universal method for sending messages to telegram chats
* Using TelegramBot execute(BaseRequest).


     Param: idChat must be not null
     Param: textMessage must be not null
| Name   | Type   | Description            |
|--------|--------|------------------------|
| idChat | Long   |  the chat id of the user who <br/>logged into the tg bot |
| textMessage | String | Message                |

#### public void sendUnknownProcess(Long idChat)

A method with a prepared message to send a message about receiving an unknown command
* using TelegramBotSenderService MESSAGE_SORRY_I_DONT_KNOW_COMMAND
* using TelegramBotSenderService sendMessage(Long, String)


    Param: idChat must be not null.

| Name   | Type   | Description            |
|--------|--------|------------------------|
| idChat | Long   |  the chat id of the user who <br/>logged into the tg bot |

#### public void sendHello(Long idChat, String name)A method with a prepared message for sending a welcome message
  * using TelegramBotSenderService MESSAGE_HELLO + name + ".\n"
  * using TelegramBotSenderService sendMessage(Long, String)


    Param: idChat must be not null
    Param: name must be not null
| Name   | Type   | Description                                             |
|--------|--------|---------------------------------------------------------|
| idChat | Long   | the chat id of the user who <br/>logged into the tg bot |
| name | String | Name user                                                   |

#### public void sendSorryIKnowThis(Long idChat)

* A method with a prepared message to send an alert about an unknown action
  * using TelegramBotSenderService MESSAGE_SORRY_I_KN
  * using TelegramBotSenderService sendMessage(Long,String) 
 

    Param: idChat must be not null.

| Name   | Type   | Description                                             |
|--------|--------|---------------------------------------------------------|
| idChat | Long   | the chat id of the user who <br/>logged into the tg bot |

#### public void sendInfoAboutShelter(Long idChat)

A method with a prepared message for sending a message with information about the shelter
  * using {@link InfoAboutShelter#getInfoEn()}
  * using {@link TelegramBotSenderService#sendMessage(Long, String)}

 
    Param: idChat must be not null.

| Name   | Type   | Description                                             |
|--------|--------|---------------------------------------------------------|
| idChat | Long   | the chat id of the user who <br/>logged into the tg bot |

#### public void sendHowTakeDog(Long idChat)

* A method with a prepared message for sending a message with information about how to take a dog from a shelter
  * using InfoTakeADog#getInfoEn()
  * using TelegramBotSenderService sendMessage(Long, String)

  
    Param: idChat must be not null.

| Name   | Type   | Description                                             |
|--------|--------|---------------------------------------------------------|
| idChat | Long   | the chat id of the user who <br/>logged into the tg bot |


public void sendButtonsWithOneData(Long idChat,
String caption,
String command,
List<String> nameButtons,
List<String> dataButtons,
int width, int height)

A method for sending a set of buttons to a telegram 
chat with the same first value and different second 
values.
  * Messages will be signed aram caption
  * Each button will be signed with a value from the
aram nameButtons.
  * Each button will contain a value of aram 
  * command+ " " + value from the param dataButtons.
  * For example: <br>
  * /start 1 <br>
  * /start 2 <br>
  * /start 3 <br>
  * The size of the common rectangle of buttons is
set by param width and param height
  * using TelegramBot execute(BaseRequest)

     
    Param: idChat must be not null.
    Param: caption must be not null.
    Param: command must be not null.
    Param: nameButtons must be not null.
    Param: dataButtons must be not null.
    Param: width must be adequate.
    Param: height must be adequate.

| Name   | Type           | Description                                             |
|--------|----------------|---------------------------------------------------------|
| idChat | Long           | the chat id of the user who <br/>logged into the tg bot |
| caption | String         | The caption for the button                              |
| command | String         | The command for the button                              |
| nameButtons | List to String | name Buttons for processing commands                    |
| dataButtons | List to String | data Buttons for processing commands                    |
| width | int            | width of the plate                                                  |
| height | int           | height of the plate                                                  |

#### public void sendButtonsWithDifferentData(Long idChat,
String caption,
List<String> nameButtons,
List<String> dataButtons,
int width, int height)

A method for sending a set of buttons to a 
telegram chat with different values.
  * Messages will be signed {@param caption} 
  * Each button will be signed with a value from 
  * the param nameButtons.
  * Each button will contain a value from the 
  * param dataButtons.
  * The size of the common rectangle of buttons 
  * is set by param width. and param height.
  * using TelegramBot#execute(BaseRequest).


    Param: idChat must be not null.
    Param: caption must be not null.
    Param: command must be not null.
    Param: nameButtons must be not null.
    Param: dataButtons must be not null.
    Param: width must be adequate.
    Param: height must be adequate.

| Name   | Type           | Description                                             |
|--------|----------------|---------------------------------------------------------|
| idChat | Long           | the chat id of the user who <br/>logged into the tg bot |
| caption | String         | The caption for the button                              |
| command | String         | The command for the button                              |
| nameButtons | List to String | name Buttons for processing commands                    |
| dataButtons | List to String | data Buttons for processing commands                    |
| width | int            | width of the plate                                                  |
| height | int           | height of the plate                                                  |

#### public void sendListCommandForChat(Long idChat)

The method sends the string value of all 
available commands to idChat as a list.
  * The provision of the list, using idChat, is handled 
by CommandService 
getAllTitlesAsListExcludeHide(Long)
  * using TelegramBotSenderService#sendMessage(Long, String)


    Param: idChat must be not null.
| Name   | Type           | Description                                             |
|--------|----------------|---------------------------------------------------------|
| idChat | Long           | the chat id of the user who <br/>logged into the tg bot |

#### public Pair<Integer, Integer> getTableSize(int countElements)

* Method calculates the optimal width and height values depending on the number of objects


    Param countElements.
| Name   | Type           | Description                                             |
|--------|----------------|---------------------------------------------------------|
| countElements | int           | number of elements in the table |

    Return Pair of width and height.

#### public void sendButtonsCommandForChat(Long idChat)

The method receives a pair of lists of button names and button data from
  * CommandService#getPairListsForButtonExcludeHide(Long),
  * receives a pair of values width and height
from TelegramBotSenderService#getTableSize(int),
  * forms a request and starts sending buttons 
to the chat
  * using TelegramBotSenderService 
    sendButtonsWithDifferentData(Long, String, List, List, int, int)}
  
     
    Param: idChat must be not null.
| Name   | Type           | Description                                             |
|--------|----------------|---------------------------------------------------------|
| idChat | Long           | the chat id of the user who <br/>logged into the tg bot |


#### public void sendPhoto(Long idChat, String pathFile) throws IOException

* Method sends a photo to telegram chat located in the file system.
  * Using Paths#get(URI)
  * Using Files#readAllBytes(Path)
  * Using TelegramBot#execute(BaseRequest)


    Param: idChat must be not null.
    Param: pathFile must be not null.
| Name   | Type           | Description                                             |
|--------|----------------|---------------------------------------------------------|
| idChat | Long           | the chat id of the user who <br/>logged into the tg bot |
| pathFile | String           | line file                                               |
    throws IOExcept.




