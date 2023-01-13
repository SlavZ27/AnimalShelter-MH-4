package pro.sky.animalshelter4;

import com.github.javafaker.Faker;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import pro.sky.animalshelter4.entity.Chat;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

/**
 * The method contains methods for generating objects with the necessary parameters that are needed to perform tests
 * Using {@link Faker}
 */
public class Generator {

    private final Faker faker = new Faker();
    private final Random random = new Random();


    /**
     * The method returns a random number up to the specified value.
     * Using {@link Random#nextInt(int)}
     *
     * @param max
     * @return
     */
    public int genInt(int max) {
        return random.nextInt(max);
    }

    /**
     * The method returns a random number between the parameters
     * Using {@link Random#nextInt(int)}
     *
     * @param min
     * @param max
     * @return
     */
    public int genInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }


    /**
     * @param isPast
     * @param localDateTime
     * @return Method generates the date and time in {@link LocalDateTime} format before or after the parameter
     */
    public LocalDateTime generateDateTime(boolean isPast, LocalDateTime localDateTime) {
        LocalDateTime tldt = LocalDateTime.now();
        int year = tldt.getYear();
        if (isPast) {
            tldt = localDateTime.plusYears(1L);
            while (tldt.isBefore(localDateTime)) {
                LocalDate localDate = LocalDate.of(genInt(year-2, year), genInt(12), genInt(25));
                LocalTime localTime = LocalTime.of(genInt(23), genInt(59));
                tldt = LocalDateTime.of(localDate, localTime);
            }
        } else {
            tldt = localDateTime.minusYears(1L);
            while (tldt.isAfter(localDateTime)) {
                LocalDate localDate = LocalDate.of(genInt(year-2, year), genInt(12), genInt(25));
                LocalTime localTime = LocalTime.of(genInt(23), genInt(59));
                tldt = LocalDateTime.of(localDate, localTime);
            }
        }
        return tldt;
    }

    /**
     * The method does the mapping update in comparison with the check
     * {@link Update#message()}!=null or
     * {@link Update#callbackQuery()} ()}!=null
     *
     * @param update
     * @return
     */
    public Chat mapUpdateToChat(Update update) {
        Chat chat = new Chat();
        if (update.message() != null) {
            chat.setId(update.message().chat().id());
            chat.setFirstNameUser(generateNameIfEmpty(update.message().from().firstName()));
            chat.setLastNameUser(generateNameIfEmpty(update.message().from().lastName()));
            chat.setUserNameTelegram(generateNameIfEmpty(update.message().from().username()));
        } else if (update.callbackQuery() != null) {
            chat.setId(update.callbackQuery().from().id());
            chat.setFirstNameUser(generateNameIfEmpty(update.callbackQuery().from().firstName()));
            chat.setLastNameUser(generateNameIfEmpty(update.callbackQuery().from().lastName()));
            chat.setUserNameTelegram(generateNameIfEmpty(update.callbackQuery().from().username()));
        }
        return chat;
    }

    public pro.sky.animalshelter4.entity.User generateUser(Long idUser, String nameUser, Chat chatTelegram, String phone, String address, boolean isVolunteer, boolean needGenerate) {
        if (needGenerate) {
            idUser = generateIdIfEmpty(idUser);
            nameUser = generateNameIfEmpty(nameUser);
            if (chatTelegram == null) {
                chatTelegram = generateChat(null, nameUser, nameUser, null, null, true);
            }
            phone = generatePhoneIfEmpty(phone);
            address = generateAddressIfEmpty(address);
        }
        return new pro.sky.animalshelter4.entity.User(
                idUser,
                nameUser,
                chatTelegram,
                phone,
                address,
                isVolunteer);
    }

    /**
     * The method generates a {@link Chat} object. there is an automatic field filling function.
     * If you want to generate values, then specify needGenerate=true and leave the fields equal to "" or -1
     * Using {@link Generator#generateIdIfEmpty(Long)}
     * Using {@link Generator#generateNameIfEmpty(String)}
     * Using {@link Generator#generateAddressIfEmpty(String)}
     * Using {@link Generator#generatePhoneIfEmpty(String)}
     *
     * @param idChat
     * @param userNameTelegram
     * @param firstNameUser
     * @param lastNameUser
     * @param last_activity
     * @param needGenerate
     * @return
     */
    public Chat generateChat(Long idChat, String userNameTelegram, String firstNameUser, String lastNameUser, LocalDateTime last_activity, boolean needGenerate) {
        if (needGenerate) {
            idChat = generateIdIfEmpty(idChat);
            userNameTelegram = generateNameIfEmpty(userNameTelegram);
            firstNameUser = generateNameIfEmpty(firstNameUser);
            lastNameUser = generateNameIfEmpty(lastNameUser);
            last_activity = generateDateTime(true, LocalDateTime.now());
        }
        return new Chat(
                idChat,
                userNameTelegram,
                firstNameUser,
                lastNameUser,
                last_activity);
    }


    /**
     * The method generates a {@link Update} with {@link Update#callbackQuery()} object. there is an automatic field filling function.
     * If you want to generate values, then specify needGenerate=true and leave the fields equal to "" or -1
     * Using {@link Generator#generateNameIfEmpty(String)}
     * Using {@link Generator#generateMessageIfEmpty(String)}
     * Using {@link Generator#generateIdIfEmpty(Long)}
     *
     * @param userName
     * @param firstName
     * @param lastName
     * @param chatId
     * @param callbackQueryData
     * @param needGenerate
     * @return
     */
    public Update generateUpdateCallbackQueryWithReflection(String userName,
                                                            String firstName,
                                                            String lastName,
                                                            Long chatId,
                                                            String callbackQueryData,
                                                            boolean needGenerate) {
        if (needGenerate) {
            userName = generateNameIfEmpty(userName);
            firstName = generateNameIfEmpty(firstName);
            lastName = generateNameIfEmpty(lastName);
            chatId = generateIdIfEmpty(chatId);
            callbackQueryData = generateMessageIfEmpty(callbackQueryData);
        }

        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        User user = new User(0L);

        try {
            Field userNameField = user.getClass().getDeclaredField("username");
            userNameField.setAccessible(true);
            Field firstNameField = user.getClass().getDeclaredField("first_name");
            firstNameField.setAccessible(true);
            Field lastNameField = user.getClass().getDeclaredField("last_name");
            lastNameField.setAccessible(true);
            Field userId = user.getClass().getDeclaredField("id");
            userId.setAccessible(true);
            userNameField.set(user, userName);
            firstNameField.set(user, firstName);
            lastNameField.set(user, lastName);
            userId.set(user, chatId);

            Field callbackUserField = callbackQuery.getClass().getDeclaredField("from");
            callbackUserField.setAccessible(true);
            Field callbackDataField = callbackQuery.getClass().getDeclaredField("data");
            callbackDataField.setAccessible(true);
            callbackUserField.set(callbackQuery, user);
            callbackDataField.set(callbackQuery, callbackQueryData);

            Field updateCallbackField = update.getClass().getDeclaredField("callback_query");
            updateCallbackField.setAccessible(true);
            updateCallbackField.set(update, callbackQuery);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return update;
    }

    public Update generateUpdateMessageWithReflection() {
        return generateUpdateMessageWithReflection("", "", "", -1L, "", true);
    }

    /**
     * The method generates a {@link Update} with {@link Update#message()} ()} object. there is an automatic field filling function.
     * If you want to generate values, then specify needGenerate=true and leave the fields equal to "" or -1. <br>
     * Using {@link Generator#generateNameIfEmpty(String)}
     * Using {@link Generator#generateMessageIfEmpty(String)}
     * Using {@link Generator#generateIdIfEmpty(Long)}
     *
     * @param userName
     * @param firstName
     * @param lastName
     * @param chatId
     * @param messageText
     * @param needGenerate
     * @return
     */
    public Update generateUpdateMessageWithReflection(String userName,
                                                      String firstName,
                                                      String lastName,
                                                      Long chatId,
                                                      String messageText,
                                                      boolean needGenerate) {
        if (needGenerate) {
            userName = generateNameIfEmpty(userName);
            firstName = generateNameIfEmpty(firstName);
            lastName = generateNameIfEmpty(lastName);
            messageText = generateMessageIfEmpty(messageText);
            chatId = generateIdIfEmpty(chatId);
        }

        Update update = new Update();
        Message message = new Message();
        com.pengrad.telegrambot.model.Chat chat = new com.pengrad.telegrambot.model.Chat();
        User user = new User(0L);

        try {
            Field userNameField = user.getClass().getDeclaredField("username");
            userNameField.setAccessible(true);
            Field firstNameField = user.getClass().getDeclaredField("first_name");
            firstNameField.setAccessible(true);
            Field lastNameField = user.getClass().getDeclaredField("last_name");
            lastNameField.setAccessible(true);
            Field userId = user.getClass().getDeclaredField("id");
            userId.setAccessible(true);
            userNameField.set(user, userName);
            firstNameField.set(user, firstName);
            lastNameField.set(user, lastName);
            userId.set(user, chatId);


            Field chatIdField = chat.getClass().getDeclaredField("id");
            chatIdField.setAccessible(true);
            chatIdField.set(chat, chatId);

            Field messageTextField = message.getClass().getDeclaredField("text");
            messageTextField.setAccessible(true);
            Field messageChatField = message.getClass().getDeclaredField("chat");
            messageChatField.setAccessible(true);
            Field messageUserField = message.getClass().getDeclaredField("from");
            messageUserField.setAccessible(true);
            messageTextField.set(message, messageText);
            messageChatField.set(message, chat);
            messageUserField.set(message, user);

            Field updateMessageField = update.getClass().getDeclaredField("message");
            updateMessageField.setAccessible(true);
            updateMessageField.set(update, message);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return update;
    }


    /**
     * The method generates a random time zone if it gets null
     * Using {@link Faker#random()#nextInt}
     *
     * @param timeZone
     * @return
     */
    public int generateTimeZoneIfNull(Integer timeZone) {
        if (timeZone == null) {
            timeZone = faker.random().nextInt(-11, 12);
        }
        return timeZone;
    }

    /**
     * The method generates a random address if it receives null or an empty string
     * Using {@link Faker#address()#streetAddress}
     *
     * @param address
     * @return
     */
    public String generateAddressIfEmpty(String address) {
        if (address == null || address.length() == 0) {
            return faker.address().streetAddress();
        }
        return address;
    }

    /**
     * The method generates a random city if it receives null or an empty string
     * Using {@link Faker#address()#city}
     *
     * @param city
     * @return
     */
    public String generateCityIfEmpty(String city) {
        if (city == null || city.length() == 0) {
            return faker.address().city();
        }
        return city;
    }

    /**
     * The method generates a random phone if it receives null or an empty string.
     * Limited to 15 characters due to database rules.
     * Using {@link Faker#phoneNumber()#phoneNumber()}
     *
     * @param phone
     * @return
     */
    public String generatePhoneIfEmpty(String phone) {
        if (phone == null || phone.length() == 0) {
            String tempPhone = faker.phoneNumber().phoneNumber();
            if (tempPhone.length() > 15) {
                tempPhone = tempPhone.substring(0, 14);
            }
            return tempPhone;
        }
        return phone;
    }

    /**
     * The method generates a random name if it receives null or an empty string.
     * Using {@link Faker#name()#username()}
     *
     * @param name
     * @return
     */
    public String generateNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().username();
        }
        return name;
    }

    /**
     * The method generates a random id for telegram if it receives null or id<0.
     * Values from 100_000_000 to 999_999_999
     * Using {@link Faker#random()#nextLong()}
     *
     * @param id
     * @return
     */
    public Long generateIdIfEmpty(Long id) {
        if (id == null || id < 0) {
            long idTemp = -1L;
            //id with <100 I leave for my needs
            while (idTemp < 100) {
                idTemp = faker.random().nextLong(999_999_999 - 100_000_000) + 100_000_000;
            }
            return idTemp;
        }
        return id;
    }

    /**
     * The method generates a random message for telegram if it receives null or an empty string
     * Using {@link Faker#lordOfTheRings()#character()}
     *
     * @param message
     * @return
     */
    public String generateMessageIfEmpty(String message) {
        if (message == null || message.length() == 0) {
            return faker.lordOfTheRings().character();
        }
        return message;
    }

    public boolean generateBool() {
        return faker.bool().bool();
    }
}
