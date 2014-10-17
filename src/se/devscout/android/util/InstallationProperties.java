package se.devscout.android.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * Class for accessing an unique identifier (UUID) which is generated when the
 * app is first started.
 */
public class InstallationProperties {

    private static final String FILE_NAME = "installation.properties";
    private static final String PROP_ID = "id";
    private static InstallationProperties instance;

    private Properties mProperties;
    private File mPropertiesFile;

    private InstallationProperties(Context context) throws IOException {
        mPropertiesFile = new File(context.getFilesDir(), FILE_NAME);
        LogUtil.d(InstallationProperties.class.getName(), "Installation properties file is " + mPropertiesFile.getAbsolutePath());
        read();
    }

    public static InstallationProperties getInstance(Context context) {
        if (instance == null) {
            try {
                instance = new InstallationProperties(context);
            } catch (IOException e) {
                LogUtil.e(InstallationProperties.class.getName(), "Could not create/load installation properties. Expect problems.", e);
            }
        }
        return instance;
    }

    /**
     * Loads the installation id. Triggers the creation of an id if it does not
     * already exist.
     */
    private void read() throws IOException {
        if (!mPropertiesFile.exists()) {
            initPropertiesFile();
        }
        LogUtil.d(InstallationProperties.class.getName(), "Installation properties file is about to be read.");
        FileInputStream stream = new FileInputStream(mPropertiesFile);
        mProperties = new Properties();
        mProperties.load(stream);
        stream.close();
        LogUtil.d(InstallationProperties.class.getName(), mProperties.size() + " installation properties have been read from file.");
    }

    /**
     * Creates the properties file and generates the installation id.
     */
    private void initPropertiesFile() throws IOException {
        LogUtil.d(InstallationProperties.class.getName(), "Installation properties file is about to be created.");
        mPropertiesFile.createNewFile();
        FileOutputStream stream = new FileOutputStream(mPropertiesFile);
        mProperties = new Properties();
        mProperties.setProperty(PROP_ID, UUID.randomUUID().toString());
        mProperties.store(stream, null);
        stream.close();
        LogUtil.d(InstallationProperties.class.getName(), "Installation properties file has been created.");
    }

    /**
     * Returns the UUID which identifies the app installation, basically a
     * substitute for a device id.
     */
    public UUID getId() {
        return UUID.fromString(mProperties.getProperty(PROP_ID));
    }
}
