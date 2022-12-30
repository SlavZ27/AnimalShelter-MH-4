package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoTakeADog {
    private final static String infoRu =
            "Приют не резервирует собак.\n"+
            "Выбрать собаку может только потенциальный хозяин, приехав в приют лично.\n\n" +
                    "1. Предварительно оставьте заявку волонтеру и договоритесь о встрече.\n" +
                    "2. Отправляясь в приют, возьмите паспорт, он понадобится при подписании договора. Собаки отдаются будущему владельцу только после подписания договора о его передаче.\n" +
                    "3. Мы оставляем за собой право проверять условия содержания наших животных в новом доме и возвращаем животных в приют в случае, если новые владельцы не соблюдают условия договора.\n\n" +
            "Чтобы взять собаку,нужно:\n\n" +
                    "• паспорт.\n" +
                    "• ошейник с поводком.\n" +
                    "• транспорт.\n\n";

    private final static String infoEn =
            "The shelter does not reserve dogs.\n"+
            "Only a potential owner can choose a dog by coming to the shelter in person.\n\n" +
                    "1. Pre-submit a request to the volunteer and arrange a meeting.\n" +
                    "2. When going to the shelter, take a passport, you will need it when signing the contract. Dogs are given to the future owner only after signing the contract on its transfer.\n" +
                    "3. We reserve the right to check the conditions of keeping our animals in a new home and return the animals to the shelter if the new owners do not comply with the terms of the contract.\n\n" +
            "To take a dog, you need:\n\n" +
                    "• passport.\n" +
                    "• a collar with a leash.\n" +
                    "• transport.\n\n";

    public static String getInfoRu() {
        return infoRu;
    }

    public static String getInfoEn() {
        return infoEn;
    }
}
