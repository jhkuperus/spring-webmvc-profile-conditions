package nl.jhkuperus.org.springframework.web.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Indicates a RequestMapping should be restricted further by requiring a certain profile of a
 * MediaType. This annotation MUST be combined with a @RequestMapping-annotation which also
 * specifies a produces-parameter. The MediaType(s) of the RequestMapping are combined with the
 * profiles from this annotation to differentiate between different values of the Accept-header
 * in the request.</p>
 * <p>Wildcards are supported, but only as a postfix wildcard. Specific values always take precedence
 * over wildcard values and more specific wildcards take precedence over more general wildcard expressions.</p>
 *
 * <p><strong>Note: </strong> requires the use of a {@link nl.jhkuperus.org.springframework.web.servlet.mvc.method.annotation.CustomConditionProvidingRequestMappingHandlerMapping}
 * configured with a {@link RequireProfileConditionProvider} in order to be picked up during scanning for request mappings.</p>
 * 
 * @author J.H. Kuperus
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequireProfile {

    /**
     * @return Specifies the required value of the 'profile' parameter to the Accept-ed media type. Allows for wildcard-postfix of '*'
     */
    String[] value();

}


