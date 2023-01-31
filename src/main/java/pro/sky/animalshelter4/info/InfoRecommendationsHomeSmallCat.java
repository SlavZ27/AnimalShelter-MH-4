package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoRecommendationsHomeSmallCat {
    private final static String infoEn =
            "To begin with, get what your pet needs from the first hours of life in your home: \n" +
                    " - A couch. It should be comfortable for the animal, that is, fit it in size, and not, as they say, grow. You can replace the sofa with a home one, but with the same condition — the main thing is comfort, not size\n" +
                    "- Toys. Their main task is not only to entertain, but also to distract the kitty from spoiling household items. The choice of toys is extremely wide: balls, plush products, wicker men and even special slippers. Do not forget to make sure that the pet does not touch anything that is not intended for him. For example, without exaggeration, the wires of electrical appliances can pose a deadly danger." +
                    "- Bowls for food and water. It is better to give preference to individual models. Also, the kitten will need treats and food, about the right choice of which read below " +
                    "- A toilet tray and a diaper. Be patient. This will be needed when you teach your pet to meet his needs in the right place and on time";

    public static String getInfoEn() {
        return infoEn;
    }

}
