package pro.sky.animalshelter4.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.InteractionUnit;
import pro.sky.animalshelter4.model.UpdateDPO;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TelegramBotSenderServiceTest {
    private final TelegramBotSenderService telegramBotSenderService = new TelegramBotSenderService(null, null);

    @ParameterizedTest
    @MethodSource("paramForGetTableSize")
    void getTableSize(int count, Pair<Integer, Integer> actual) {
        Pair<Integer, Integer> expected = telegramBotSenderService.getTableSize(count);
        org.assertj.core.api.Assertions.assertThat(actual).isEqualTo(expected);
    }

    public static Stream<Arguments> paramForGetTableSize() {
        return Stream.of(
                //standard positive
                Arguments.of(1, Pair.of(1, 1)),
                Arguments.of(2, Pair.of(2, 1)),
                Arguments.of(3, Pair.of(3, 1)),
                Arguments.of(4, Pair.of(4, 1)),
                Arguments.of(5, Pair.of(5, 1)),
                Arguments.of(6, Pair.of(3, 2)),
                Arguments.of(7, Pair.of(7, 1)),
                Arguments.of(8, Pair.of(4, 2)),
                Arguments.of(9, Pair.of(3, 3)),
                Arguments.of(10, Pair.of(5, 2)),
                Arguments.of(11, Pair.of(11, 1)),
                Arguments.of(12, Pair.of(4, 3)),
                Arguments.of(13, Pair.of(13, 1)),
                Arguments.of(14, Pair.of(7, 2)),
                Arguments.of(15, Pair.of(5, 3)),
                Arguments.of(16, Pair.of(4, 4)),
                Arguments.of(17, Pair.of(17, 1)),
                Arguments.of(18, Pair.of(3, 6)),
                Arguments.of(19, Pair.of(19, 1)),
                Arguments.of(20, Pair.of(5, 4))
        );
    }
}