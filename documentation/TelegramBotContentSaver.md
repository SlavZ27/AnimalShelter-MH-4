[TelegramBotContentSaver]

The class contains methods for saving 
various content that comes from telegram chat.

#### public void savePhoto(Update update) throws IOException

The method saves the photo that is contained in 
the Update.
  * From several copies of one photo, the method selects
the last one, because it is expected that it will have
a higher quality than the others.
  * The photo from Update message() photo() is saved in the file system.
  * The file is saved in TelegramBotContentSaver materialsDir.
  * The constant takes the value from the file using constructor.
  * using GetFile
  * using GetFileResponse
  * using TelegramBot#execute(BaseRequest)
  * using TelegramBotContentSaver#getAndCreatePath(Long, String, String)
  * using Files#write(Path, byte[], OpenOption...)
  * Update#message() chat() id() must be not null
  * Update#message() photo() must be not null
  

    Param update.
| Name   | Type   | Description                  |
|--------|--------|------------------------------|
| update | Update | Returns command updates |

    Throws IOException.

#### private boolean checkOrCreateFolder(String path)
The method checks folder exists in the file system and creates if necessary
* using java.io.File mkdir()


    Param: path must be not null.
| Name   | Type   | Description                  |
|--------|--------|------------------------------|
| path | String | sets the path to the file |
    Return: true if folder was created, else fal.

#### private Path getAndCreatePath(Long idChat, String folderName, String fileFormat)
The method checks and, if necessary, creates the full path to the file.
* The data is taken from the input data. <br>
* Example of a path:TelegramBotContentSaver 
* materialsDir + / + idChat + / +  folderName + / + YY
* YY.MM.DD_HH.MM. + fileFormat
* using TelegramBotContentSaver checkOrCreateFolder(String)}


    Param idChat
    Param folderName
    Param fileFormat

| Name   | Type   | Description              |
|--------|--------|--------------------------|
| idChat | Long   | sets the path to the file |    
| folderName | String | Format of the file being sent |    
| fileFormat | String | Name of the file being sent                         |    

    Return:Saved file with folder path.

###private String parseFileFormat(String filePath)

The method gets the path to the file and outputs 
characters after the last dot



    Param: filePath must be not null.
| Name   | Type   | Description            |
|--------|--------|------------------------|
| filePath | String   | The path to the specified file |
    Return: file format without dot, if string 
    don't contain dot then return null.
