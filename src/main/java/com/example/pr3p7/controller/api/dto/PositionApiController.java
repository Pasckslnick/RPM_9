package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.PositionModel;
import com.example.pr3p7.service.PositionService;
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
@RequestMapping("/api/positions")
@Tag(
        name = "Должности",
        description = "Управление должностями"
)
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class PositionApiController {

    private final PositionService service;

    public PositionApiController(PositionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Получить все должности", description = "Получить список всех должностей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Должности не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при получении должностей"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ к должностям"),
            @ApiResponse(responseCode = "403", description = "Запрещенный доступ к должностям"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос для получения должностей"),
            @ApiResponse(responseCode = "204", description = "Нет должностей для отображения")
    })
    public List<PositionModel> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить должность", description = "Получить информацию о должности по её идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Должность не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при получении должности"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ к должности"),
            @ApiResponse(responseCode = "403", description = "Запрещенный доступ к должности"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос для получения должности"),
            @ApiResponse(responseCode = "204", description = "Должность не найдена для отображения")
    })
    public PositionModel getById(
            @PathVariable Integer id) {

        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать должность", description = "Создать новую должность с указанными данными")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для создания должности"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при создании должности"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ к созданию должности"),
            @ApiResponse(responseCode = "403", description = "Запрещенный доступ к созданию должности"),
            @ApiResponse(responseCode = "409", description = "Конфликт при создании должности (например, должность с таким именем уже существует)")
    })
    public PositionModel create(
            @Valid @RequestBody PositionModel item) {

        return service.save(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Обновить должность", description = "Обновить информацию о должности по её идентификатору с новыми данными")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления должности"),
            @ApiResponse(responseCode = "404", description = "Должность не найдена для обновления"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при обновлении должности"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ к обновлению должности"),
            @ApiResponse(responseCode = "403", description = "Запрещенный доступ к обновлению должности"),
            @ApiResponse(responseCode = "409", description = "Конфликт при обновлении должности (например, должность с таким именем уже существует)")
    })
    public PositionModel update(
            @PathVariable Integer id,
            @Valid @RequestBody PositionModel item) {

        item.setId(id);

        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Удалить должность", description = "Удалить должность по её идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Удалено"),
            @ApiResponse(responseCode = "404", description = "Должность не найдена для удаления"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при удалении должности"),
            @ApiResponse(responseCode = "401", description = "Неавторизованный доступ к удалению должности"),
            @ApiResponse(responseCode = "403", description = "Запрещенный доступ к удалению должности"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос для удаления должности"),
            @ApiResponse(responseCode = "409", description = "Конфликт при удалении должности (например, должность используется в других записях и не может быть удалена)")
    })
    public void delete(
            @PathVariable Integer id) {

        service.delete(id);
    }
}