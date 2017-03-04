package javax.servlet;

import javax.servlet.annotation.MultipartConfig;

/**
 * Created by xjk on 3/4/17.
 */
public class MultipartConfigElement {

    private final String location;
    private final long maxFileSize;
    private final long maxRequestSize;
    private final int fileSizeThreshold;

    public MultipartConfigElement(String location) {
        // Keep empty string default if location is null
        if (location != null) {
            this.location = location;
        } else {
            this.location = "";
        }
        this.maxFileSize = -1;
        this.maxRequestSize = -1;
        this.fileSizeThreshold = 0;
    }

    public MultipartConfigElement(String location, long maxFileSize, long maxRequestSize, int fileSizeThreshold) {
        // Keep empty string default if location is null
        if (location != null) {
            this.location = location;
        } else {
            this.location = "";
        }
        this.maxFileSize = maxFileSize;
        this.maxRequestSize = maxRequestSize;
        // Avoid threshold values of less than zero as they cause trigger NPEs
        // in the Commons FileUpload port for fields that have no data.
        if (fileSizeThreshold > 0) {
            this.fileSizeThreshold = fileSizeThreshold;
        } else {
            this.fileSizeThreshold = 0;
        }
    }

    public MultipartConfigElement(MultipartConfig annotation) {
        location = annotation.location();
        maxFileSize = annotation.maxFileSize();
        maxRequestSize = annotation.maxRequestSize();
        fileSizeThreshold = annotation.fileSizeThreshold();
    }

    public String getLocation() {
        return location;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public long getMaxRequestSize() {
        return maxRequestSize;
    }

    public int getFileSizeThreshold() {
        return fileSizeThreshold;
    }
}
