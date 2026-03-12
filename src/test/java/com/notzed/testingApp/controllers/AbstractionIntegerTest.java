package com.notzed.testingApp.controllers;


import com.notzed.testingApp.TestContainerConfiguration;
import com.notzed.testingApp.dto.EmployeeDto;
import com.notzed.testingApp.entities.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestContainerConfiguration.class)
public class AbstractionIntegerTest {

    @Autowired
    WebTestClient webTestClient;

    Employee testEmployee = Employee.builder()
            .email("zed@gmail.com")
                .name("zed")
                .salary(50000L)
                .build();

    EmployeeDto testEmployeeDto = EmployeeDto.builder()
            .email("zed@gmail.com")
                .name("zed")
                .salary(50000L)
                .build();

}
