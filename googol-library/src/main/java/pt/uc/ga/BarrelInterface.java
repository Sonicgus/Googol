package pt.uc.ga;

import java.io.IOException;
import java.rmi.Remote;
import java.util.HashSet;

public interface BarrelInterface extends Remote {

    String search(HashSet<String> keywords, int page_number) throws IOException;

    String linkInfo(String url) throws IOException;

}
