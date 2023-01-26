package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoGettingKnowCat {
    private final static String infoEn =
            "Что нельзя делать при знакомстве с кошкой:\n" +
                    "- Rise above the animal. It is better to sit down or even lie down on the floor — so the cat will understand that there is no threat to her.\n" +
                    "- Make noise. A loud voice or a sudden sound from outside will spoil the acquaintance.\n" +
                    "- Move abruptly. To establish a relationship, it is enough to gently and unhurriedly stretch out your finger so that the cat sniffs it.\n";



    public static String getInfoEn() {
        return infoEn;
    }
}
