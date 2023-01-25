package pro.sky.animalshelter4.configuration;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType dataSourceType = DataSourceType.DOG;

        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            dataSourceType = (DataSourceType) request.getAttribute("database");
        } else {
            dataSourceType = DatabaseContextHolder.getClientDatabase();
        }
        return dataSourceType;
    }

    public void initDataSources(DataSource dataSource0, DataSource dataSource1, DataSource dataSource2) {
        Map<Object, Object> dsMap = new HashMap<Object, Object>();
        dsMap.put(DataSourceType.PUBLIC, dataSource0);
        dsMap.put(DataSourceType.DOG, dataSource1);
        dsMap.put(DataSourceType.CAT, dataSource2);

        this.setTargetDataSources(dsMap);
    }

}
