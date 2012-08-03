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
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.CastUtils;
import org.apache.camel.util.URISupport;


/**
 *
 * @author Steven Swor
 */
public class JamodComponent extends DefaultComponent{
    
    @Override
    protected JamodEndpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        String addressUri = uri;
        if (addressUri.startsWith("jamod://")) {
            addressUri = addressUri.substring(8);
        }else{
            addressUri = remaining;
        }
        Map<String, Object> jamodParameters = new HashMap<String, Object>(parameters);
        /*
         * TODO resolve jamod configuration parameters
         */
        URI endpointUri = URISupport.createRemainingURI(new URI(addressUri), CastUtils.cast(jamodParameters));
        JamodEndpoint endpoint = new JamodEndpoint(endpointUri);
        return endpoint;
    }

}
