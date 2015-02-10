package nl.yoink.org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

/**
 * Defines behaviour for providing custom {@link RequestCondition}s on type level during the scanning for
 * request mappings in the {@link CustomConditionProvidingRequestMappingHandlerMapping}.
 *
 * @author J.H. Kuperus
 */
public interface CustomTypeConditionProvider<T extends RequestCondition> {

    /**
     * @param handlerType the class to detect a custom {@link RequestCondition} for.
     * @return a customer {@link RequestCondition} if this provider wishes to add it to the
     *   mapping info for the specified class. Null if no such condition is required.
     */
    T detectTypeCondition(Class<?> handlerType);
    
}
