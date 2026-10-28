package com.example.testdrivesservice.controller;

import com.example.testdrivesservice.model.TestDrive;
import com.example.testdrivesservice.service.TestDriveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TestDriveControllerTest {

    private TestDriveService service;
    private TestDriveController controller;

    @BeforeEach
    void setUp() {
        service = mock(TestDriveService.class);
        controller = new TestDriveController(service);
    }

    @Test
    void customerCreatesRecordForOwnUser() {
        TestDrive testDrive = validTestDrive();
        when(service.getUserId("customer")).thenReturn(7);
        when(service.create(testDrive)).thenReturn(testDrive);

        controller.create(testDrive, authentication("customer", "ROLE_CUSTOMER"));

        assertEquals(7, testDrive.getUserId());
        verify(service).create(testDrive);
    }

    @Test
    void customerCannotReadAnotherUsersRecord() {
        when(service.getUserId("customer")).thenReturn(7);
        when(service.findByIdForUser(10, 7)).thenThrow(
                new ResponseStatusException(HttpStatus.FORBIDDEN));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.getById(
                        10, authentication("customer", "ROLE_CUSTOMER")));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void customerCannotUpdateRecord() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.update(
                        10,
                        validTestDrive(),
                        authentication("customer", "ROLE_CUSTOMER")));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(service);
    }

    @Test
    void customerCannotDeleteRecord() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.delete(
                        10, authentication("customer", "ROLE_CUSTOMER")));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(service);
    }

    @Test
    void managerCanUpdateRecord() {
        TestDrive testDrive = validTestDrive();
        when(service.update(10, testDrive)).thenReturn(testDrive);

        controller.update(
                10, testDrive, authentication("manager", "ROLE_MANAGER"));

        verify(service).update(10, testDrive);
    }

    @Test
    void adminCanDeleteRecord() {
        controller.delete(10, authentication("admin", "ROLE_ADMIN"));

        verify(service).delete(10);
    }

    private Authentication authentication(String username, String role) {
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority(role)));
    }

    private TestDrive validTestDrive() {
        TestDrive testDrive = new TestDrive();
        testDrive.setUserId(99);
        testDrive.setCarModelId(2);
        testDrive.setDealerId(3);
        testDrive.setTestDriveDate(LocalDate.of(2026, 12, 1));
        testDrive.setStatus("PENDING");
        testDrive.setComment("Тестовая запись");
        return testDrive;
    }
}
