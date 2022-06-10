package de.freerider.restapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.freerider.datamodel.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@RestController
class CustomersController implements CustomersAPI {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;
    //
    private final HttpServletRequest request;

    public CustomersController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }


    @Override
    public ResponseEntity<List<?>> getCustomers() {
        ResponseEntity<List<?>> re;
        System.err.println(request.getMethod() + " " + request.getRequestURI());
        try {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            customerRepository.findAll().forEach(c -> {
                StringBuffer sb = new StringBuffer();
                c.getContacts().forEach(contact -> sb.append(sb.length() == 0 ? "" : "; ").append(contact));
                arrayNode.add(objectMapper.createObjectNode().put("id", c.getId()).put("name", c.getLastName()).put("first", c.getFirstName()).put("contacts", sb.toString()));
            });
            ObjectReader reader = objectMapper.readerFor(new TypeReference<List<ObjectNode>>() {
            });
            List<String> list = reader.readValue(arrayNode);
            re = new ResponseEntity<>(list, HttpStatus.OK);

        } catch (IOException e) {
            re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return re;
    }

    @Override
    public ResponseEntity<?> getCustomer(long id) {
        //Optional<JsonNode> jn = StreamSupport.stream(peopleAsJSON().spliterator(), false).filter(person -> Long.parseLong(String.valueOf(person.get("id"))) == id).findFirst();
        Optional<JsonNode> jn = (Optional<JsonNode>) getCustomers().getBody().stream().filter(person -> Long.parseLong(String.valueOf(((ObjectNode) person).get("id"))) == id).findFirst();
        return jn.isPresent() ? new ResponseEntity<>(jn, HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<List<?>> postCustomers(Map<String, Object>[] jsonMap) {
        System.err.println("POST /customers");
        if (jsonMap == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        List<Collection<Object>> badReq = new ArrayList<>();
        List<Collection<Object>> conflict = new ArrayList<>();
        System.out.println("[{");
        for (Map<String, Object> kvpairs : jsonMap) {

            try {
                Optional<Customer> customer = accept(kvpairs);
                if (customer.isPresent()) {
                    customerRepository.save(customer.get());
                } else {
                    conflict.add(kvpairs.values());
                }
                kvpairs.keySet().forEach(key -> {
                    Object value = kvpairs.get(key);
                    System.out.println(" [" + key + ", " + value + " ]");
                });
            } catch (IllegalArgumentException e) {
                badReq.add(kvpairs.values());
            }
        }
        System.out.println("]}");
        if (badReq.size() > 0) {
            badReq.addAll(conflict);
            return new ResponseEntity<>(badReq, HttpStatus.BAD_REQUEST);
        }
        if (conflict.size() > 0) {
            return new ResponseEntity<>(conflict, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    private Optional<Customer> accept(Map<String, Object> customerMap) throws IllegalArgumentException {
        String sId = (String) customerMap.getOrDefault("id", null);
        long id = (sId == null ? customerRepository.getFreeID() : Long.parseLong(sId));

        System.out.println(customerRepository.existsById(id));
        if (id <= 0 || (sId == null && customerRepository.existsById(id))) {
            return Optional.empty();
        }
        String first = (String) customerMap.getOrDefault("first", null);
        String name = (String) customerMap.getOrDefault("name", null);

        if (first == null || name == null) {
            throw new IllegalArgumentException();
        }
        Customer customer = new Customer()
                .setId(id)
                .setName(first, name);
        String[] contacts = ((String) customerMap.getOrDefault("contacts", "")).split(";");
        Arrays.stream(contacts).forEach(customer::addContact);
        return Optional.of(customer);

    }

    @Override
    public ResponseEntity<List<?>> putCustomers(Map<String, Object>[] jsonMap) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteCustomer(long id) {
        System.err.println("DELETE /customers/" + id);
        ResponseEntity<?> re;
        if (customerRepository.findById(id).isPresent()) {
            customerRepository.deleteById(id);
            re = new ResponseEntity<>(null, HttpStatus.ACCEPTED);

        } else {
            re = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return re;
    }
}