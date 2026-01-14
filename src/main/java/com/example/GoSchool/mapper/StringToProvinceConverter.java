package com.example.GoSchool.mapper;

import com.example.GoSchool.constant.Province;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProvinceConverter implements Converter<String, Province> {
    @Override
    public Province convert(String source) {
        return Province.valueOf(source.toUpperCase().replace(" ", "_"));
    }
}
