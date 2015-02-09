package org.springframework.web.servlet.mvc.condition;

import org.junit.Test;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertTrue;

public class ProfileRestrictingRequestConditionTest {

    @Test
    public void testComparator() {
        MediaType bla = MediaType.parseMediaType("application/json;profile=bla");
        MediaType blaW = MediaType.parseMediaType("application/json;profile=bla*");
        MediaType blaaa = MediaType.parseMediaType("application/json;profile=blaaa");
        MediaType blaaaW = MediaType.parseMediaType("application/json;profile=blaaa*");
        MediaType reutel = MediaType.parseMediaType("application/json;profile=reutel");
        MediaType reutelW = MediaType.parseMediaType("application/json;profile=reutel*");

        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(bla, bla) == 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(bla, blaW) < 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaW, bla) > 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaW, blaW) == 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaaa, bla) > 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaW, blaaaW) > 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaW, blaaa) > 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(bla, blaaaW) < 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(reutel, bla) > 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(bla, reutel) < 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(reutelW, bla) > 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(bla, reutelW) < 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(reutel, blaW) > 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaW, reutel) < 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaaaW, bla) > 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaaa, blaW) < 0);
        assertTrue(ProfileRestrictingRequestCondition.PROFILE_COMPARATOR.compare(blaaaW, blaW) < 0);
    }

}