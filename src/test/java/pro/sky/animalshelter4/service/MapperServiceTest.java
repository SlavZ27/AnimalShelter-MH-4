package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.animalshelter4.UpdateGenerator;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.InteractionUnit;
import pro.sky.animalshelter4.model.UpdateDPO;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MapperServiceTest {
    private static final UpdateGenerator updateGenerator = new UpdateGenerator();
    private final MapperService mapperService = new MapperService();

    @ParameterizedTest
    @MethodSource("paramForToDPO")
    void toDPO(Update updateTdo, UpdateDPO updateDpo) {
        UpdateDPO actual = updateDpo;
        UpdateDPO expected = mapperService.toDPO(updateTdo);
        assertThat(actual.getMessage()).isEqualTo(expected.getMessage());
        assertThat(actual.getUserName()).isEqualTo(expected.getUserName());
        assertThat(actual.getIdMedia()).isEqualTo(expected.getIdMedia());
        assertThat(actual.getIdChat()).isEqualTo(expected.getIdChat());
        assertThat(actual.getCommand()).isEqualTo(expected.getCommand());
        assertThat(actual.getInteractionUnit()).isEqualTo(expected.getInteractionUnit());
        Assertions.assertTrue(actual.equals(expected));
    }

    public static Stream<Arguments> paramForToDPO() {
        return Stream.of(
                Arguments.of(
                        updateGenerator.generateUpdateMessageWithReflection(
                                "123",
                                "456",
                                "789",
                                50L,
                                Command.START.getTitle()),
                        new UpdateDPO(
                                50L,
                                "456",
                                Command.START,
                                "",
                                null,
                                InteractionUnit.COMMAND
                        )
                ),
                Arguments.of(
                        updateGenerator.generateUpdateMessageWithReflection(
                                "123",
                                "456",
                                "789",
                                50L,
                                Command.START.getTitle() + " fsfdsfs"),
                        new UpdateDPO(
                                50L,
                                "456",
                                Command.START,
                                "fsfdsfs",
                                null,
                                InteractionUnit.COMMAND
                        )
                )
        );
    }


}