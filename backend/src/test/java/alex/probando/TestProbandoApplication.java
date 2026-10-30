package alex.probando;

import org.springframework.boot.SpringApplication;

public class TestProbandoApplication {

	public static void main(String[] args) {
		SpringApplication.from(ProbandoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
