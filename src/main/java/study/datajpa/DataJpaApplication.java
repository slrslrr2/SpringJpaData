package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
// @EnableJpaRepositories(basePackageClasses = "study.datajpa.repository")
// Spring FrameWork는 해당 어노테이션 선언해줘야함
// SpringBoot는 자동으로 해준다.
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		// 원래는 스프링 시큐리티 서비스 세션 가지고와서 id, name 조회하여 BaseEntity 채워줌
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
