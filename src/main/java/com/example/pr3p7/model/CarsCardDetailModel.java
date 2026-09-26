package com.example.pr3p7.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "cars_card_details")
public class CarsCardDetailModel {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Нужно выбрать корзину")
    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "details"})
    private CarsCardModel card;

    @NotNull(message = "Нужно выбрать комплектацию")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "complecttion_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cardDetails"})
    private CarComplecttionModel complecttion;

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть больше 0")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public CarsCardModel getCard() { return card; }
    public void setCard(CarsCardModel card) { this.card = card; }

    public CarComplecttionModel getComplecttion() { return complecttion; }
    public void setComplecttion(CarComplecttionModel complecttion) { this.complecttion = complecttion; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        String cardText = card == null ? "не выбрана" : "корзина #" + card.getId();
        String complecttionName = complecttion == null ? "не выбрана" : complecttion.getConfigurationName();
        return "ID: " + id + ", " + cardText
                + ", комплектация: " + complecttionName + ", количество: " + quantity;
    }
}