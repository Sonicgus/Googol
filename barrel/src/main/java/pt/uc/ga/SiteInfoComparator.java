package pt.uc.ga;

import java.util.Comparator;

/**
 * Comparator for SiteInfo objects
 */
public class SiteInfoComparator implements Comparator<SiteInfo> {
    /**
     * Compares two SiteInfo objects based on the number of URLs they have.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return
     */
    @Override
    public int compare(SiteInfo o1, SiteInfo o2) {
        return o2.getNumUrls() - o1.getNumUrls();
    }

}
