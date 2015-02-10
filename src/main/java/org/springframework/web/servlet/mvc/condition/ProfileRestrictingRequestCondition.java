package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * This custom RequestCondition allows for the restriction of an Accept-header's MediaType to include a profile.
 * Note that this condition is placed inside the actual Spring framework package, so it can access the already
 * existing code for ProducesRequestCondition. Sadly, that class is final and so this condition includes a lot
 * of literally copy-pasted code from Spring's ProducesRequestCondition.
 * 
 * @author J.H. Kuperus
 */
public class ProfileRestrictingRequestCondition extends AbstractRequestCondition<ProfileRestrictingRequestCondition> {

    public static final String PROFILE_PARAMETER = "profile";
    public static final String PROFILE_WILDCARD_POSTFIX = "*";

    private final List<ProfileMediaTypeExpression> PROFILE_MATCH_ALL = Collections.singletonList(new ProfileMediaTypeExpression("*/*"));

    private final List<ProfileMediaTypeExpression> profileMatchExpressions;

    private final ContentNegotiationManager contentNegotiationManager;

    public ProfileRestrictingRequestCondition(String... profileMatches) {
        this(profileMatches, null);
    }

    public ProfileRestrictingRequestCondition(String[] profiles, ContentNegotiationManager manager) {
        this.profileMatchExpressions = new ArrayList<ProfileMediaTypeExpression>(parseExpressions(profiles));
        Collections.sort(this.profileMatchExpressions);
        this.contentNegotiationManager = (manager != null) ? manager : new ContentNegotiationManager();
    }

    /**
     * Private constructor with already parsed media type expressions.
     */
    private ProfileRestrictingRequestCondition(Collection<ProfileMediaTypeExpression> expressions, ContentNegotiationManager manager) {
        this.profileMatchExpressions = new ArrayList<ProfileMediaTypeExpression>(expressions);
        Collections.sort(this.profileMatchExpressions);
        this.contentNegotiationManager = (manager != null) ? manager : new ContentNegotiationManager();
    }

    private Set<ProfileMediaTypeExpression> parseExpressions(String[] profileMatches) {
        Set<ProfileMediaTypeExpression> result = new LinkedHashSet<ProfileMediaTypeExpression>();

        if (profileMatches != null && profileMatches.length > 0) {
            for (String profileMatch : profileMatches) {
                result.add(new ProfileMediaTypeExpression(profileMatch));
            }
        }
        else {
            result.addAll(PROFILE_MATCH_ALL);
        }

        return result;
    }

    @Override
    protected Collection<?> getContent() {
        return this.profileMatchExpressions;
    }

    @Override
    protected String getToStringInfix() {
        return " || ";
    }

    /**
     * Method-level conditions override type-level conditions.
     */
    @Override
    public ProfileRestrictingRequestCondition combine(ProfileRestrictingRequestCondition other) {
        return !other.profileMatchExpressions.isEmpty() ? other : this;
    }

    @Override
    public ProfileRestrictingRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (this.profileMatchExpressions.isEmpty()) {
            return this;
        }

        Set<ProfileMediaTypeExpression> result = new LinkedHashSet<ProfileMediaTypeExpression>(profileMatchExpressions);
        for (Iterator<ProfileMediaTypeExpression> iterator = result.iterator(); iterator.hasNext();) {
            ProfileMediaTypeExpression expression = iterator.next();
            if (!expression.match(request)) {
                iterator.remove();
            }
        }

