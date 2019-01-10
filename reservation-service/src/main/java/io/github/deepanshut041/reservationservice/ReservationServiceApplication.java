package io.github.deepanshut041.reservationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@EnableDiscoveryClient
@SpringBootApplication
@EnableBinding(Sink.class)
public class ReservationServiceApplication {

	@Bean
	CommandLineRunner commandLineRunner(ReservationRepository reservationRepository){
		return strings -> {
			Stream.of("hdhd", "djdj", "dkdjd", "djfh", "hfhf").forEach(
					n-> reservationRepository.save(new Reservation(n))
			);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}


}

@MessageEndpoint
class ReservationProcessor{

	@Autowired
	private ReservationRepository reservationRepository;

	@ServiceActivator(inputChannel = Sink.INPUT)
	public void acceptNewReservation(String rn){
		this.reservationRepository.save(new Reservation(rn));
	}
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {
	@RestResource(path = "by-name")
	Collection<Reservation> findByReservationName(@Param("name")String name);
}

@RefreshScope
@RestController
class MessageController{

	@Value("${message}")
	private String msg;

	@RequestMapping("/message")
	String message(){
		return this.msg;
	}
}


@Entity
class Reservation{

	@Id
	@GeneratedValue
	private Long id;
	private String reservationName;

	public Reservation(String reservationName) {
		this.reservationName = reservationName;
	}

	//	 Jpa constructor
	public Reservation() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReservationName() {
		return reservationName;
	}

	public void setReservationName(String reservationName) {
		this.reservationName = reservationName;
	}

	@Override
	public String toString() {
		return "Reservation{" + "id=" + id + ", reservationName='" + reservationName + "'}";
	}
}

