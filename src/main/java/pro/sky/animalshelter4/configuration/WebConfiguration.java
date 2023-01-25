package pro.sky.animalshelter4.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.sky.animalshelter4.interceptor.DataSourceInterceptor;

@Component
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final DataSourceInterceptor dataSourceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(dataSourceInterceptor);
    }
}
