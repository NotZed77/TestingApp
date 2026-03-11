package com.notzed.testingApp.services.impl;

import com.notzed.testingApp.TestContainerConfiguration;
import com.notzed.testingApp.dto.EmployeeDto;
import com.notzed.testingApp.entities.Employee;
import com.notzed.testingApp.exceptions.ResourceNotFoundException;
import com.notzed.testingApp.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee mockEmployee;
    private EmployeeDto mockEmployeeDto;

    @BeforeEach
    void setUp(){
        mockEmployee = Employee.builder()
                .id(1L)
                .name("zeds")
                .email("zeds@gmail.com")
                .salary(50000L)
                .build();
        mockEmployeeDto = modelMapper.map(mockEmployee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_whenEmployeeIdIsPresent_thenReturnEmployeeDto(){
        // Assign
        Long id = mockEmployee.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));

        // Act
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);


        // Assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(id);
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
        verify(employeeRepository, only()).findById(id);
    }

    @Test
    void testGetEmployeeById_whenEmployeeIsNotPresent_thenThrowException(){
        // Assign
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateNewEmployee_whenValidEmployee_ThenCreateNewEmployee(){
        // Assign
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        // Act
        EmployeeDto employeeDto = employeeService.createNewEmployee(mockEmployeeDto);

        // Assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployeeDto.getEmail());

        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee capturedEmployee = employeeArgumentCaptor.getValue();
        assertThat(capturedEmployee.getEmail()).isEqualTo(mockEmployee.getEmail());
    }

    @Test
    void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowException(){
        // Arrange
        when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployee));

        // Act & Assert
        assertThatThrownBy(() -> employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockEmployee.getEmail());

        verify(employeeRepository).findByEmail(mockEmployeeDto.getEmail());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExist_thenThrowException(){
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & assert
        assertThatThrownBy(() -> employeeService.updateEmployee(1L, mockEmployeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("zedsa");
        mockEmployee.setEmail("random@gmail.com");

        assertThatThrownBy(() -> employeeService.updateEmployee(mockEmployee.getId(),mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployee.getId());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee(){
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("Random");
        mockEmployeeDto.setSalary(55000L);

        Employee newEmployee = modelMapper.map(mockEmployeeDto, Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        // Act
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(mockEmployee.getId(), mockEmployeeDto);

        // Assert
        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeDto);
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExist_thenThrowException(){
        when(employeeRepository.existsById(1L)).thenReturn(false);

        // Act
        assertThatThrownBy(() -> employeeService.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + 1L);

        verify(employeeRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee(){
        // Arrange
        when(employeeRepository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> employeeService.deleteEmployee(1L))
                        .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(1L);
    }
}