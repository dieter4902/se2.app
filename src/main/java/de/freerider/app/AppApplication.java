package de.freerider.app;

import de.freerider.datamodel.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ComponentScan(
        basePackages = {"de.freerider.restapi", "de.freerider.repository"}
)
public class AppApplication {

    @Autowired
    private CustomerRepository customerRepository;

    public static void main(String[] args) {
        System.out.println("Hello, freerider.de");
        SpringApplication.run(AppApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStringStartup() {
        customerRepository.save(new Customer()
                .setId(1).setName("Eric", "Meyer").addContact("eric98@yahoo.com")
                .addContact("(030) 7000640000")
        );
        customerRepository.save(new Customer()
                .setId(2).setName("Anne", "Bayer").addContact("anne24@yahoo.de")
                .addContact("(030) 3481-23352")
        );
        customerRepository.save(new Customer()
                .setId(3).setName("Tim", "Schulz-Mueller").addContact("tim2346@gmx.de")
        );
        long count = customerRepository.count();
        System.out.println("repository<Customer> with: " + count + " entries");
    }
}
