package src.exceptions;

import java.io.IOException;

public class NameInvalidException extends IOException {
    public NameInvalidException(String errorMessage) {
        super(errorMessage);
    }
}
