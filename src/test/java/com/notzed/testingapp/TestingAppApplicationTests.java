package com.notzed.testingapp;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.assertj.core.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
class TestingAppApplicationTests {

    @BeforeAll
    static void setUp(){
        log.info("Starting the method, setting up config");
    }

    @AfterAll
    static void tearDown(){
        log.info("Tearing down the method");
    }

    @Test
    void testNumberOne() {
        int a = 5;
        int b = 0;

        Assertions.assertThatThrownBy(() -> divideTwoIntegers(a, b))
                        .isInstanceOf(ArithmeticException.class)
                .hasMessage("Tried to divide by zero");


//        Assertions.assertEquals(8, result);
//        Assertions.
//
//        assertThat(result)
//                .isEqualTo(8)
//                .isCloseTo(9, Offset.offset(1));
//
//        assertThat("Apple")
//                .isEqualTo("Apple")
//                .startsWith("App")
//                .endsWith("ple")
//                .hasSize(5);

    }

    @Test
    void testNumberTwo(){
        log.info("test two is run");
        int a = 2;
        int b = 2;
        double result = divideTwoIntegers(a, b);
        System.out.println(result);



    }

    int addTwoNumbers(int a, int b){
        return a+b;
    }

    double divideTwoIntegers(int a, int b){
        try{
            return (double) a/b;
        }catch (ArithmeticException e){
            log.error("Arithmetic Exception occurred"+e.getLocalizedMessage());
            throw new ArithmeticException(e.getLocalizedMessage());
        }
    }
}
