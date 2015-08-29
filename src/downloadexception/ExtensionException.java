package downloadexception;

public class ExtensionException extends Exception{

    public ExtensionException(){
        super("The URL contains no file extension");
    }
}
