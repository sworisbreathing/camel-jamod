/*
 *  Copyright 2012 Steven Swor.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package cameljamod;

import java.text.MessageFormat;
import org.eclipse.gemini.blueprint.test.AbstractConfigurableBundleCreatorTests;
import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 *
 * @author Steven Swor
 */
public class OSGITest extends AbstractConfigurableBundleCreatorTests {
    
    public void testOsgiEnvironment() throws Exception {
        System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VENDOR));
	System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_VERSION));
	System.out.println(bundleContext.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
        Bundle[] bundles = bundleContext.getBundles();
        System.out.println("Bundles:");
	for (int i = 0; i < bundles.length; i++) {
		System.out.println(MessageFormat.format("{0} ({1})", OsgiStringUtils.nullSafeNameAndSymName(bundles[i]), bundles[i].getVersion()));
	}
    }
}
