package com.mengxinya.mould;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Spec2Tests {

    private SourceDetail superMould(String source) {
        source = source.replaceAll(",", "，")
                .replaceAll(";", "；")
                .replaceAll("型号", " 型号")
                .replaceAll("规格", " 规格");

        Mould main = SpecTests.main;

        Mould gap = Mould.not(Mould.maybe(Mould.Digit, Mould.Letter, Mould.Han));
        Mould prefix = Mould.compose(
                Mould.maybe(Mould.theMould("型号"), Mould.theMould("规格")),
                Mould.repeat(gap, 0)
        );
        Mould mould = Mould.compose(
                prefix,
                main,
                Mould.repeat(gap),
                prefix,
                main
        );

        SourceDetail detail = mould.fill(source);
        if (detail.isFinish() && Clay.getValues(detail.getClay(), Object.class).size() > 1) {
            return detail;
        }
        return SourceDetail.notMatch(source);
    }


    @Test
    public void testInput1() {
        String sourceStr = "型号：通用，跌打损伤，神经痛账，风寒湿痛，颈肩腰腿痛，咳喘，前列腺，乳房痛，规格4cm×6cm 、6cm×8cm 、7cm×9cm 、7cm×10cm、8cm×10cm、9cm×11.5cm、10cm×12cm、10cm×13cm、12cm×15cm、9cm×30cm、13cm×16cm、15cm×21cm、14cm×14cm、∮2.0cm、∮4.0cm、∮6.0cm、∮8.0cm、∮10.0cm、∮12.0cm";
        SourceDetail detail = superMould(sourceStr);
        Assertions.assertTrue(detail.isFinish());
        Assertions.assertTrue(Clay.deconstruct(detail.getClay()).size() > 5);
    }

}


