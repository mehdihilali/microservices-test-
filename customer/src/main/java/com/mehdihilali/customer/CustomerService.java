package com.mehdihilali.customer;

import com.mehdihilali.clients.fraud.FraudCheckResponse;
import com.mehdihilali.clients.fraud.FraudClient;
import com.mehdihilali.clients.notification.NotificationClient;
import com.mehdihilali.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {

    private final RestTemplate restTemplate;

    private final CustomerRepository customerRepository;

    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        // todo: check if email valid
        // todo: check if email not taken

        //store customer in db
        //We use saveAndFlush for this reason : We can have access to the customerId
        customerRepository.save(customer);

        // todo: check if fraudster

        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }

        // todo: send notification
        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s , welcome to this test ... ", customer.getFirstName())
                )
        );
    }
}
