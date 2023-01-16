[TelegramMapperService]
* The class is intended for mapping objects
into other objects.
* A very important class, greatly affects the 
fault tolerance of the application

#### public UpdateDPO toDPO(Update update)

* The method deals with mapping Update to Update and data validation.
    * For example, idChat and userName must not be null for the program to work.
    * The method determines which command to execute depending on  Update.
    * For example, if update.message().photo()!=null,
      then  UpdateD Post Interaction Unit(Interaction Unit) changes to Interaction Unit#PHOTO.
      If update.message().text()!=null && update.message().text().starts with("/")
      then UpdateD Post Interaction Unit(Interaction Unit) changes to  Interaction Unit#COMMAND.


| Name   | Type   | Description                  |
|--------|--------|------------------------------|
| update | Update | Returns command updates |

* Return null if update == null <br>
  * return null if update.message().from() == null <br>
  * return null if update.message().from().id() == null <br>
  * return null if update.message().from().id() < 0 <br>
  * return null if update.message().from().username() != null <br>
  * return null if update.callbackQuery().from() == null <br>
  * return null if update.callbackQuery().from().id() == null <br>
  * return null if update.callbackQuery().from().id() < 0 <br>
  * return null if update.callbackQuery().from().username() != null <br>

#### private boolean isNotNullOrEmpty(String s)
* The method checks the string so that it is not null, or empty

| Name   | Type   | Description                                                  |
|--------|--------------------------------------------------------------|------------------------------|
| s | string | parameter that should not be null andin length greater than 0|

Return: true or false

#### public String toWord(String s, int indexWord)
* The method makes a single word from a string with many words

| Name   | Type   | Description      |
|--------|--------------------------------------------------------------|------------------|
| s | string | Defines the text |
| indexWord | int | Defines the length |

  * Return: word with indexWord 
  * if (s==null) then return null 
  * if (indexWord > sum of words into string) then return "" 
  * if (string don't contain {@link TelegramBotSenderService#REQUEST_SPLIT_SYMBOL}) then return string without changes.


  



