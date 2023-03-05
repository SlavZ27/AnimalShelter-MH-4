package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoGettingKnowDog {

    private final static String infoRu =
            "Что нельзя делать:\n" +
                    "- Не нависайте над собакой (не наклоняйтесь над ней)\n" +
                    "- Не нужно смотреть пристально собаке в глаза\n" +
                    "- Не нужно обнимать и прижимать к себе собаку\n" +
                    "- Не нужно целовать ее\n" +
                    "- Не трогайте за голову, и не гладьте по голове\n" +
                    "- Не надо кричать, визжать\n" +
                    "- Не нужно приближаться быстро и лоб в лоб\n" +
                    "А как правильно:\n" +
                    "- Дайте собаке возможность самой подойти к вам Если вам нужно подойти к ней, делайте это немного как бы по дуге и не торопясь\n" +
                    "- Дайте собаке возможность обнюхать вас Если она сильно боится, можно присесть на корточки, но, не наклоняясь над ней (если собачка маленькая)\n" +
                    "- Избегайте взгляда глаза в глаза\n" +
                    "- Можете погладить по бокам, щечкам, груди, если она не сопротивляется — по спине\n" +
                    "- Если хозяин не против, можете угостить лакомством на открытой ладони";

    private final static String infoEn =
            "What not to do:\n" +
                    "- Do not hover over the dog (do not bend over it)\n" +
                    "- No need to look closely into the dog's eyes\n" +
                    "- No need to hug and cuddle the dog\n" +
                    "- No need to kiss her\n" +
                    "- Do not touch the head, and do not stroke the head\n" +
                    "- No need to scream\n" +
                    "- No need to approach quickly and head-on\n" +
                    "And how is it right:\n" +
                    "- Give the dog the opportunity to come to you by itself. If you need to approach her, do it a little bit as if in an arc and not in a hurry\n" +
                    "- Give the dog a chance to sniff you. If she is very afraid, you can squat down, but without bending over her (if the dog is small)\n" +
                    "- Avoid eye-to-eye contact\n" +
                    "- You can stroke the sides, cheeks, breasts, if she does not resist — on the back\n" +
                    "- If the owner does not mind, you can treat a treat in the open palm";

    public static String getInfoRu() {
        return infoRu;
    }

    public static String getInfoEn() {
        return infoEn;
    }

}
