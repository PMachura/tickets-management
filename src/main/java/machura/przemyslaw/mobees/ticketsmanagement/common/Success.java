package machura.przemyslaw.mobees.ticketsmanagement.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Success {
    private final String message;

    public static Success from(String message){
        return new Success(message);
    }
}
