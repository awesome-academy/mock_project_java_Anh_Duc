package asterisk.sun.booking_tours.common.helper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

/**
 * Helper class for reusing ModelMapper
 * Provides static utility methods for mapping between objects
 */
public class MapperHelper {
    private static final ModelMapper modelMapper = new ModelMapper();

    // Private constructor to prevent instantiation
    private MapperHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Map an object to a destination type
     *
     * @param source Source object
     * @param destinationType Class of destination object
     * @return Mapped destination object, or null if source is null
     */
    public static <S, D> D map(S source, Class<D> destinationType) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationType);
    }

    /**
     * Map a list of objects to a list of destination type
     *
     * @param sources List of source objects
     * @param destinationType Class of destination object
     * @return List of mapped destination objects
     */
    public static <S, D> List<D> mapList(List<S> sources, Class<D> destinationType) {
        if (sources == null) {
            return null;
        }
        return sources.stream()
                .map(source -> map(source, destinationType))
                .collect(Collectors.toList());
    }

    /**
     * Map a list of objects to an array of destination type
     *
     * @param sources List of source objects
     * @param destinationType Class of destination array (e.g., UserDTO[].class)
     * @return Array of mapped destination objects
     */
    public static <S, D> D mapListToArray(List<S> sources, Class<D> destinationType) {
        if (sources == null) {
            return null;
        }
        return modelMapper.map(sources, destinationType);
    }

    /**
     * Map data from source object to existing destination object
     *
     * @param source Source object
     * @param destination Destination object (will be updated)
     */
    public static <S, D> void mapTo(S source, D destination) {
        if (source != null && destination != null) {
            modelMapper.map(source, destination);
        }
    }

    /**
     * Get ModelMapper instance for direct use if needed
     *
     * @return ModelMapper instance
     */
    public static ModelMapper getModelMapper() {
        return modelMapper;
    }
}
