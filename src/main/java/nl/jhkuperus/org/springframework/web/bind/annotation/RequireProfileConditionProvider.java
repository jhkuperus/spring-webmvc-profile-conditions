package nl.jhkuperus.org.springframework.web.bind.annotation;

import nl.jhkuperus.org.springframework.web.servlet.mvc.method.annotation.CustomMethodConditionProvider;
import nl.jhkuperus.org.springframework.web.servlet.mvc.method.annotation.CustomTypeConditionProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.ProfileRestrictingRequestCondition;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Searches a method or type for the {@link RequireProfile} annotation. If it is found in combination with an {@link RequestMapping}
 * annotation, it will create a custom ProfileRestrictingRequestCondition which the RequestMappingHandlerMapping can
 * use to restrict incoming requests by. Use this provider as a plugin for the {@link nl.jhkuperus.org.springframework.web.servlet.mvc.method.annotation.CustomConditionProvidingRequestMappingHandlerMapping}
 * class to enable the use of the {@link RequireProfile} annotation.
 *  
 * @author J.H. Kuperus
 */
public class RequireProfileConditionProvider implements CustomMethodConditionProvider<ProfileRestrictingRequestCondition>, CustomTypeConditionProvider<ProfileRestrictingRequestCondition> {

    private static final String PROFILE_MEDIA_TYPE_PARAMETER_FORMAT = ";profile=";

    @Override
    public ProfileRestrictingRequestCondition detectMethodCondition(Method method) {
        RequireProfile requireProfile = AnnotationUtils.findAnnotation(method, RequireProfile.class);
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);

        return createCondition(requireProfile, requestMapping);
    }

    @Override
    public ProfileRestrictingRequestCondition detectTypeCondition(Class handlerType) {
        RequireProfile requireProfile = AnnotationUtils.findAnnotation(handlerType, RequireProfile.class);
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);

        return createCondition(requireProfile, requestMapping);
    }

    private ProfileRestrictingRequestCondition createCondition(RequireProfile requireProfile, RequestMapping requestMapping) {
        if (requireProfile != null) {
            if (requestMapping != null && requestMapping.produces() != null && requestMapping.produces().length > 0 && requireProfile.value().length > 0) {
                // Combine content-types from @RequestMapping with the profile(s) from @RequireProfile
                Set<String> combinedMediaTypesAndProfiles = new HashSet<String>();
                for (String profile : requireProfile.value()) {
                    for (String mediaType : requestMapping.produces()) {
                        combinedMediaTypesAndProfiles.add(mediaType + PROFILE_MEDIA_TYPE_PARAMETER_FORMAT + profile);
                    }
                }

                return new ProfileRestrictingRequestCondition(combinedMediaTypesAndProfiles.toArray(new String[combinedMediaTypesAndProfiles.size()]));
            }
            else {
                throw new IllegalArgumentException("Failed to register RequireProfile annotation, it can only be applied if a RequestMapping is also present");
            }
        }

        return null;
    }
}
