package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoListOfDocumentsCat {
    private final static String infoEn =
            "List of documents:\n" +
                    "- Kitten booking agreement\n" +
                    "- Kitten card\n" +
                    "- Kitten pedigree\n" +
                    "- Veterinary passport\n" +
                    "- Brand\n" +
                    "- passport.\n" +
                    "- Microchip\n" +
                    "- International veterinary passport\n";

    public static String getInfoEn() {
        return infoEn;
    }
}
