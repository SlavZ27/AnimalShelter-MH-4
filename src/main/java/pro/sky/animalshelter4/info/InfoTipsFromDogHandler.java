package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoTipsFromDogHandler {
    private final static String infoEn =
            "Tips from a dog handler on primary communication with a dog:\n"+
                    "How to recognize a well-behaved dog\n" +
                    "It's good to have a well-behaved dog. She is a welcome guest everywhere, because she behaves well both in the company of people and other dogs. She knows how to stay in her place and approach when called. It's nice to go for a walk with her, because she walks well next to her, does not pull a leash, knows how to wear a muzzle and does not run after cats and other animals. She does not suffer from phobias (she is not afraid of harsh sounds, public transport, visits to the vet hospital, other dogs), calmly stays at home alone, does not spoil things. Does not require to be fed first, does not beg, does not steal food from the table. Does not take the place of the owner without permission. He relieves himself only in specially designated places.\n" +
                    "When is it necessary to start training a dog\n" +
                    "It should be borne in mind that the educational training of a puppy and the training of an adult dog are very different. Puppies should be raised shortly after birth, the most suitable age is 3.5 — 4 months";

    public static String getInfoEn() {
        return infoEn;
    }
}
