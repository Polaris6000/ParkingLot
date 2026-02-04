package com.exmple.parkinglot.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

public enum MapperUtil {
    INSTANCE;

    private final ModelMapper modelMapper;
    MapperUtil(){
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        //Strict : 엄격 모드. 필드명과 타입이 모두 일치해야 매핑.
    }
    public ModelMapper getInstance(){
        return modelMapper;
    }
}
