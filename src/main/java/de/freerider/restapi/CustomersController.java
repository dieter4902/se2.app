package de.freerider.restapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RestController
class CustomersController implements CustomersAPI {

    @Autowired
    private ApplicationContext context;
    private final ObjectMapper objectMapper;
    //
    private final HttpServletRequest request;

    public CustomersController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }


    @Override
    public ResponseEntity<List<?>> getCustomers() {
        ResponseEntity<List<?>> re = null;
        System.err.println(request.getMethod() + " " + request.getRequestURI());
        try {
            ArrayNode arrayNode = peopleAsJSON();
            ObjectReader reader = objectMapper.readerFor(new TypeReference<List<ObjectNode>>() {
            });
            List<String> list = reader.readValue(arrayNode);
            //
            re = new ResponseEntity<>(list, HttpStatus.OK);

        } catch (IOException e) {
            re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return re;
    }

    @Override
    public ResponseEntity<?> getCustomer(long id) {

        Optional<JsonNode> jn = StreamSupport.stream(peopleAsJSON().spliterator(), false).filter(person -> Long.parseLong(String.valueOf(person.get("id"))) == id).findFirst();
        return jn.isPresent() ? new ResponseEntity<>(jn, HttpStatus.OK) : new ResponseEntity<String>(HttpStatus.NOT_FOUND);

/*
        for (JsonNode jon : peopleAsJSON()) {
            if (id == Long.parseLong(String.valueOf(jon.get("id")))) {
                re = new ResponseEntity<>(jon, HttpStatus.OK);
                break;
            }
        }
        return re;

        System.err.println(request.getMethod() + " " + request.getRequestURI());
        if (userCount >= id) {
            Optional<Person> person = people.stream().filter(pers -> pers.id == id).findFirst();
            if (person.isPresent()) {
                ObjectNode objectNode = personAsJson(person);
                re = new ResponseEntity<>(objectNode.toString(), HttpStatus.OK);
            }
        }
        return re;
*/
    }


    /**
     * GET /server/stop
     * <p>
     * Stop sever and shut down application.
     *
     * @return
     */
    public ResponseEntity<Void> stop() {
        //
        try {
            System.err.print(request.getRequestURI() + " " + request.getMethod() + "\nshutting down server...");
            //
            ApplicationContext context = this.context;
            ((ConfigurableApplicationContext) context).close();
            //
            System.err.println("  done.");
            //
            return new ResponseEntity<Void>(HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* ... */

    long userCount = 0;

    class Person {
        String firstName = "";
        String lastName = "";
        long id;


        final List<String> contacts = new ArrayList<String>();


        Person() {
            this.id = ++userCount;
        }

        Person setName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            return this;
        }

        Person addContact(String contact) {
            this.contacts.add(contact);
            return this;
        }

    }

    private final CustomersController.Person eric = new CustomersController.Person()
            .setName("Eric", "Meyer")
            .addContact("eric98@yahoo.com")
            .addContact("(030) 3945-642298");
    //
    private final CustomersController.Person anne = new CustomersController.Person()
            .setName("Anne", "Bayer")
            .addContact("anne24@yahoo.de")
            .addContact("(030) 3481-23352");
    //
    private final CustomersController.Person tim = new CustomersController.Person()
            .setName("Tim", "Schulz-Mueller")
            .addContact("tim2346@gmx.de");


    private final List<CustomersController.Person> people = Arrays.asList(eric, anne, tim);

    private ArrayNode peopleAsJSON() {
        //
        ArrayNode arrayNode = objectMapper.createArrayNode();
        //
        people.forEach(c -> {
            StringBuffer sb = new StringBuffer();
            c.contacts.forEach(contact -> sb.append(sb.length() == 0 ? "" : "; ").append(contact));
            arrayNode.add(
                    objectMapper.createObjectNode()
                            .put("id", c.id)
                            .put("name", c.lastName)
                            .put("first", c.firstName)
                            .put("contacts", sb.toString())
            );
        });
        return arrayNode;
    }

    private ObjectNode personAsJson(Optional<Person> person) {
        ObjectNode on = objectMapper.createObjectNode();
        StringBuffer sb = new StringBuffer();
        person.get().contacts.forEach(contact -> sb.append(sb.length() == 0 ? "" : "; ").append(contact));
        on.put("id", person.get().id)
                .put("name", person.get().lastName)
                .put("first", person.get().id)
                .put("contacts", sb.toString());
        return on;
    }
}