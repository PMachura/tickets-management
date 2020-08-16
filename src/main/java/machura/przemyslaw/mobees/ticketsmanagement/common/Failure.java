package machura.przemyslaw.mobees.ticketsmanagement.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Failure {
    private final String reason;

    public static Failure from(String reason){
        return new Failure(reason);
    }

    public static Failure from(Throwable throwable){
        return new Failure(throwable.getMessage());
    }

    public static Failure from(Throwable throwable, String message){
        return Failure.from(message + "\n" + throwable.getMessage());
    }
}
