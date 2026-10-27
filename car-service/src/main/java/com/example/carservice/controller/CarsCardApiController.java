package com.example.carservice.controller;

import com.example.carservice.model.CarsCardModel;
import com.example.carservice.service.CarsCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Корзины", description = "CRUD операции для корзин")
@SecurityRequirement(name = "bearerAuth")
public class CarsCardApiController {

    private final CarsCardService service;

    public CarsCardApiController(CarsCardService service) {
        this.service = service;
    }

    private boolean canManageAll(Authentication authentication) {
        return authentication != null
                && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role ->
                        role.equals("ROLE_ADMIN")
                                || role.equals("ROLE_MANAGER"));
    }

    private void requireManager(Authentication authentication) {
        if (!canManageAll(authentication)) {
            throw new AccessDeniedException(
                    "До подключения user-service эта операция доступна только ADMIN/MANAGER"
            );
        }
    }

    @GetMapping
    @Operation(
            summary = "Получить корзины",
            description = "До подключения user-service доступно только ADMIN/MANAGER"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав")
    })
    public List<CarsCardModel> getAll(Authentication authentication) {
        requireManager(authentication);
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить корзину по ID")
    public CarsCardModel getById(
            @PathVariable Integer id,
            Authentication authentication) {
        requireManager(authentication);
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать корзину")
    public CarsCardModel create(
            @Valid @RequestBody CarsCardModel item,
            Authentication authentication) {
        requireManager(authentication);
        return service.save(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить корзину")
    public CarsCardModel update(
            @PathVariable Integer id,
            @Valid @RequestBody CarsCardModel item,
            Authentication authentication) {
        requireManager(authentication);

        CarsCardModel existing = service.getById(id);
        item.setId(existing.getId());

        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить корзину")
    public void delete(
            @PathVariable Integer id,
            Authentication authentication) {
        requireManager(authentication);
        service.delete(id);
    }
}