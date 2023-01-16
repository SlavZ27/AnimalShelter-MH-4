### [TelegramBotConfiguration]
The class is needed to take the token from the file,
create and configure bean.

private String token;

* Token is taken from the file by reference using an 
annotation @Value.

public TelegramBot telegramBot()

* Bean is created using an annotation @Bean
* @return {@link TelegramBot}
