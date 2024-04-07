package pt.uc.ga;

import java.util.HashMap;

public class FuncLib {
    public static HashMap<String, String> getDici(String info) {
        String[] parts = info.split(";");

        HashMap<String, String> dici = new HashMap<>();

        for (String part : parts) {
            String[] key_value_pair = part.split("\\|");
            if (key_value_pair.length == 2) {
                dici.put(key_value_pair[0].trim(), key_value_pair[1].trim());
            }
        }
        return dici;
    }
}
