# spring-webmvc-profile-conditions
Provides a new annotation @RequireProfile which allows a @RequestMapping's produces-expression to be restricted to certain profiles.

# REST-APIs and Versioning
Using a profile to version your representations is now possible in a Spring WebMVC environment.

## The problem
Spring's `@RequestMapping` annotation allows you to restrict request matching to the `Accept` header through the `produces` parameter.
However, by default, the `RequestMappingHandlerMapping` does not take media type parameters into account. Due to this fact, a request
attempting to negotiate the content based on a profile is not guaranteed to end up at the correct method.

Take for example these methods definitions:

```java
@RequestMapping(value = "/customer/{id}", produces = "application/hal+json;profile=nl.yoink.Customer.v1")
public Customer getCustomer(@PathVariable Long id) {
    // ...
}

@RequestMapping(value = "/customer/{id}", produces = "application/hal+json;profile=nl.yoink.Customer.v2")
public CustomerV2 getCustomerV2(@PathVariable Long id) {
    // ...
}
```

Spring will allow you to define these mappings, but will only ever use a single one. Since the `produces`-expressions are syntactically
different, Spring does not see them as ambiguous. When it comes to mapping a request's `Accept` header, it matches the first mapping it
encounters, because it will compare the two media types without parameters.

## The solution

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