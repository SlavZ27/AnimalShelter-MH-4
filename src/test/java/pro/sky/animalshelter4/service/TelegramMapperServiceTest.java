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

/**
 * A test of the mapper class for correct mapping of the {@link Update} object in {@link UpdateDPO}.
 * The correctness of this class greatly affects the operation of the entire program
 */
class TelegramMapperServiceTest {
    private static final Generator generator = new Generator();
    private final TelegramMapperService telegramMapperService = new TelegramMapperService();


    /**
     * The parameterized test compares all the fields that are in the {@link UpdateDPO}
     * and UpdateDPO converted from {@link Update} using the stream arguments
     *
     * @param updateTdo Original incoming {@link Update}
     * @param updateDpo Expected result after mapping
     */
    @ParameterizedTest
    @MethodSource("paramForToDPO")
    void toDPO(Update updateTdo, UpdateDPO updateDpo) {
        UpdateDPO expected = updateDpo;
        UpdateDPO actual = telegramMapperService.toDPO(updateTdo);

        if (expected != null) {
            assertThat(actual.getMessage()).isEqualTo(expected.getMessage());
            assertThat(actual.getUserName()).isEqualTo(expected.getUserName());
            assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
            assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
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
                                "789",
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
                                "789",
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
                                "",
                                null,
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
                        new UpdateDPO(
                                50L,
                                null,
                                null,
                                null,
                                Command.START,
                                "",
                                null,
                                InteractionUnit.COMMAND
                        )
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
                                "789",
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
                                "456",
                                "789",
                                "123",
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
                                "789",
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