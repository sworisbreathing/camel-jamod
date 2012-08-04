/*
 * Copyright 2012 Steven Swor.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cameljamod.impl;

import java.net.URI;

/**
 * Utility class for parsing camel-jamod URIs.
 * 
 * URI syntax:
 *
 * {@code protocol://host[:port]/dataType/reference[?options]}
 *
 * @author Steven Swor
 */
public class JamodUriResolver {

    /**
     * Gets the protocol from the URI.
     * @param uri the uri
     * @return the protocol from the uri
     */
    public static String getProtocolFromUri(final URI uri) {
        return uri.getScheme();
    }

    /**
     * Gets the host from a URI
     * @param uri the uri
     * @return the host from the uri
     */
    public static String getHostFromUri(final URI uri) {
        return uri.getHost();
    }

    /**
     * Gets the port from the URI.
     * @param uri the uri
     * @return the port from the uri, or -1 if no port is specified
     */
    public static int getPortFromUri(final URI uri) {
        return uri.getPort();
    }

    /**
     * Gets the data type from a URI
     * @param uri the uri
     * @return the data type from the uri
     */
    public static String getDataTypeFromUri(final URI uri) {
        return uri.getPath().split("/")[1];
    }

    /**
     * Gets the reference address from a URI.
     * @param uri the uri
     * @return the reference address from the uri
     */
    public static int getReferenceFromUri(final URI uri) {
        return Integer.parseInt(uri.getPath().split("/")[2]);
    }
}
