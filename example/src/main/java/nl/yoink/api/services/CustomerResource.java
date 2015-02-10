package nl.yoink.api.services;

import nl.yoink.api.domain.base.AbstractCustomer;
import nl.yoink.api.domain.v1.Customer;
import nl.yoink.api.domain.v2.Phonenumber;
import nl.yoink.org.springframework.web.bind.annotation.RequireProfile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by x077411 on 2/2/2015.
 */
@RestController
@RequestMapping("/customer/{id}")
public class CustomerResource {

    @RequestMapping(produces = {"application/hal+json"})
    @RequireProfile("nl.yoink.Customer.v1")
    public Customer getCustomer(@PathVariable Long id) {

        Customer customer = loadCustomer(new Customer(), id);

        customer.phonenumber = "+31612345678";

        return customer;

    }

    @RequestMapping(produces = {"application/hal+json"})
    @RequireProfile("nl.yoink.Customer.v2")
    public nl.yoink.api.domain.v2.Customer getCustomerV2(@PathVariable Long id) {

        nl.yoink.api.domain.v2.Customer customer = loadCustomer(new nl.yoink.api.domain.v2.Customer(), id);
        
        // Perform a tweak to the data that was introduced in V2
        customer.phonenumber = new Phonenumber();
        customer.phonenumber.countryCode = "+31";
        customer.phonenumber.number = "0612345678"; 
        
        return customer;
        
    }
    
    @RequestMapping
    @ResponseStatus(HttpStatus.MULTIPLE_CHOICES)
    public void getCustomerNoVersion(@PathVariable Long id) {
        // You can either choose to call one of the others, or return
        
    }

    /**
     * This method loads a Customer into the specified domain object. This method should only concern itself
     * with loading properties which are shared between all versions of the domain object. Any caller should
     * be responsible for creating version-specific adjustments. 
     * * 
     * @param customer the Customer object to apply the settings to
     * @param id the ID of the customer to load
     * @param <T> captured type of the actual domain object, allowing the method to be used with any version 
     *            of the Customer object, without requiring casts.
     * @return the loaded Customer object.
     */
    private <T extends AbstractCustomer> T loadCustomer(T customer, long id) {
        // Do database/webservice call and apply properties
        
        customer.id = id;
        customer.firstName = "Piet";
        customer.lastName  = "Pietersen";
        
        return customer;
    }

}
