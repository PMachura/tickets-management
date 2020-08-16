package machura.przemyslaw.mobees.ticketsmanagement.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result {
    private final Failure failure;
    private final Success success;

    public static Result fromFailure(Failure failure){
        return new Result(failure, null);
    }

    public static Result fromFailure(String reason){
        return fromFailure(Failure.from(reason));
    }

    public static Result fromSuccess(Success success){
        return new Result(null, success);
    }

    public static Result fromSuccess(String message){
        return fromSuccess(Success.from(message));
    }

    public Optional<Failure> getFailure(){
        return Optional.ofNullable(failure);
    }

    public boolean isFailure(){
        return getFailure().isPresent();
    }

    public Optional<Success> getSuccess(){
        return Optional.ofNullable(success);
    }

    public boolean isSuccess(){
        return getSuccess().isPresent();
    }
}
