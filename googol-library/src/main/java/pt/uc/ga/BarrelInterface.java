package pt.uc.ga;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.util.HashSet;

public interface BarrelInterface extends Remote {
    /**
     * @param keywords
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    String search(HashSet<String> keywords, int page_number) throws FileNotFoundException, IOException;

    String linkInfo(String url) throws FileNotFoundException, IOException;
 
}
