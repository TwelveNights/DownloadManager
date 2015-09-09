package exception;

public class ExtensionException extends Exception{

    public ExtensionException(){
        super("The URL contains no file extension");
    }
}
