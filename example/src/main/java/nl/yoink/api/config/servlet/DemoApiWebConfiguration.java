package nl.yoink.api.config.servlet;

import nl.yoink.org.springframework.web.bind.annotation.RequireProfileConditionProvider;
import nl.yoink.org.springframework.web.servlet.mvc.method.annotation.CustomConditionProvidingRequestMappingHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan(basePackages = {"nl.yoink.api.config.servlet", "nl.yoink.api.services"})
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableWebMvc
public class DemoApiWebConfiguration extends WebMvcConfigurationSupport {
    
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }

    @Override
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        CustomConditionProvidingRequestMappingHandlerMapping handlerMapping = new CustomConditionProvidingRequestMappingHandlerMapping();

        RequireProfileConditionProvider customConditionProvider = new RequireProfileConditionProvider();
        handlerMapping.setCustomMethodConditionProvider(customConditionProvider);
        handlerMapping.setCustomTypeConditionProvider(customConditionProvider);

        return handlerMapping;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaTypes.HAL_JSON);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(mediaTypes);
        return converter;
    }
    

}
