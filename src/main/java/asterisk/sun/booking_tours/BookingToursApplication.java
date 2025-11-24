package asterisk.sun.booking_tours;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class BookingToursApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingToursApplication.class, args);
	}

}
