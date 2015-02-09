package nl.jhkuperus.org.springframework.web.bind.annotation;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.ProfileRestrictingRequestCondition;

import static org.junit.Assert.*;

public class RequireProfileConditionProviderTest {

    private RequireProfileConditionProvider classUnderTest;

    @Before
    public void setup() {

        classUnderTest = new RequireProfileConditionProvider();

    }
    
    @Test
    public void testValidRequestMappingAndRequireProfile() throws NoSuchMethodException {

        ProfileRestrictingRequestCondition typeCondition = classUnderTest.detectTypeCondition(ValidMappings.class);
        assertNotNull(typeCondition);

        ProfileRestrictingRequestCondition methodCondition = classUnderTest.detectMethodCondition(ValidMappings.class.getMethod("testMapping"));
        assertNotNull(methodCondition);

    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRequiresRequestMappingOnType() throws NoSuchMethodException {

        ProfileRestrictingRequestCondition typeCondition = classUnderTest.detectTypeCondition(MissingRequestMapping.class);
        assertNotNull(typeCondition);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRequiresRequestMappingOnMethod() throws NoSuchMethodException {

        ProfileRestrictingRequestCondition methodCondition = classUnderTest.detectMethodCondition(MissingRequestMapping.class.getMethod("testMapping"));
        assertNotNull(methodCondition);
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiresProducesMappingOnType() throws NoSuchMethodException {

        ProfileRestrictingRequestCondition typeCondition = classUnderTest.detectTypeCondition(MissingProduces.class);
        assertNotNull(typeCondition);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiresProducesMappingOnMethod() throws NoSuchMethodException {

        ProfileRestrictingRequestCondition methodCondition = classUnderTest.detectMethodCondition(MissingProduces.class.getMethod("testMapping"));
        assertNotNull(methodCondition);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiresProfilesMappingOnType() throws NoSuchMethodException {

        ProfileRestrictingRequestCondition typeCondition = classUnderTest.detectTypeCondition(MissingProfiles.class);
        assertNotNull(typeCondition);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRequiresProfilesMappingOnMethod() throws NoSuchMethodException {

        ProfileRestrictingRequestCondition methodCondition = classUnderTest.detectMethodCondition(MissingProfiles.class.getMethod("testMapping"));
        assertNotNull(methodCondition);

    }

    @RequestMapping(produces = "application/json")
    @RequireProfile("nl.jhkuperus.Test")
    private static class ValidMappings {
        
        @RequestMapping(produces = "application/json")
        @RequireProfile("nl.jhkuperus.Test")
        public void testMapping() {

        }
        
    }
    
    @RequireProfile("nl.jhkuperus.Test")
    private static class MissingRequestMapping {
        
        @RequireProfile("nl.jhkuperus.Test")
        public void testMapping() {
            
        }
        
    }
    
    @RequestMapping
    @RequireProfile("nl.jhkuperus.Test")
    private static class MissingProduces {

        @RequestMapping
        @RequireProfile("nl.jhkuperus.Test")
        public void testMapping() {

        }

    }
    
    @RequestMapping(produces = "application/json")
    @RequireProfile({})
    public static class MissingProfiles {
        
        @RequestMapping(produces = "application/json")
        @RequireProfile({})
        public void testMapping() {

        }
        
    }
}