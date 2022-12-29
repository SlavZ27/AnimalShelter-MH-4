package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoAboutShelter {
    private final static String infoRu =
            "Мы-негосударственный приют для кошек Астана.\n\n" +
                    "Приют был открыт 15 октября 2013 года неравнодушными девушками.\n" +
                    "Существует исключительно за счет добровольных пожертвований людей, любящих животных.\n" +
                    "В данный момент мы не имеем своего собственного помещения под приют. Арендуем небольшой домик в частном секторе.\n" +
                    "Если вы желаете помочь приюту, то нам всегда необходима помощь.\n\n";

    private final static String infoEn =
            "We are a non-governmental shelter for cats in Astana.\n\n" +
                    "The shelter was opened on October 15, 2013 by caring girls.\n" +
                    "It exists solely through voluntary donations from people who love animals.\n" +
                    "If you want to help the shelter, then we always need help.\n\n";

    public static String getInfoRu() {
        return infoRu;
    }

    public static String getInfoEn() {
        return infoEn;
    }
}
