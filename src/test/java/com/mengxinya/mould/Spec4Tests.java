package com.mengxinya.mould;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Spec4Tests {

    private SourceDetail superMould(String source) {
        source = source.trim();
        if (source.contains("规格：") || source.contains("型号：")) {
            return SourceDetail.notMatch(source);
        }
        Mould.MouldContext context = Mould.makeContext();
        String joinKey = "key";
        Mould sep = Mould.maybe(
                Mould.theMould("、"),
                Mould.maybe(Mould.theMould("，"), Mould.theMould(",")),
                Mould.maybe(Mould.theMould("；"), Mould.theMould(";"))
        );
        Mould sepSub = Mould.maybe(
                Mould.theMould("×"), Mould.theMould("*"),
                Mould.theMould("/"), Mould.theMould("-")
        );
        Mould itemSub = Mould.convert(
                Mould.repeat(Mould.not(Mould.maybe(
                        Mould.theMould("("), Mould.theMould("（"),
                        Mould.theMould(":"), Mould.theMould("："),
                        Mould.theMould("规格"), Mould.theMould("型号"),
                        Mould.theMould("注"), Mould.theMould("。"),
                        sep, sepSub
                ))),
                ClayConverter.joining("")
        );

        Mould item = Mould.convert(Mould.cons(itemSub, Mould.repeat(Mould.composeJoining(sepSub, itemSub))), ClayConverter.joining(""));
        Mould mould = Mould.join(context, joinKey, sep, item);

        SourceDetail detail = mould.fill(source);

        // 排除只有一项的情况
        if (detail.isFinish() && Clay.getValues(detail.getClay(), Object.class).size() == 1) {
            return SourceDetail.notMatch(source);
        }

        // 如果尾部还存在字符串的话，不能出现规律模式。
        Mould gap = Mould.repeat(Mould.not(item));
        Mould lawMould = Mould.compose(gap, item);
        if (detail.isFinish() && detail.getLeftSource().length() > 0) {
            SourceDetail lawDetail = lawMould.fill(detail.getLeftSource());
            if (lawDetail.isFinish()) {
                return SourceDetail.notMatch(source);
            }
        }

        return detail;
    }

    @Test
    public void testInput1() {
        String sourceStr = "产品型号：A型；B型包装规格：500g袋装；500g瓶装；4.5kg桶装";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        System.out.println(detail.getClay().toJsonString());
    }


}