        return (result.isEmpty()) ? null : new ProfileRestrictingRequestCondition(result, this.contentNegotiationManager);
    }

    /**
     * Compares this and another "produces" condition as follows:
     * <ol>
     * <li>Sort 'Accept' header media types by quality value via
     * {@link MediaType#sortByQualityValue(List)} and iterate the list.
     * <li>Get the first index of matching media types in each "produces"
     * condition first matching with {@link MediaType#equals(Object)} and
     * then with {@link MediaType#includes(MediaType)}.
     * <li>If a lower index is found, the condition at that index wins.
     * <li>If both indexes are equal, the media types at the index are
     * compared further with {@link MediaType#SPECIFICITY_COMPARATOR}.
     * </ol>
     * <p>It is assumed that both instances have been obtained via
     * {@link #getMatchingCondition(HttpServletRequest)} and each instance
     * contains the matching producible media type expression only or
     * is otherwise empty.
     */
    @Override
    public int compareTo(ProfileRestrictingRequestCondition other, HttpServletRequest request) {
        try {
            List<MediaType> acceptedMediaTypes = getAcceptedMediaTypes(request);
            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                int thisIndex = this.indexOfEqualMediaType(acceptedMediaType);
                int otherIndex = other.indexOfEqualMediaType(acceptedMediaType);
                int result = compareMatchingMediaTypes(this, thisIndex, other, otherIndex);
                if (result != 0) {
                    return result;
                }
                thisIndex = this.indexOfIncludedMediaType(acceptedMediaType);
                otherIndex = other.indexOfIncludedMediaType(acceptedMediaType);
                result = compareMatchingMediaTypes(this, thisIndex, other, otherIndex);
                if (result != 0) {
                    return result;
                }
            }

            return 0;
        }
        catch (HttpMediaTypeNotAcceptableException ex) {
            // should never happen ... (lol, even the Spring guys do this ^_^)
            throw new IllegalStateException("Cannot compare without having any requested media types", ex);
        }
    }

    // Shameless copies of ProducesRequestCondition
    private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        List<MediaType> mediaTypes = this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
        return mediaTypes.isEmpty() ? Collections.singletonList(MediaType.ALL) : mediaTypes;
    }

    private int indexOfEqualMediaType(MediaType mediaType) {
        for (int i = 0; i < getExpressionsToCompare().size(); i++) {
            MediaType currentMediaType = getExpressionsToCompare().get(i).getMediaType();
            if (mediaType.getType().equalsIgnoreCase(currentMediaType.getType()) &&
                            mediaType.getSubtype().equalsIgnoreCase(currentMediaType.getSubtype())) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfIncludedMediaType(MediaType mediaType) {
        for (int i = 0; i < getExpressionsToCompare().size(); i++) {
            if (mediaType.includes(getExpressionsToCompare().get(i).getMediaType())) {
                return i;
            }
        }
        return -1;
    }

    private int compareMatchingMediaTypes(ProfileRestrictingRequestCondition condition1, int index1,
                    ProfileRestrictingRequestCondition condition2, int index2) {

        int result = 0;
        if (index1 != index2) {
            result = index2 - index1;
        }
        else if (index1 != -1) {
            ProfileMediaTypeExpression expr1 = condition1.getExpressionsToCompare().get(index1);
            ProfileMediaTypeExpression expr2 = condition2.getExpressionsToCompare().get(index2);
            result = expr1.compareTo(expr2);
            result = (result != 0) ? result : expr1.getMediaType().compareTo(expr2.getMediaType());
        }
        return result;
    }

    /**
     * Return the contained "produces" expressions or if that's empty, a list
     * with a {@code MediaType_ALL} expression.
     */
    private List<ProfileMediaTypeExpression> getExpressionsToCompare() {
        return (this.profileMatchExpressions.isEmpty() ? PROFILE_MATCH_ALL : this.profileMatchExpressions);
    }


    class ProfileMediaTypeExpression extends AbstractMediaTypeExpression {


        ProfileMediaTypeExpression(MediaType mediaType, boolean negated) { super(mediaType, negated); }

        ProfileMediaTypeExpression(String expression) { super(expression); }

        @Override
        protected boolean matchMediaType(HttpServletRequest request) throws HttpMediaTypeException {
            List<MediaType> acceptedMediaTypes = getAcceptedMediaTypes(request);
            String requiredProfile = (StringUtils.isEmpty(getMediaType().getParameter(PROFILE_PARAMETER))) ? "*" : getMediaType().getParameter(PROFILE_PARAMETER);
            boolean isWildcardedProfile = isWildcardedProfile(requiredProfile);
            String profileLiteralPart = (isWildcardedProfile) ? requiredProfile.substring(-1) : requiredProfile;

            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                String profileParameter = acceptedMediaType.getParameter(PROFILE_PARAMETER);
                if (profileParameter != null && getMediaType().isCompatibleWith(acceptedMediaType) && ((isWildcardedProfile) ? profileParameter.startsWith(profileLiteralPart) : profileParameter.equals(profileLiteralPart))) {
                    return true;
                }
            }

            return false;
        }

        private boolean isWildcardedProfile(String profile) {
            return profile.endsWith(PROFILE_WILDCARD_POSTFIX);
        }

        @Override
        public int compareTo(AbstractMediaTypeExpression other) {
            return PROFILE_COMPARATOR.compare(this.getMediaType() , other.getMediaType());
        }
    }

    public static final Comparator<MediaType> PROFILE_COMPARATOR = new MimeType.SpecificityComparator<MediaType>() {
        @Override
        protected int compareParameters(MediaType mediaType1, MediaType mediaType2) {
            String profile1 = mediaType1.getParameter(PROFILE_PARAMETER);
            boolean profile1IsWildcard = profile1.endsWith(PROFILE_WILDCARD_POSTFIX);
            String profile1LiteralPart = (profile1IsWildcard) ? profile1.substring(0, profile1.length() - 1) : profile1;

            String profile2 = mediaType2.getParameter(PROFILE_PARAMETER);
            boolean profile2IsWildcard = profile2.endsWith(PROFILE_WILDCARD_POSTFIX);
            String profile2LiteralPart = (profile2IsWildcard) ? profile2.substring(0, profile2.length() - 1) : profile2;

            int literalPartComparison = profile1LiteralPart.compareTo(profile2LiteralPart);

            if (literalPartComparison == 0) {
                // If the literal parts are equal, the only thing that may make a difference is whether a profile
                // is wildcarded are not. Since we want wildcarded profiles lower than specific ones, this trick takes care of that.
                int wildcardComparison = (profile1IsWildcard ? PROFILE_WILDCARD_POSTFIX : "").compareTo(profile2IsWildcard ? PROFILE_WILDCARD_POSTFIX : "");

                // If they two are completely the same, use the specificity comparison of the parent class
                return (wildcardComparison != 0) ? wildcardComparison : super.compareParameters(mediaType1, mediaType2);
            }

            // If the literal parts are not equal, there are a few cases to take into account
            // a) one is a substring of the other
            // aa) the shorter string literal is not a wildcard -> the shorter one on top
            // ab) the shorter string literal is a wildcard -> the shorter one on bottom (more specific wildcards/values get sorted up)
            // b) they are completely different -> normal comparison
            if (profile1.startsWith(profile2LiteralPart)) {
                return (profile2IsWildcard) ? -1 : 1;
            }
            else if (profile2.startsWith(profile1LiteralPart)) {
                return (profile1IsWildcard) ? 1 : -1;
            }
            else {
                // Whether one or the other is a wildcard is of no interest in this case
                return literalPartComparison;
            }
        }
    };
}
