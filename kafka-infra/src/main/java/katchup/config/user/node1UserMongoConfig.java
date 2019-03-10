package katchup.config.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "katchup.Users.repository.node1",
        mongoTemplateRef = "node1UserMongoTemplate")
public class node1UserMongoConfig {
}
