package pro.sky.animalshelter4.model;

public enum OwnershipUnit {

    START, USER, ANIMAL;

    OwnershipUnit() {
    }

    public static OwnershipUnit fromStringUpperCase(String textOwnershipUnit) {
        if (textOwnershipUnit == null || textOwnershipUnit.length() < 1) {
            return null;
        }
        textOwnershipUnit = textOwnershipUnit.toUpperCase();
        try {
            return OwnershipUnit.valueOf(OwnershipUnit.class, textOwnershipUnit);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
