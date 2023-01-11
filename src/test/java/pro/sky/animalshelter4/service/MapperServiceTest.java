package pro.sky.animalshelter4.service;

import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pro.sky.animalshelter4.Generator;
import pro.sky.animalshelter4.model.Command;
import pro.sky.animalshelter4.model.InteractionUnit;
import pro.sky.animalshelter4.model.UpdateDPO;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

class MapperServiceTest {
    private static final Generator generator = new Generator();
    private final MapperService mapperService = new MapperService();

    @ParameterizedTest
    @MethodSource("paramForToDPO")
    void toDPO(Update updateTdo, UpdateDPO updateDpo) {
        UpdateDPO actual = updateDpo;
        UpdateDPO expected = mapperService.toDPO(updateTdo);

        if (actual != null) {
            assertThat(actual.getMessage()).isEqualTo(expected.getMessage());
            assertThat(actual.getUserName()).isEqualTo(expected.getUserName());
            assertThat(actual.getIdMedia()).isEqualTo(expected.getIdMedia());
            assertThat(actual.getIdChat()).isEqualTo(expected.getIdChat());
            assertThat(actual.getCommand()).isEqualTo(expected.getCommand());
            assertThat(actual.getInteractionUnit()).isEqualTo(expected.getInteractionUnit());
        }
        Assertions.assertEquals(actual, expected);
    }

    public static Stream<Arguments> paramForToDPO() {
        return Stream.of(
                //standard positive
                Arguments.of(
                        generator.generateUpdateMessageWithReflection(
                                "123",
                                "456",
                                "789",
                                50L,
                                Command.START.getTextCommand(),
                                false),
                        new UpdateDPO(
                                50L,
                                "456",
                                "123",
                                Command.START,
                                "",
                                null,
                                InteractionUnit.COMMAND
                        )
                ),
                //message text = Command + " " + text
                Arguments.of(
                        generator.generateUpdateMessageWithReflection(
                                "123",
                                "456",
                                "789",
                                50L,
                                Command.START.getTextCommand() + " fsfdsfs",
                                false),
                        new UpdateDPO(
                                50L,
                                "456",
                                "123",
                                Command.START,
                                "fsfdsfs",
                                null,
                                InteractionUnit.COMMAND
                        )
                ),
                //firstName = "", lastName = null, message = Command + " fsfdsfs sdfsdf sdf s "
                Arguments.of(
                        generator.generateUpdateMessageWithReflection(
                                "123",
                                "",
                                null,
                                50L,
                                Command.START.getTextCommand() + " fsfdsfs sdfsdf sdf s ",
                                false),
                        new UpdateDPO(
                                50L,
                                "123",
                                "123",
                                Command.START,
                                "fsfdsfs sdfsdf sdf s",
                                null,
                                InteractionUnit.COMMAND
                        )
                ),
                //firstName = null, lastName = null, userName=null,
                Arguments.of(
                        generator.generateUpdateMessageWithReflection(
                                null,
                                null,
                                null,
                                50L,
                                Command.START.getTextCommand(),
                                false),
                        null
                ),
                //chatId = null
                Arguments.of(
                        generator.generateUpdateMessageWithReflection(
                                "123",
                                "456",
                                "789",
                                null,
                                Command.START.getTextCommand() + " " + Command.INFO.getTextCommand(),
                                false),
                        null
                ),
                //Command = /sagfasd
                Arguments.of(
                        generator.generateUpdateMessageWithReflection(
                                "123",
                                "456",
                                "789",
                                50L,
                                "/sagfasd",
                                false),
                        new UpdateDPO(
                                50L,
                                "456",
                                "123",
                                null,
                                "/sagfasd",
                                null,
                                InteractionUnit.COMMAND
                        )
                ),
                //message chatId < 0
                Arguments.of(
                        generator.generateUpdateMessageWithReflection(
                                "123",
                                "456",
                                "789",
                                -50L,
                                "/sagfasd",
                                false),
                        null
                ),
                //CallbackQuery chatId < 0
                Arguments.of(
                        generator.generateUpdateCallbackQueryWithReflection(
                                "123",
                                "456",
                                "789",
                                -50L,
                                "/sagfasd",
                                false),
                        null
                ),
                //message = simple text
                Arguments.of(
                        generator.generateUpdateMessageWithReflection(
                                "123",
                                "456",
                                "789",
                                50L,
                                "fgdhfgfhfjgghhffdf",
                                false),
                        new UpdateDPO(
                                50L,
                                "456",
                                "123",
                                null,
                                "fgdhfgfhfjgghhffdf",
                                null,
                                InteractionUnit.MESSAGE
                        )
                )
        );
    }


}