package at.sepp.mods.villagers.api.license;


import java.security.GeneralSecurityException;

public abstract class LicenseManager {
    public abstract boolean isValid() throws GeneralSecurityException;

    public abstract int daysLeft();

    public abstract String getFeature(String paramString);

    public abstract String getLicense();
}
