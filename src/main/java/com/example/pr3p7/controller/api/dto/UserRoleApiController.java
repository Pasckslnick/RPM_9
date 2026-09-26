package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.UserRoleModel;
import com.example.pr3p7.service.UserRoleService;
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
    @RequestMapping("/api/roles")
    @Tag(
            name = "Роли",
            description = "Управление ролями пользователей"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public class UserRoleApiController {

        private final UserRoleService service;

        public UserRoleApiController(UserRoleService service) {
            this.service = service;
        }

        @GetMapping
        @Operation(summary = "Получить все роли", description = "Возвращает список всех ролей пользователей")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Найдено"),
                @ApiResponse(responseCode = "404", description = "Роли не найдена"),
                @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при получении ролей"),
                @ApiResponse(responseCode = "503", description = "Сервис временно недоступен для получения ролей"),
                @ApiResponse(responseCode = "504", description = "Время ожидания истекло при получении ролей")
        })
        public List<UserRoleModel> getAll() {
            return service.getAll();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Получить роль по ID", description = "Возвращает роль пользователя по её идентификатору")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Найдено"),
                @ApiResponse(responseCode = "404", description = "Роль не найдена"),
                @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при получении роли"),
                @ApiResponse(responseCode = "503", description = "Сервис временно недоступен для получения роли"),
                @ApiResponse(responseCode = "504", description = "Время ожидания истекло при получении роли")
        })
        public UserRoleModel getById(@PathVariable Integer id) {
            return service.getById(id);
        }

        @PostMapping
        @Operation(summary = "Создать новую роль", description = "Создаёт новую роль пользователя")
        @PreAuthorize("hasAnyRole('ADMIN')")
        @ApiResponses({
                @ApiResponse(responseCode = "201", description = "Роль создана"),
                @ApiResponse(responseCode = "400", description = "Некорректные данные для создания роли"),
                @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания роли"),
                @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при создании роли"),
                @ApiResponse(responseCode = "409", description = "Роль с таким именем уже существует"),
                @ApiResponse(responseCode = "422", description = "Невозможно создать роль из-за бизнес-правил"),
                @ApiResponse(responseCode = "503", description = "Сервис временно недоступен для создания роли"),
                @ApiResponse(responseCode = "504", description = "Время ожидания истекло при создании роли")

        })
        public UserRoleModel create(@Valid @RequestBody UserRoleModel item) {
            return service.save(item);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Обновить роль", description = "Обновляет существующую роль пользователя по её идентификатору")
        @PreAuthorize("hasAnyRole('ADMIN')")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Роль обновлена"),
                @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления роли"),
                @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                @ApiResponse(responseCode = "403", description = "Недостаточно прав для обновления роли"),
                @ApiResponse(responseCode = "404", description = "Роль не найдена"),
                @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при обновлении роли"),
                @ApiResponse(responseCode = "409", description = "Роль с таким именем уже существует"),
                @ApiResponse(responseCode = "422", description = "Невозможно обновить роль из-за бизнес-правил"),
                @ApiResponse(responseCode = "503", description = "Сервис временно недоступен для обновления роли"),
                @ApiResponse(responseCode = "504", description = "Время ожидания истекло при обновлении роли")
        })
        public UserRoleModel update(
                @PathVariable Integer id,
                @Valid @RequestBody UserRoleModel item) {

            item.setId(id);
            return service.save(item);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN')")
        @Operation(summary = "Удалить роль", description = "Удаляет существующую роль пользователя по её идентификатору")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Роль удалена"),
                @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
                @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления роли"),
                @ApiResponse(responseCode = "404", description = "Роль не найдена"),
                @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при удалении роли"),
                @ApiResponse(responseCode = "503", description = "Сервис временно недоступен для удаления роли"),
                @ApiResponse(responseCode = "504", description = "Время ожидания истекло при удалении роли")
        })
        public String delete(@PathVariable Integer id) {
            service.delete(id);
            return "Роль удалена";
        }
    }