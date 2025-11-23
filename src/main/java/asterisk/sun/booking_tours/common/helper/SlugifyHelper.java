package asterisk.sun.booking_tours.common.helper;

import com.github.slugify.Slugify;

public class SlugifyHelper {
    private static final Slugify slugify = Slugify.builder().build();

    public static String toSlug(String input) {
        if (input == null) {
            return null;
        }
        return slugify.slugify(input);
    }
}
