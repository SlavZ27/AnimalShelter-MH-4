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

public class Generator {

    private final Faker faker = new Faker();
    private final Random random = new Random();

    public int genInt(int max) {
        return random.nextInt(max);
    }

    public int genInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public LocalDateTime generateDateTime(boolean isPast, LocalDateTime localDateTime) {
        LocalDateTime tldt = LocalDateTime.now();
        if (isPast) {
            tldt = localDateTime.plusYears(1L);
            while (tldt.isBefore(localDateTime)) {
                LocalDate localDate = LocalDate.of(genInt(2020, 2022), genInt(12), genInt(25));
                LocalTime localTime = LocalTime.of(genInt(23), genInt(59));
                tldt = LocalDateTime.of(localDate, localTime);
            }
        } else {
            tldt = localDateTime.minusYears(1L);
            while (tldt.isAfter(localDateTime)) {
                LocalDate localDate = LocalDate.of(genInt(2020, 2022), genInt(12), genInt(25));
                LocalTime localTime = LocalTime.of(genInt(23), genInt(59));
                tldt = LocalDateTime.of(localDate, localTime);
            }
        }
        return tldt;
    }

    public Chat mapUpdateToChat(Update update) {
        Chat chat = new Chat();
        if (update.message() != null) {
            chat.setId(update.message().chat().id());
            chat.setName(generateNameIfEmpty(update.message().chat().firstName()));
        } else if (update.callbackQuery() != null) {
            chat.setId(update.callbackQuery().from().id());
            chat.setName(generateNameIfEmpty(update.callbackQuery().from().firstName()));
        }
        chat.setVolunteer(false);
        chat.setPhone(generatePhoneIfEmpty(""));
        return chat;
    }

    public Chat generateChat(Long idChat, String name, String address, String phone, boolean isVolunteer, boolean needGenerate) {
        if (needGenerate) {
            idChat = generateIdIfEmpty(idChat);
            name = generateNameIfEmpty(name);
            address = generateAddressIfEmpty(address);
            phone = generatePhoneIfEmpty(phone);
        }
        Chat chat = new Chat();
        chat.setId(idChat);
        chat.setName(name);
        chat.setPhone(phone);
        chat.setAddress(address);
        chat.setVolunteer(isVolunteer);
        return chat;
    }

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

    public int generateTimeZoneIfNull(Integer timeZone) {
        if (timeZone == null || timeZone < 0) {
            timeZone = faker.random().nextInt(-11, 12);
        }
        return timeZone;
    }

    public String generateAddressIfEmpty(String address) {
        if (address == null || address.length() == 0) {
            return faker.address().streetAddress();
        }
        return address;
    }

    public String generateCityIfEmpty(String city) {
        if (city == null || city.length() == 0) {
            return faker.address().city();
        }
        return city;
    }

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

    public String generateNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().username();
        }
        return name;
    }

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

    public String generateMessageIfEmpty(String message) {
        if (message == null || message.length() == 0) {
            return faker.lordOfTheRings().character();
        }
        return message;
    }
}
