package com.mengxinya.mould;

import java.util.List;
import java.util.stream.Collectors;

public interface ClayConverter {
    Clay apply(Clay clay);

    /**
     * 将Clay集合拼接成字符串
     */
    static ClayConverter joining(String sep) {
        return clay -> {
            List<Clay> clayList = Clay.deconstruct(clay);
            return Clay.make(
                    clayList.stream()
                            .map(item -> item.value(Object.class))
                            .map(Object::toString)
                            .collect(Collectors.joining(sep))
            );
        };
    }
}
