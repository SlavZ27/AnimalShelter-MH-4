package pro.sky.animalshelter4.info;

import org.springframework.stereotype.Component;

@Component
public class InfoDogsWithDisabilities {
    private final static String infoEn =
            "Recommendations for dogs with disabilities:\n"+
                    "Deaf dogs\n" +
                    "Such a dog is easy to scare, especially if you suddenly appear in front of her, since she does not hear your steps in advance. It is important to be able to tell the dog what you want to do. To do this, you need to attract her attention without using sound, for example, using vibration or light. Tap your foot or hand on the floor if you want to call your pet or warn about your approach. The dog will feel the vibration and realize that someone is approaching her. To attract attention, you can also use a flashlight or a collar with vibration (not to be confused with an electric collar!). You can safely take these devices with you for a walk. Whenever you want to call your pet or attract his attention, turn on the flashlight, directing the light ahead of the dog, or turn on the vibration on the animal's collar\n" +
                    "Blind dogs\n" +
                    "It may seem that a dog's lack of vision will become a strong obstacle to its full-fledged life, but a blind pet adapts to this problem quickly enough and begins to use hearing and smell to navigate the environment. For such a pet, it is very important to ensure a safe environment: first of all, carefully inspect the apartment, remove all protruding objects that can injure the dog, fence the stairs, close the hatches and pits in the courtyard of a private house and remove all the wires. To make it easy for the dog to navigate at home, you can use narrow carpet paths along which the pet will walk to its place, to the bowl, to the doors. Make sure that when you leave the house, the dog stays in a safe space.\n" +
                    "Wheelchair dogs\n" +
                    "The absence of a paw, paralysis of limbs and other health problems do not take away from dogs their curiosity and cheerfulness. Such pets need special care, but otherwise such dogs are no different from those who run on four paws.\n" +
                    "\n" +
                    "It is a misconception to believe that an animal that is restricted in movement due to health problems has no chance of a happy life. If you give the dog the opportunity to move around with the help of a special stroller, she will not notice the difference and will also run after toys and get acquainted with relatives.\n" +
                    "\n" +
                    "In general, the upbringing of such dogs is no different from that used for healthy pets. It is only important for the owner to correctly distribute the load on the animal so that the pet does not get tired while walking, playing or training.\n" +
                    "\n" +
                    "Dogs with disabilities certainly need more care, attention and love from the owner. There is no need to feel sorry for the dog, adapt with it, use the senses that the pet possesses";


    public static String getInfoEn() {
        return infoEn;
    }
}
