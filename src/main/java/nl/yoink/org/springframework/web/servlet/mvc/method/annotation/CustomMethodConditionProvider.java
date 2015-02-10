package nl.yoink.org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

import java.lang.reflect.Method;

/**
 * Defines behaviour for providing custom {@link RequestCondition}s on method level during the scanning for
 * request mappings in the {@link CustomConditionProvidingRequestMappingHandlerMapping}. 
 *
 * @author J.H. Kuperus
 */
public interface CustomMethodConditionProvider<T extends RequestCondition> {

    /**
     * @param method the method to detect a custom {@link RequestCondition} for.
     * @return a customer {@link RequestCondition} if this provider wishes to add it to the
     *   mapping info for the specified method. Null if no such condition is required.
     */
    T detectMethodCondition(Method method);

}
