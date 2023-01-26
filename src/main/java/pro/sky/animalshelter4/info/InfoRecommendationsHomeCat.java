package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoRecommendationsHomeCat {
    private final static String infoEn =
            "First of all, you need to take care of a place for your pet.\n\n"+
                    "- Cat hammocks. They are soft sun beds with various fastening options (loops, Velcro, carabiners). They can be hung anywhere, fixed on four sides. There are products that are sold with rigid bases – they can be fixed, for example, between the crossbars of the table. Such a cozy place is not difficult to make yourself.\n" +
                    "- Beds. Another simple and inexpensive option for a pet home. They are very different: in the form of boxes and baskets, in the form of hearts, rectangles and circles. A minimalistic couch can consist of two parts – a small stand and a dense mattress made in a solid color.\n" +
                    "-A house in the shape of a booth. The usual design with a roof, upholstered with a soft fabric, for example plush. You can make it with your own hands. Often houses in the form of booths can be sold together with a scratching post fixed on the same platform with the house.\n"+
                    "-Built-in houses. Usually these are curbstones and ottomans that perform two tasks at once. For example, the owner makes personal belongings on the bedside table, and the cat sleeps inside it. Such mini-houses are upholstered inside with plush or other soft material, often supplemented with a special pillow-a couch for an animal.\n"+
                    "It all depends on the free space in the house and your imagination. In pet stories you can find a house for every taste and color that any cat will like and will fit perfectly into the interior\n";

    public static String getInfoEn() {
        return infoEn;
    }
}
