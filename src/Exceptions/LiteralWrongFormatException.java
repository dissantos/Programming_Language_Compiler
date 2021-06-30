package Exceptions;

public class LiteralWrongFormatException extends Exception {

        public LiteralWrongFormatException(String msg){
            super(msg);
        }

        public LiteralWrongFormatException(String msg, Throwable cause){
            super(msg, cause);
        }
}
