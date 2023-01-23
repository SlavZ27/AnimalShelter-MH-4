package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoRecommendationsHomeDog {
    private final static String infoEn =
            "First of all, you need to take care of a place for your pet. Pay attention to the shape of the house - it can be a couch, a small booth or a soft plush bedding. It all depends on the free space in the house and your imagination. In pet stores, you can find a couch for every taste and color that a small breed dog will like and will perfectly fit into the interior";
    public static String getInfoEn() {
        return infoEn;
    }
}
