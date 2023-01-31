package pro.sky.animalshelter4.exception;

import pro.sky.animalshelter4.entity.Animal;
import pro.sky.animalshelter4.entity.AnimalOwnership;
import pro.sky.animalshelter4.entity.User;

public class AnimalOwnershipBadParameterException extends RuntimeException {
    public AnimalOwnershipBadParameterException() {
        super("The parameters must be the same: " +
                "animalOwnership.setShelter() | user.setShelter() | animal.setShelter()");
    }
}
