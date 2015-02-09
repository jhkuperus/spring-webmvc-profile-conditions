package nl.jhkuperus.org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * <p>Extension of the default {@link RequestMappingHandlerMapping} which allows for the detection of
 * custom {@link RequestCondition}s through the use of a {@link CustomMethodConditionProvider} and/or
 * a {@link CustomTypeConditionProvider}.</p>
 * <p>For example, you could plug in the {@link nl.jhkuperus.org.springframework.web.bind.annotation.RequireProfileConditionProvider}
 * to enable the use of {@link nl.jhkuperus.org.springframework.web.bind.annotation.RequireProfile} annotation. </p>
 *  
 * @author J.H. Kuperus
 */
public class CustomConditionProvidingRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private CustomMethodConditionProvider<?> customMethodConditionProvider;
    private CustomTypeConditionProvider<?> customTypeConditionProvider;

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        if (this.customMethodConditionProvider != null) {
            return this.customMethodConditionProvider.detectMethodCondition(method);
        }

        return super.getCustomMethodCondition(method);
    }

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        if (this.customTypeConditionProvider != null) {
            return this.customTypeConditionProvider.detectTypeCondition(handlerType);
        }

        return super.getCustomTypeCondition(handlerType);
    }

    public void setCustomMethodConditionProvider(CustomMethodConditionProvider<?> customMethodConditionProvider) {
        this.customMethodConditionProvider = customMethodConditionProvider;
    }

    public void setCustomTypeConditionProvider(CustomTypeConditionProvider<?> customTypeConditionProvider) {
        this.customTypeConditionProvider = customTypeConditionProvider;
    }
}
