package machura.przemyslaw.mobees.ticketsmanagement.common;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class Failure {

    public enum Status{
        SERVER_ERROR, RESOURCE_NOT_FOUND, ILLEGAL_INPUT, IMPROPER_REQUEST;
    }

    private final String reason;
    private final Status status;

    public static Failure from(String reason){
        return new Failure(reason, Status.SERVER_ERROR);
    }

    public static Failure from(Throwable throwable){
        return new Failure(throwable.getMessage(), Status.SERVER_ERROR);
    }

    public static Failure from(Throwable throwable, String message){
        return Failure.from(message + "\n" + throwable.getMessage());
    }

    public static Failure from(String reason, Status status){
        return new Failure(reason, status);
    }

    public static Failure from(Throwable throwable, Status status){
        return new Failure(throwable.getMessage(), status);
    }

    public static Failure from(Throwable throwable, String message, Status status){
        return Failure.from(message + "\n" + throwable.getMessage(), status);
    }
}
