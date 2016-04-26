/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher.application;

import static org.mule.module.launcher.MuleFoldersUtil.getAppLibFolder;
import org.mule.module.launcher.descriptor.ApplicationDescriptor;
import org.mule.module.launcher.plugin.PluginDescriptor;
import org.mule.util.SplashScreen;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Set;

public class ApplicationStartedSplashScreen extends SplashScreen
{
    public void doBody(ApplicationDescriptor descriptor)
    {
        doBody(String.format("Started app '%s'", descriptor.getName()));
        listAppProperties(descriptor);
        listPlugins(descriptor);
        listLibraries(descriptor);
        listOverrides(descriptor);
    }

    private void listPlugins(ApplicationDescriptor descriptor)
    {
        Set<PluginDescriptor> plugins = descriptor.getPlugins();
        if (!plugins.isEmpty())
        {
            doBody("Application plugins:");
            for (PluginDescriptor plugin : plugins)
            {
                doBody(String.format(VALUE_FORMAT, plugin.getName()));
            }
        }
    }

    private void listAppProperties(ApplicationDescriptor descriptor)
    {
        listItems(descriptor.getAppProperties(), "Application properties:");
    }

    private void listOverrides(ApplicationDescriptor descriptor)
    {
        listItems(descriptor.getLoaderOverride(), "Class loader overrides:");
    }

    private void listLibraries(ApplicationDescriptor descriptor)
    {
        File appLib = getAppLibFolder(descriptor.getName());
        if (appLib.exists())
        {
            String[] appLibraries = appLib.list(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith(".jar");
                }
            });
            listItems(Arrays.asList(appLibraries), "Application libraries:");
        }
    }

}
