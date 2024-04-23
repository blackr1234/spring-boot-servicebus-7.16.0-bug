package code;

import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.core.http.policy.FixedDelayOptions;
import com.azure.core.http.policy.RetryOptions;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import com.azure.messaging.servicebus.administration.models.CreateRuleOptions;
import com.azure.messaging.servicebus.administration.models.CreateSubscriptionOptions;
import com.azure.messaging.servicebus.administration.models.TrueRuleFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MyController {
    public static final String CONN_STR = "Endpoint=sb://xxx.servicebus.windows.net/;SharedAccessKeyName=xxx;SharedAccessKey=xxx";
    public static final String TOPIC_NAME = "existing-topic";
    public static final String SUB_NAME = "some-subscription";

    @GetMapping("greet")
    public String greet() {
        log.info("Called /greet.");

        final ServiceBusAdministrationClient adminClient = createAdminClient(Integer.MAX_VALUE);

        adminClient.createSubscription(
            TOPIC_NAME, SUB_NAME, "default-filter",
            createSubscriptionOptions(), createRuleOptions());

        return "OK";
    }

    private ServiceBusAdministrationClient createAdminClient(int maxRetries) {
        return new ServiceBusAdministrationClientBuilder()
                    .connectionString(CONN_STR)
                    .retryOptions(createRetryOptions(maxRetries))
                    .buildClient();
    }

    public CreateSubscriptionOptions createSubscriptionOptions() {
        final CreateSubscriptionOptions options = new CreateSubscriptionOptions();
        options.setAutoDeleteOnIdle(Duration.ofMinutes(5));
        options.setMaxDeliveryCount(3);

        return options;
    }

    public RetryOptions createRetryOptions(int maxRetries) {
        return new RetryOptions(
                new FixedDelayOptions(maxRetries, Duration.ofSeconds(7)));
    }

    public CreateRuleOptions createRuleOptions() {
        final CreateRuleOptions options = new CreateRuleOptions();
        options.setFilter(new TrueRuleFilter());

        return options;
    }
}