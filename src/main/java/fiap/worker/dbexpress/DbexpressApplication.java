package fiap.worker.dbexpress;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class DbexpressApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbexpressApplication.class, args);
	}

}
