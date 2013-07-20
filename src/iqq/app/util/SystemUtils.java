/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */

package iqq.app.util;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a set of utilities to retrieve various operating system information.
 * Futher on operating system called shortly - OS.
 *
 * @author Mikle Garin
 * @since 1.3
 */

public class SystemUtils
{
    // OS short names
    public static final String WINDOWS = "win";
    public static final String MAC = "mac";
    public static final String LINUX = "linux";
    public static final String UNIX = "unix";
    public static final String SOLARIS = "solaris";

    /**
     * Copies text to system clipboard.
     *
     * @param text text to copy into clipboard
     */
    public static void copyToClipboard ( String text )
    {
        try
        {
            Clipboard clipboard = Toolkit.getDefaultToolkit ().getSystemClipboard ();
            clipboard.setContents ( new StringSelection ( text ), null );
        }
        catch ( Throwable e )
        {
            e.printStackTrace ();
        }
    }

    /**
     * Returns java version.
     *
     * @return java version
     */
    public static String getJavaVersion ()
    {
        return System.getProperty ( "java.version" );
    }

    /**
     * Returns java vm name.
     *
     * @return java vm name
     */
    public static String getJavaName ()
    {
        return System.getProperty ( "java.vm.name" );
    }

    /**
     * Returns java vm vendor.
     *
     * @return java vm vendor
     */
    public static String getJavaVendor ()
    {
        return System.getProperty ( "java.vm.vendor" );
    }

    /**
     * Returns short OS name.
     *
     * @return short OS name
     */
    public static String getShortOsName ()
    {
        if ( isWindows () )
        {
            return WINDOWS;
        }
        else if ( isMac () )
        {
            return MAC;
        }
        else if ( isUnix () )
        {
            return UNIX;
        }
        else if ( isSolaris () )
        {
            return SOLARIS;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns OS icon.
     *
     * @return OS icon
     */
    public static ImageIcon getOsIcon ()
    {
        return getOsIcon ( 16 );
    }

    /**
     * Returns OS icon of specified size if such exists; returns 16x16 icon otherwise.
     *
     * @param size preferred icon size
     * @return OS icon
     */
    public static ImageIcon getOsIcon ( int size )
    {
        if ( size != 16 && size != 32 )
        {
            size = 16;
        }
        String os = getShortOsName ();
        return os != null ? new ImageIcon ( SystemUtils.class.getResource ( "icons/os/" + size + "/" + os + ".png" ) ) : null;
    }

    /**
     * Is system allows advanced window options
     */

    /**
     * Returns whether window transparency is supported on current OS or not.
     *
     * @return true if window transparency is supported on current OS; false otherwise
     */
    public static boolean isWindowTransparencyAllowed ()
    {
        // todo Replace when Linux will have proper support for transparency
        return isWindows () || isMac () || isSolaris ();
    }

    /**
     * Returns whether current OS is windows or not.
     *
     * @return true if current OS is windows, false otherwise
     */
    public static boolean isWindows ()
    {
        return getOsName ().toLowerCase ().contains ( "win" );
    }
    
    public static boolean isLinux ()
    {
        return getOsName ().toLowerCase ().contains ( "linux" );
    }

    /**
     * Returns whether current OS is mac or not.
     *
     * @return true if current OS is mac, false otherwise
     */
    public static boolean isMac ()
    {
        String os = getOsName ().toLowerCase ();
        return os.contains ( "mac" ) || os.contains ( "darwin" );
    }

    /**
     * Returns whether current OS is unix or not.
     *
     * @return true if current OS is unix, false otherwise
     */
    public static boolean isUnix ()
    {
        String os = getOsName ().toLowerCase ();
        return os.contains ( "nix" ) || os.contains ( "nux" );
    }

    /**
     * Returns whether current OS is solaris or not.
     *
     * @return true if current OS is solaris, false otherwise
     */
    public static boolean isSolaris ()
    {
        return getOsName ().toLowerCase ().contains ( "sunos" );
    }

    /**
     * Returns OS architecture.
     *
     * @return OS architecture
     */
    public static String getOsArch ()
    {
        return ManagementFactory.getOperatingSystemMXBean ().getArch ();
    }

    /**
     * Returns OS name.
     *
     * @return OS name
     */
    public static String getOsName ()
    {
        return ManagementFactory.getOperatingSystemMXBean ().getName ();
    }

    /**
     * Returns OS version.
     *
     * @return OS version
     */
    public static String getOsVersion ()
    {
        return ManagementFactory.getOperatingSystemMXBean ().getVersion ();
    }

    /**
     * Returns the number of processors available to the Java virtual machine.
     *
     * @return number of processors
     */
    public static int getOsProcessors ()
    {
        return ManagementFactory.getOperatingSystemMXBean ().getAvailableProcessors ();
    }

    /**
     * Returns JRE architecture.
     *
     * @return JRE architecture
     */
    public static String getJreArch ()
    {
        return getJreArchName ().contains ( "64" ) ? "64" : "32";
    }

    /**
     * Returns JRE architecture name.
     *
     * @return JRE architecture name
     */
    public static String getJreArchName ()
    {
        return System.getProperty ( "sun.arch.data.model" );
    }

    /**
     * Returns whether Caps Lock is on or not.
     *
     * @return true if Caps Lock is on, false otherwise
     */
    public static boolean isCapsLock ()
    {
        return Toolkit.getDefaultToolkit ().getLockingKeyState ( KeyEvent.VK_CAPS_LOCK );
    }

    /**
     * Returns whether Num Lock is on or not.
     *
     * @return true if Num Lock is on, false otherwise
     */
    public static boolean isNumLock ()
    {
        return Toolkit.getDefaultToolkit ().getLockingKeyState ( KeyEvent.VK_NUM_LOCK );
    }

    /**
     * Returns whether Scroll Lock is on or not.
     *
     * @return true if Scroll Lock is on, false otherwise
     */
    public static boolean isScrollLock ()
    {
        return Toolkit.getDefaultToolkit ().getLockingKeyState ( KeyEvent.VK_SCROLL_LOCK );
    }

    /**
     * Returns default GraphicsConfiguration for main screen.
     *
     * @return mail scren GraphicsConfiguration
     */
    public static GraphicsConfiguration getGraphicsConfiguration ()
    {
        return getGraphicsEnvironment ().getDefaultScreenDevice ().getDefaultConfiguration ();
    }

    /**
     * Returns default GraphicsEnvironment.
     *
     * @return default GraphicsEnvironment
     */
    private static GraphicsEnvironment getGraphicsEnvironment ()
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment ();
    }

    /**
     * Returns list of available screen devices.
     *
     * @return list of available screen devices
     */
    public static List<GraphicsDevice> getGraphicsDevices ()
    {
        // Retrieving system devices
        GraphicsEnvironment graphicsEnvironment = getGraphicsEnvironment ();
        GraphicsDevice[] screenDevices = graphicsEnvironment.getScreenDevices ();
        GraphicsDevice defaultScreenDevice = graphicsEnvironment.getDefaultScreenDevice ();

        // Collecting devices into list
        List<GraphicsDevice> devices = new ArrayList<GraphicsDevice> ();
        for ( GraphicsDevice gd : screenDevices )
        {
            if ( gd.getType () == GraphicsDevice.TYPE_RASTER_SCREEN )
            {
                if ( gd == defaultScreenDevice )
                {
                    // Add default device to list start
                    devices.add ( 0, gd );
                }
                else
                {
                    devices.add ( gd );
                }
            }
        }
        return devices;
    }
}