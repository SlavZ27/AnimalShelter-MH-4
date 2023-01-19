package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoTransportationAnimals {
    private final static String infoEn =
            "Ground transport for the transportation of live animals in Russia must meet the following technical requirements:\n" +
                    "- The bottom of containers or car bodies should be strong, solid and smooth. There should be no cracks, holes, nails, wire or chips on its surface. Otherwise, the animals may be injured on the road, or get caught in an artificial trap\n" +
                    "- The bottom is usually covered with a special absorbent film, hay or dead wood\n" +
                    "- A railway container or a car body must protect the animal from rain and direct sunlight. But at the same time, it must provide free circulation of fresh air inside a limited space\n" +
                    "- The car body or container must be made of natural or non-toxic materials, for example, durable metal, boards or plastic\n";

    public static String getInfoEn() {
        return infoEn;
    }
}