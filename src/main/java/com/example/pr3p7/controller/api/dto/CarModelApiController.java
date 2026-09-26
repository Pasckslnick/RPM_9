package com.example.pr3p7.controller.api.dto;

import com.example.pr3p7.model.CarModel;
import com.example.pr3p7.model.CarsCardDetailModel;
import com.example.pr3p7.service.CarModelService;
import com.example.pr3p7.service.CarsCardDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-models")
@Tag(name = "Модели автомобилей", description = "CRUD операции с моделями автомобилей")
@SecurityRequirement(name = "bearerAuth")
public class CarModelApiController {

    private final CarModelService service;
    private final CarsCardDetailService service1;

    public CarModelApiController(CarModelService service, CarsCardDetailService service1) {
        this.service = service;
        this.service1 = service1;
    }


    @GetMapping("/by-card/{cardId}")
    public List<CarsCardDetailModel> getByCard(@PathVariable Integer cardId) {
        return service1.getByCardId(cardId);
    }

    @GetMapping
    @Operation(summary = "Получить все модели", description = "Возвращает список всех моделей автомобилей")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Модели не найдены"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")

    })
    public List<CarModel> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить модель по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Найдено"),
            @ApiResponse(responseCode = "404", description = "Модель не найдена"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public CarModel getById(@Parameter(description = "Идентификатор модели") @PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать модель", description = "Создаёт новую модель автомобиля и возвращает её")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Создано"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public CarModel create(@Valid @RequestBody CarModel item) {
        return service.save(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить модель", description = "Обновляет существующую модель автомобиля по её идентификатору и возвращает обновлённую модель")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обновлено"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Модель не найдена"),
            @ApiResponse(responseCode = "401", description = "Не авторизирован"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public CarModel update(
            @PathVariable Integer id,
            @Valid @RequestBody CarModel item) {

        item.setId(id);

        return service.save(item);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить модель", description = "Удаляет модель автомобиля по её идентификатору и возвращает сообщение об удалении")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Удалено"),
            @ApiResponse(responseCode = "404", description = "Модель не найдена"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "503", description = "Service Unavailable"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public String delete(@PathVariable Integer id) {

        service.delete(id);

        return "Модель удалена";
    }
}