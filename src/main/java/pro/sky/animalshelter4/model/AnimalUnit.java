package pro.sky.animalshelter4.model;

public enum AnimalUnit {

    START, ID, NAME, BORN, ANIMAL_TYPE;

    AnimalUnit() {
    }

    public static AnimalUnit fromStringUpperCase(String textAnimalUnit) {
        if (textAnimalUnit == null || textAnimalUnit.length() < 1) {
            return null;
        }
        textAnimalUnit = textAnimalUnit.toUpperCase();
        try {
            return AnimalUnit.valueOf(AnimalUnit.class, textAnimalUnit);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
