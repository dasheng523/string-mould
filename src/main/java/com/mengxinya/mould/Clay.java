package com.mengxinya.mould;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.stream.Collectors;

public interface Clay {

    <T> T value(Class<T> tClass);

    default <T> List<T> values(Class<T> tClass) {
        throw new ClayException("该Clay不支持values方法");
    }

    default String toJsonString() {
        return JSON.toJSONString(value(Object.class));
    }

    static Clay make(Object obj) {
        return new Clay() {
            @Override
            public <T> T value(Class<T> tClass) {
                return tClass.cast(obj);
            }
        };
    }

    static Clay compose(List<Clay> collect) {
        return new Clay() {
            @Override
            public <T> T value(Class<T> tClass) {
                throw new ClayException("该Clay不支持value方法");
            }

            @Override
            public <T> List<T> values(Class<T> tClass) {
                return collect.stream().map(item -> item.value(tClass)).collect(Collectors.toList());
            }

            @Override
            public String toJsonString() {
                return JSON.toJSONString(values(Object.class));
            }
        };
    }

    static List<Clay> deconstruct(Clay clay, ClayConverter converter) {
        return clay.values(Object.class).stream().map(Clay::make).map(converter::apply).collect(Collectors.toList());
    }

    static Clay deconstruct(Clay clay, int index) {
        return Clay.make(clay.values(Object.class).get(index));
    }

}
