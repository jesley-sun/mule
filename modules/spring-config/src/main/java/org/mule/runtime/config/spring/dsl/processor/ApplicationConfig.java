/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring.dsl.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the application configuration files that describe the integrations. It does not include resource files or application descriptors.
 *
 * An application configuration is defined by an application name and a set of configuration files containing the integration required components.
 *
 * @since 4.0
 */
public class ApplicationConfig
{

    private String applicationName;
    private List<ConfigFile> configFiles = new ArrayList<>();

    private ApplicationConfig()
    {}

    public String getApplicationName() {
        return applicationName;
    }

    public List<ConfigFile> getConfigFiles() {
        return Collections.unmodifiableList(configFiles);
    }

    /**
     * Builder for {@link org.mule.runtime.config.spring.dsl.processor.ApplicationConfig} instances.
     */
    public static class Builder {
        private ApplicationConfig applicationConfig = new ApplicationConfig();

        /**
         * @param applicationName the application name
         * @return the builder
         */
        public Builder setApplicationName(String applicationName) {
            this.applicationConfig.applicationName = applicationName;
            return this;
        }

        /**
         * @param configFile a {@code ConfigFile} to be added to the application.
         * @return the builder
         */
        public Builder addConfigFile(ConfigFile configFile) {
            this.applicationConfig.configFiles.add(configFile);
            return this;
        }

        public ApplicationConfig build() {
            return this.applicationConfig;
        }

    }
}
