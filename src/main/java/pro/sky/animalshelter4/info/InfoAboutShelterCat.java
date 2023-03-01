package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;


/**
 * This class was created to respond to the INFO command request.
 * The class has getters.
 */
@Component
public class InfoAboutShelterCat {
    private final static String infoEn =
            "We are a non-governmental shelter for cats in Astana.\n\n" +
                    "The shelter was opened on October 15, 2013 by caring girls.\n" +
                    "It exists solely through voluntary donations from people who love animals.\n" +
                    "If you want to help the shelter, then we always need help.\n\n";

    public static String getInfoEn() {
        return infoEn;
    }
}
