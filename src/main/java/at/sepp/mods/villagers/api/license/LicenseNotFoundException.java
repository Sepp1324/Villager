package at.sepp.mods.villagers.api.license;

public class LicenseNotFoundException extends Exception {
    private static final String MSG = "License Not Found";

    public LicenseNotFoundException(String msg) {
        super(msg);
    }

    public LicenseNotFoundException() {
        super("License Not Found");
    }
}
