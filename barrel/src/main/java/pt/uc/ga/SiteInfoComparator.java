package pt.uc.ga;

import java.util.Comparator;

public class SiteInfoComparator implements Comparator<SiteInfo> {
    @Override
    public int compare(SiteInfo o1, SiteInfo o2) {
        return o2.getNumUrls() - o1.getNumUrls();
    }

}
