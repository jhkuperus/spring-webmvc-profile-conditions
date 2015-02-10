# spring-webmvc-profile-conditions
Provides a new annotation @RequireProfile which allows a @RequestMapping's produces-expression to be restricted to certain profiles.

# REST-APIs and Versioning
Using a profile to version your representations is now possible in a Spring WebMVC environment.

Register the CustomConditionProvidingRequestMappingHandlerMapping instead of the regular RequestMappingHandlerMapping
and provide it with the RequireProfileConditionProvider. You can now use the @RequireProfile to further restrict
method selection:

```java
@RequestMapping(value = "/customer/{id}", produces = "application/json")
@RequireProfile("nl.yoink.Customer.v1")
public Customer getCustomer(@PathVariable Long id) {
    // ...
}
```

TODO : Add more elaborate example