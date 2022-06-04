package de.freerider.repository;

import de.freerider.datamodel.Customer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerRepository implements CrudRepository<Customer, Long> {

    private final HashMap<Long, Customer> CustomerList;

    public CustomerRepository() {
        CustomerList = new HashMap<>();
    }

    @Override
    public <S extends Customer> S save(S entity) {
        CustomerList.putIfAbsent(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Customer> Iterable<S> saveAll(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    @Override
    public boolean existsById(Long aLong) {
        Customer c = CustomerList.get(aLong);
        return c != null;
    }

    public Long getFreeID() {
        long prev = 0L;
        for (Long l : CustomerList.keySet()) {
            prev += 1;
            if (prev + 1 < l) {
                break;
            }
        }
        return prev + 1;
    }

    @Override
    public Optional<Customer> findById(Long aLong) {
        Customer c = CustomerList.get(aLong);
        return c == null ? Optional.empty() : Optional.of(c);
    }

    @Override
    public Iterable<Customer> findAll() {
        return CustomerList.values();
    }

    @Override
    public Iterable<Customer> findAllById(Iterable<Long> longs) {
        List<Customer> customers = new ArrayList<>();
        for (Long id : longs) {
            Optional<Customer> customer = findById(id);
            customer.ifPresent(customers::add);
        }
        return customers;
    }

    @Override
    public long count() {
        return CustomerList.size();
    }

    @Override
    public void deleteById(Long aLong) {
        CustomerList.remove(aLong);
    }

    @Override
    public void delete(Customer entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        for (Long id : longs) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Customer> entities) {
        for (Customer customer : entities) {
            delete(customer);
        }
    }

    @Override
    public void deleteAll() {
        CustomerList.clear();
    }
}
