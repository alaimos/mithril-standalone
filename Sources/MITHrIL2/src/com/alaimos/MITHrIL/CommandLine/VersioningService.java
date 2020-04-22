package com.alaimos.MITHrIL.CommandLine;

import com.alaimos.Commons.CommandLine.AbstractOptions;
import com.alaimos.Commons.CommandLine.Options;
import com.alaimos.Commons.CommandLine.Service;
import com.alaimos.Commons.Utils.Version;
import com.alaimos.MITHrIL.Constants;
import com.alaimos.MITHrIL.Data.Reader.RemoteVersionReader;

/**
 * Shows current version of MITHrIL and eventual updates available
 *
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 06/01/2016
 */
public class VersioningService implements Service {

    Options o = new AbstractOptions() {

        /**
         * Checks if the help has been requested
         *
         * @return help requested?
         */
        @Override
        public boolean getHelp() {
            return super.getHelp();
        }
    };

    @Override
    public String getShortName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Shows current version of MITHrIL and checks for updates";
    }

    @Override
    public Options getOptions() {
        return o;
    }

    /**
     * Gets the list of all organisms and prints it
     */
    @Override
    public void run() {
        RemoteVersionReader rv = new RemoteVersionReader();
        String version = rv.read();
        System.out.println("MITHrIL version \"" + Constants.CURRENT_VERSION + "\"\n");
        System.out.print("    ");
        if (Version.compare(version).with(Constants.CURRENT_VERSION) <= 0) {
            System.out.println("No updates available.");
        } else {
            System.out.println("New version available: \"" + version + "\".");
        }
    }
}
