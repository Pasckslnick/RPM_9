package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.TestDriveModel;
import com.example.pr3p7.model.UserModel;
import com.example.pr3p7.repository.UserRepository;
import com.example.pr3p7.service.TestDriveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-drives")
@Tag(name = "Тест-драйвы", description = "Управление тест-драйвами")
@SecurityRequirement(name = "bearerAuth")
public class TestDriveApiController {

    private final TestDriveService service;
    private final UserRepository userRepository;

    public TestDriveApiController(TestDriveService service,
                                  UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Получить тест-драйвы",
            description = "ADMIN/MANAGER — все, обычный пользователь — только свои")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public List<TestDriveModel> getAll(Authentication authentication) {

        boolean canSeeAll = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ROLE_MANAGER"));

        if (canSeeAll) {
            return service.getAll();
        }

        UserModel me = userRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + authentication.getName()));

        return service.getByUserId(me.getId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить тест-драйв по id")
    public TestDriveModel getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать запись на тест-драйв")
    public TestDriveModel create(@Valid @RequestBody TestDriveModel item) {
        return service.save(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Обновить запись на тест-драйв")
    public TestDriveModel update(@PathVariable Integer id,
                                 @Valid @RequestBody TestDriveModel item) {
        item.setId(id);
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Удалить запись на тест-драйв")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}