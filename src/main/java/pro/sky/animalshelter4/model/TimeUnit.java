package pro.sky.animalshelter4.model;

public enum TimeUnit
{
    YEAR("year"),
    MONTH("month"),
    DAY("day"),
    HOUR("hour"),
    MINUTE("minute"),
    NEXT("next"),
    NOW("now");

    private final String title;

    TimeUnit(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
