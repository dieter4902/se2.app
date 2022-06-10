package de.freerider.repository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import de.freerider.datamodel.Customer;

@Repository
@Component
public interface CustomerRepository extends
        org.springframework.data.repository.CrudRepository<Customer, Long> {

}
