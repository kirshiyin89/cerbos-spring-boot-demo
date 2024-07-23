package org.cerbos.demo;

import lombok.extern.slf4j.Slf4j;
import org.cerbos.demo.model.Employee;
import org.cerbos.demo.model.Profile;
import org.cerbos.demo.repository.EmployeeRepository;
import org.cerbos.demo.repository.ProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@ComponentScan(basePackages = "org.cerbos.demo")
@EnableJpaRepositories
@EnableAutoConfiguration
@Slf4j
public class Main {

    static ConfigurableApplicationContext appCtx;

    public static void main(String[] args) {
        var app = new SpringApplication(Main.class);
        appCtx = app.run(args);
    }

    @Bean
    CommandLineRunner commandLineRunner(EmployeeRepository employeeRepository, ProfileRepository profileRepository) {
        return args -> {
            populateDb(employeeRepository, profileRepository);
        };
    }

    void populateDb(EmployeeRepository employeeRepository, ProfileRepository profileRepository) {
        Employee employee1 = new Employee();
        employee1.setEmployeeId(123L);
        employee1.setName("John Doe");
        employee1.setEmail("john.doe@me.com");
        employee1.setRole("employee");
        employee1.setSalary(1500.0);

        Employee employee2 = new Employee();
        employee2.setEmployeeId(321L);
        employee2.setName("Marie Smith");
        employee2.setEmail("marie.smith@me.com");
        employee2.setRole("employee");
        employee2.setSalary(2000.0);

        Employee hr = new Employee();
        hr.setEmployeeId(456L);
        hr.setName("Andrew Anderson");
        hr.setEmail("andrew.anderson@me.com");
        hr.setRole("hr");
        hr.setSalary(1000.0);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        employeeRepository.save(hr);

        Profile profile = new Profile();
        profile.setId(111L);
        profile.setEmployee(employee1);
        profileRepository.save(profile);

        Profile profile2 = new Profile();
        profile2.setId(222L);
        profile2.setEmployee(employee2);
        profileRepository.save(profile2);

        Profile profile3 = new Profile();
        profile3.setId(333L);
        profile3.setEmployee(hr);
        profileRepository.save(profile3);

        log.debug("Saved data to db");
    }
}