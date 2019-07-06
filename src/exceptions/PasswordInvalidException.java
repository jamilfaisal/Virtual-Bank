package src.exceptions;

import java.io.IOException;

public class PasswordInvalidException extends IOException {
    public PasswordInvalidException(String errorMessage) {
        super(errorMessage);
    }
}
