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
    public <S extends Customer> S save(S entity) throws IllegalArgumentException {
        checkIfSingleNull(entity);
        CustomerList.putIfAbsent(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends Customer> Iterable<S> saveAll(Iterable<S> entities) throws IllegalArgumentException {
        checkIfSingleNull(entities);
        for (S entity : entities) {
            save(entity);
        }
        return entities;
    }

    @Override
    public boolean existsById(Long aLong) throws IllegalArgumentException {
        checkIfSingleNull(aLong);
        Customer c = CustomerList.get(aLong);
        return c != null;
    }

    public Long getFreeID() {
        long val = 0L;
        for (Customer customer : CustomerList.values()) {
            val += 1;
            if (val < customer.getId()) {
                return val;
            }
        }
        return val + 1;
    }

    @Override
    public Optional<Customer> findById(Long aLong) throws IllegalArgumentException {
        checkIfSingleNull(aLong);
        Customer c = CustomerList.get(aLong);
        return c == null ? Optional.empty() : Optional.of(c);
    }

    @Override
    public Iterable<Customer> findAll() {
        return CustomerList.values();
    }

    @Override
    public Iterable<Customer> findAllById(Iterable<Long> longs) throws IllegalArgumentException {
        checkIfSingleNull(longs);
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
    public void deleteById(Long aLong) throws IllegalArgumentException {
        checkIfSingleNull(aLong);
        CustomerList.remove(aLong);
    }

    @Override
    public void delete(Customer entity) throws IllegalArgumentException {
        checkIfSingleNull(entity);
        deleteById(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) throws IllegalArgumentException {
        checkIfSingleNull(longs);
        for (Long id : longs) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Customer> entities) throws IllegalArgumentException {
        checkIfSingleNull(entities);
        for (Customer customer : entities) {
            delete(customer);
        }
    }

    private void checkIfSingleNull(Object o) throws IllegalArgumentException {
        if (o == null) throw new IllegalArgumentException();
    }

    @Override
    public void deleteAll() {
        CustomerList.clear();
    }
}
