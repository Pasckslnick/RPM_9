package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.CarsCardModel;
import com.example.pr3p7.model.UserModel;
import com.example.pr3p7.repository.UserRepository;
import com.example.pr3p7.service.CarsCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Корзины", description = "CRUD операции для корзин")
@SecurityRequirement(name = "bearerAuth")
public class CarsCardApiController {

    private final CarsCardService service;
    private final UserRepository userRepository;

    public CarsCardApiController(CarsCardService service,
                                 UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "Получить корзины",
            description = "ADMIN/MANAGER — все, обычный пользователь — только свои")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "401", description = "Неавторизован"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public List<CarsCardModel> getAll(Authentication authentication) {

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
    @Operation(summary = "Получить корзину")
    public CarsCardModel getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать корзину")
    public CarsCardModel create(@Valid @RequestBody CarsCardModel item) {
        return service.save(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить корзину")
    public CarsCardModel update(@PathVariable Integer id,
                                @Valid @RequestBody CarsCardModel item) {
        item.setId(id);
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить корзину")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}