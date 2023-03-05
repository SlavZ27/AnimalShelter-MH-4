package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoWhyDoYouNeedDogHandler {
    private final static String infoEn =
            "Why do you need a dog handler?\n"+
                    "A dog handler is a specialist in dog training. He understands not only the physiological, but also the psychological characteristics of pets. Dog handlers are engaged in special and household training: they find mistakes in upbringing, explain to owners the specifics of interaction with dogs, help build the right hierarchy in the family and raise an obedient dog";

    public static String getInfoEn() {
        return infoEn;
    }

}
