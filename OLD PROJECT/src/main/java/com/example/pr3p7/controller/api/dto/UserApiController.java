package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.UserModel;
import com.example.pr3p7.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "Пользователи",
        description = "API для управления пользователями"
)
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список пользователей"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователи не найден"
            ),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "422", description = "Невалидные данные запроса")
    })
    public List<UserModel> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword) {

        return keyword != null && !keyword.isBlank()
                ? userService.search(keyword)
                : userService.getPage(page, 10);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить пользователя",
            description = "Получение пользователя по идентификатору"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            ),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "422", description = "Невалидные данные запроса")
    })
    public UserModel getById(
            @PathVariable int id) {

        return userService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Создать пользователя",
            description = "Создание нового пользователя"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Пользователь создан"), @ApiResponse(responseCode = "404", description = "Ошибка создания")})
    public UserModel create(
            @Valid @RequestBody UserModel user) {

        return userService.save(user);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить пользователя",
            description = "Изменение существующего пользователя"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка обновления"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "422", description = "Невалидные данные запроса")
    })
    public UserModel update(
            @PathVariable Integer id,
            @Valid @RequestBody UserModel user) {

        user.setId(id);

        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаление пользователя по идентификатору"
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Ошибка удаления"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "422", description = "Невалидные данные запроса")
    })
    public String delete(
            @PathVariable Integer id) {

        userService.delete(id);

        return "Пользователь удален";
    }
}