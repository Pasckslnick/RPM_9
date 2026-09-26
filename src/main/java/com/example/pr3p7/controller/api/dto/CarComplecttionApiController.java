package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.CarComplecttionModel;
import com.example.pr3p7.service.CarComplecttionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-complecttions")
@Tag(name = "Комплектации", description = "API для управления комплектациями автомобилей")
@SecurityRequirement(name = "bearerAuth")
public class CarComplecttionApiController {

    private final CarComplecttionService service;

    public CarComplecttionApiController(CarComplecttionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Получить все комплектации" , description = "Возвращает список всех комплектаций автомобилей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Комплектации не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при получении комплектаций"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён для получения комплектаций"),
            @ApiResponse(responseCode = "401", description = "Неавторизован для получения комплектаций"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос для получения комплектаций"),
            @ApiResponse(responseCode = "204", description = "Нет контента - комплектации не найдены")
    })
    public List<CarComplecttionModel> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить комплектацию по ID" , description = "Возвращает комплектацию автомобиля по её уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Комплектация по ID не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при получении комплектации по ID"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён для получения комплектации по ID"),
            @ApiResponse(responseCode = "401", description = "Неавторизован для получения комплектации по ID"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос для получения комплектации по ID")
    })
    public CarComplecttionModel getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать новую комплектацию" , description = "Создаёт новую комплектацию автомобиля на основе предоставленных данных")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для создания комплектации"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при создании комплектации"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён для создания комплектации"),
            @ApiResponse(responseCode = "401", description = "Неавторизован для создания комплектации")
    })
    public CarComplecttionModel create(@Valid @RequestBody CarComplecttionModel item) {
        return service.save(item);
    }

    @GetMapping("/by-model/{modelId}")
    @Operation(
            summary = "Комплектации модели",
            description = "Возвращает все комплектации для указанной модели"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Модель не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера"),
            @ApiResponse(responseCode = "401", description = "Неавторизован")
    })
    public List<CarComplecttionModel> getByModel(@PathVariable Integer modelId) {
        return service.getByCarModel(modelId);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Обновить существующую комплектацию" , description = "Обновляет существующую комплектацию автомобиля на основе её ID и предоставленных данных")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные для обновления комплектации"),
            @ApiResponse(responseCode = "404", description = "Комплектация по ID не найдена для обновления"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при обновлении комплектации"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён для обновления комплектации"),
            @ApiResponse(responseCode = "401", description = "Неавторизован для обновления комплектации")
    })
    public CarComplecttionModel update(
            @PathVariable Integer id,
            @Valid @RequestBody CarComplecttionModel item) {

        item.setId(id);
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить комплектацию" , description = "Удаляет комплектацию автомобиля по её уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Удалено"),
            @ApiResponse(responseCode = "404", description = "Комплектация по ID не найдена для удаления"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера при удалении комплектации"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён для удаления комплектации"),
            @ApiResponse(responseCode = "401", description = "Неавторизован для удаления комплектации"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос для удаления комплектации")
    })
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Комплектация удалена";
    }
}