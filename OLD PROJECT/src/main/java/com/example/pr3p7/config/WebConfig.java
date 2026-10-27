package com.example.pr3p7.config;

import com.example.pr3p7.model.CarComplecttionModel;
import com.example.pr3p7.model.CarModel;
import com.example.pr3p7.model.CarsCardModel;
import com.example.pr3p7.model.DealerShipModel;
import com.example.pr3p7.model.PositionModel;
import com.example.pr3p7.model.UserModel;
import com.example.pr3p7.repository.CarComplecttionRepository;
import com.example.pr3p7.repository.CarModelRepository;
import com.example.pr3p7.repository.CarsCardRepository;
import com.example.pr3p7.repository.DealerShipRepository;
import com.example.pr3p7.repository.PositionRepository;
import com.example.pr3p7.repository.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfig implements WebMvcConfigurer {

    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final CarModelRepository carModelRepository;
    private final CarsCardRepository carsCardRepository;
    private final CarComplecttionRepository carComplecttionRepository;
    private final DealerShipRepository dealerShipRepository;

    public WebConfig(PositionRepository positionRepository,
                     UserRepository userRepository,
                     CarModelRepository carModelRepository,
                     CarsCardRepository carsCardRepository,
                     CarComplecttionRepository carComplecttionRepository,
                     DealerShipRepository dealerShipRepository) {
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
        this.carModelRepository = carModelRepository;
        this.carsCardRepository = carsCardRepository;
        this.carComplecttionRepository = carComplecttionRepository;
        this.dealerShipRepository = dealerShipRepository;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToPositionConverter(positionRepository));
        registry.addConverter(new PositionToStringConverter());

        registry.addConverter(new StringToUserConverter(userRepository));
        registry.addConverter(new UserToStringConverter());

        registry.addConverter(new StringToCarModelConverter(carModelRepository));
        registry.addConverter(new CarModelToStringConverter());

        registry.addConverter(new StringToCarsCardConverter(carsCardRepository));
        registry.addConverter(new CarsCardToStringConverter());

        registry.addConverter(new StringToComplecttionConverter(carComplecttionRepository));
        registry.addConverter(new ComplecttionToStringConverter());

        registry.addConverter(new StringToDealerConverter(dealerShipRepository));
        registry.addConverter(new DealerToStringConverter());
    }

    private static Integer toInteger(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return Integer.valueOf(source);
    }

    private static class StringToPositionConverter implements Converter<String, PositionModel> {
        private final PositionRepository repository;

        private StringToPositionConverter(PositionRepository repository) {
            this.repository = repository;
        }

        @Override
        public PositionModel convert(String source) {
            Integer id = toInteger(source);
            return id == null ? null : repository.findById(id).orElse(null);
        }
    }

    private static class PositionToStringConverter implements Converter<PositionModel, String> {
        @Override
        public String convert(PositionModel source) {
            return source == null || source.getId() == null ? "" : source.getId().toString();
        }
    }

    private static class StringToUserConverter implements Converter<String, UserModel> {
        private final UserRepository repository;

        private StringToUserConverter(UserRepository repository) {
            this.repository = repository;
        }

        @Override
        public UserModel convert(String source) {
            Integer id = toInteger(source);
            return id == null ? null : repository.findById(id).orElse(null);
        }
    }

    private static class UserToStringConverter implements Converter<UserModel, String> {
        @Override
        public String convert(UserModel source) {
            return source == null || source.getId() == null ? "" : source.getId().toString();
        }
    }

    private static class StringToCarModelConverter implements Converter<String, CarModel> {
        private final CarModelRepository repository;

        private StringToCarModelConverter(CarModelRepository repository) {
            this.repository = repository;
        }

        @Override
        public CarModel convert(String source) {
            Integer id = toInteger(source);
            return id == null ? null : repository.findById(id).orElse(null);
        }
    }

    private static class CarModelToStringConverter implements Converter<CarModel, String> {
        @Override
        public String convert(CarModel source) {
            return source == null || source.getId() == null ? "" : source.getId().toString();
        }
    }

    private static class StringToCarsCardConverter implements Converter<String, CarsCardModel> {
        private final CarsCardRepository repository;

        private StringToCarsCardConverter(CarsCardRepository repository) {
            this.repository = repository;
        }

        @Override
        public CarsCardModel convert(String source) {
            Integer id = toInteger(source);
            return id == null ? null : repository.findById(id).orElse(null);
        }
    }

    private static class CarsCardToStringConverter implements Converter<CarsCardModel, String> {
        @Override
        public String convert(CarsCardModel source) {
            return source == null || source.getId() == null ? "" : source.getId().toString();
        }
    }

    private static class StringToComplecttionConverter implements Converter<String, CarComplecttionModel> {
        private final CarComplecttionRepository repository;

        private StringToComplecttionConverter(CarComplecttionRepository repository) {
            this.repository = repository;
        }

        @Override
        public CarComplecttionModel convert(String source) {
            Integer id = toInteger(source);
            return id == null ? null : repository.findById(id).orElse(null);
        }
    }

    private static class ComplecttionToStringConverter implements Converter<CarComplecttionModel, String> {
        @Override
        public String convert(CarComplecttionModel source) {
            return source == null || source.getId() == null ? "" : source.getId().toString();
        }
    }

    private static class StringToDealerConverter implements Converter<String, DealerShipModel> {
        private final DealerShipRepository repository;

        private StringToDealerConverter(DealerShipRepository repository) {
            this.repository = repository;
        }

        @Override
        public DealerShipModel convert(String source) {
            Integer id = toInteger(source);
            return id == null ? null : repository.findById(id).orElse(null);
        }
    }

    private static class DealerToStringConverter implements Converter<DealerShipModel, String> {
        @Override
        public String convert(DealerShipModel source) {
            return source == null || source.getId() == null ? "" : source.getId().toString();
        }
    }
}
