package Exceptions;

public class WrongFormatException extends Exception {

    public WrongFormatException(String msg){
        super(msg);
    }

    public WrongFormatException(String msg, Throwable cause){
        super(msg, cause);
    }
}
