package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoListOfDocuments {
    private final static String infoEn =
            "List of documents:\n" +
                    "- Puppy Reservation Agreement\n" +
                    "- Puppy Card\n" +
                    "- Pedigree of the dog\n" +
                    "- Veterinary passport\n" +
                    "- Brand\n" +
                    "- passport.\n" +
                    "- Microchip\n" +
                    "- International veterinary passport\n";

    public static String getInfoEn() {
        return infoEn;
    }
}
