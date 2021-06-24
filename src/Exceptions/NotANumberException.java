package Exceptions;

public class NotANumberException extends Exception{

    public NotANumberException(String msg){
        super(msg);
    }

    public NotANumberException(String msg, Throwable cause){
        super(msg, cause);
    }
}
