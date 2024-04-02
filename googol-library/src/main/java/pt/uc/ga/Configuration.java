package pt.uc.ga;

public class Configuration {
    public static boolean AUTOFAIL_DOWNLOADERS = false;

    public static final int NUM_BARRELS = 2;
    public static final int NUM_DOWNLOADERS = 5;

    public static final int PORT_A = 9080;
    public static final int PORT_B = 9081;

    public static final int RMI_GATEWAY_PORT = 1099;

    public static final int MULTICAST_PORT = 4321;
    public static final String MULTICAST_ADDRESS = "224.3.2.1";
    public static final int MAXIMUM_REFERENCE_LINKS = 10;
}
