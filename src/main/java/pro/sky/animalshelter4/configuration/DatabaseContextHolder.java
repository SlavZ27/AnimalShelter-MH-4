package pro.sky.animalshelter4.configuration;

import org.springframework.util.Assert;

public class DatabaseContextHolder {
    private static ThreadLocal<DataSourceType> CONTEXT
            = new ThreadLocal<>();

    public static void set(DataSourceType clientDatabase) {
        Assert.notNull(clientDatabase, "clientDatabase cannot be null");
        CONTEXT.set(clientDatabase);
    }

    public static DataSourceType getClientDatabase() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
