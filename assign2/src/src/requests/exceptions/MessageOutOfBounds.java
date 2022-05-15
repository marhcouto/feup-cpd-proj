package requests.exceptions;

public class MessageOutOfBounds extends Exception {
    private String message;

    public MessageOutOfBounds(String message) {
        this.message = message;
    };
}
