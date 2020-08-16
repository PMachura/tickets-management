package machura.przemyslaw.mobees.ticketsmanagement.common;

import io.vavr.Tuple2;
import io.vavr.control.Try;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {
    public static Tuple2<LocalDate, LocalDate> getFirstAndLastDayOfQuarter(LocalDate referenceDate) {
        LocalDate firstDayOfQuarter = referenceDate.with(IsoFields.DAY_OF_QUARTER, 1L);
        LocalDate lastDayOfQuarter = firstDayOfQuarter.plusMonths(2)
                .with(TemporalAdjusters.lastDayOfMonth());
        return new Tuple2<>(firstDayOfQuarter, lastDayOfQuarter);
    }

    public static List<Integer> splitEvenlyWithReminder(int dividend, int divider) {
        if (divider <= 0) return Collections.emptyList();

        int quotient = dividend / divider;
        int reminder = dividend % divider;
        Stack<Integer> reminderStack = new Stack<>();
        IntStream.range(0, reminder).forEach(value -> reminderStack.push(1));

        return IntStream.range(0, divider)
                .map(number -> quotient + Try.ofSupplier(reminderStack::pop).getOrElseGet(emptyStack -> 0))
                .boxed().collect(Collectors.toList());

    }
}
