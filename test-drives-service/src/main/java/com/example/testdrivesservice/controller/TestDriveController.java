package com.example.testdrivesservice.controller;

import com.example.testdrivesservice.model.TestDrive;
import com.example.testdrivesservice.service.TestDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-drives")
@Tag(name = "Тест-драйвы", description = "Управление записями на тест-драйв")
@SecurityRequirement(name = "bearerAuth")
public class TestDriveController {

    private final TestDriveService service;

    public TestDriveController(TestDriveService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Получить тест-драйвы")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public List<TestDrive> getAll(Authentication authentication) {
        if (hasManagerRole(authentication)) {
            return service.findAll();
        }
        return service.findByUserId(service.getUserId(authentication.getName()));
    }

    @GetMapping("/my")
    @Operation(summary = "Получить свои тест-драйвы")
    public List<TestDrive> getMy(Authentication authentication) {
        Integer userId = service.getUserId(authentication.getName());
        return service.findByUserId(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить тест-драйв по идентификатору")
    public TestDrive getById(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        if (hasManagerRole(authentication)) {
            return service.findById(id);
        }
        return service.findByIdForUser(
                id, service.getUserId(authentication.getName()));
    }

    @GetMapping("/search")
    @Operation(summary = "Найти тест-драйвы по комментарию")
    public List<TestDrive> search(
            @RequestParam String query,
            Authentication authentication
    ) {
        if (hasManagerRole(authentication)) {
            return service.search(query);
        }
        return service.findByUserId(service.getUserId(authentication.getName()))
                .stream()
                .filter(item -> item.getComment().toLowerCase()
                        .contains(query.toLowerCase()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать запись на тест-драйв")
    public TestDrive create(
            @Valid @RequestBody TestDrive testDrive,
            Authentication authentication
    ) {
        if (!hasManagerRole(authentication)) {
            testDrive.setUserId(service.getUserId(authentication.getName()));
        }
        return service.create(testDrive);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить запись на тест-драйв")
    public TestDrive update(
            @PathVariable Integer id,
            @Valid @RequestBody TestDrive testDrive,
            Authentication authentication
    ) {
        if (!hasManagerRole(authentication)) {
            service.findByIdForUser(
                    id, service.getUserId(authentication.getName()));
        }
        return service.update(id, testDrive);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить запись на тест-драйв")
    public void delete(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        if (!hasManagerRole(authentication)) {
            service.findByIdForUser(
                    id, service.getUserId(authentication.getName()));
        }
        service.delete(id);
    }

    private boolean hasManagerRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN")
                        || role.equals("ROLE_MANAGER"));
    }
}
