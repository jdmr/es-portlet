package mx.edu.um.portlets.esu.utils;

import java.text.NumberFormat;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jdmr
 */
public class TagsUtil {
    
    private static final Logger log = LoggerFactory.getLogger(TagsUtil.class);
    
    public static String[] getTags(String[] tags, DateTime hoy) {
        DateTime inicio = new DateTime(2011, 3, 26, 0, 0, 0, 0, hoy.getZone());
        DateTime t3a2011 = new DateTime(2011, 6, 25, 0, 0, 0, 0, hoy.getZone());
        DateTime t4a2011 = new DateTime(2011, 9, 24, 0, 0, 0, 0, hoy.getZone());
        DateTime t1a2012 = new DateTime(2011, 12, 31, 0, 0, 0, 0, hoy.getZone());
        DateTime t2a2012 = new DateTime(2012, 3, 31, 0, 0, 0, 0, hoy.getZone());
        DateTime t3a2012 = new DateTime(2012, 6, 30, 0, 0, 0, 0, hoy.getZone());
        DateTime t4a2012 = new DateTime(2012, 9, 29, 0, 0, 0, 0, hoy.getZone());
        if (hoy.isBefore(inicio)) {
            hoy = inicio;
        }
        log.debug("HOY: {}", hoy);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);
        Weeks weeks;
        tags[0] = new Integer(hoy.getYear()).toString();
        if (hoy.isEqual(inicio) || (hoy.isAfter(inicio) && hoy.isBefore(t3a2011))) {
            tags[1] = "t2";
            weeks = Weeks.weeksBetween(inicio, hoy);
        } else if (hoy.isEqual(t3a2011) || (hoy.isAfter(t3a2011) && hoy.isBefore(t4a2011))) {
            tags[1] = "t3";
            weeks = Weeks.weeksBetween(t3a2011, hoy);
        } else if (hoy.isEqual(t4a2011) || (hoy.isAfter(t4a2011) && hoy.isBefore(t1a2012))) {
            tags[1] = "t4";
            weeks = Weeks.weeksBetween(t4a2011, hoy);
        } else if (hoy.isEqual(t1a2012) || (hoy.isAfter(t1a2012) && hoy.isBefore(t2a2012))) {
            tags[1] = "t1";
            weeks = Weeks.weeksBetween(t1a2012, hoy);
        } else if (hoy.isEqual(t2a2012) || (hoy.isAfter(t2a2012) && hoy.isBefore(t3a2012))) {
            tags[1] = "t2";
            weeks = Weeks.weeksBetween(t2a2012, hoy);
        } else if (hoy.isEqual(t2a2012) || (hoy.isAfter(t3a2012) && hoy.isBefore(t4a2012))) {
            tags[1] = "t3";
            weeks = Weeks.weeksBetween(t3a2012, hoy);
        } else {
            tags[0] = "2012";
            tags[1] = "t4";
            weeks = Weeks.weeksBetween(t3a2012, hoy);
        }
        tags[2] = "l" + nf.format(weeks.getWeeks() + 1);
        log.debug("TAGS: {} {} {}", tags);

        return tags;
    }
    
    public static String[] getTagsConDia(String[] tags, DateTime hoy) {
        tags = getTags(tags, hoy);
        switch (hoy.getDayOfWeek()) {
            case 1:
                tags[3] = "lunes";
                break;
            case 2:
                tags[3] = "martes";
                break;
            case 3:
                tags[3] = "miercoles";
                break;
            case 4:
                tags[3] = "jueves";
                break;
            case 5:
                tags[3] = "viernes";
                break;
            case 6:
                tags[3] = "sabado";
                break;
            case 7:
                tags[3] = "domingo";
                break;
        }
        log.debug("TAGS: {} {} {} {}", tags);
        
        return tags;
    }
}
