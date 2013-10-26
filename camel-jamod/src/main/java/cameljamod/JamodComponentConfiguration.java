/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cameljamod;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import static cameljamod.JamodUriResolver.*;
import java.net.URI;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.ComponentConfigurationSupport;
import org.apache.camel.impl.ParameterConfiguration;
import org.apache.camel.util.URISupport;
import org.apache.camel.util.UnsafeUriCharactersEncoder;

/**
 *
 * @author justin
 */
public class JamodComponentConfiguration extends ComponentConfigurationSupport {

    private static final SortedMap<String, ParameterConfiguration> CONFIGS = new TreeMap<String, ParameterConfiguration>();

    private final Map<String, Object> values = new HashMap<String, Object>();
    public static final String PROTOCOL_KEY = "protocol";
    public static final String HOST_NAME_KEY = "hostName";
    public static final String PORT_KEY = "port";
    public static final String DATA_TYPE_KEY = "dataType";

    public static final DATA_TYPES DEFAULT_DATA_TYPE = DATA_TYPES.register;
    public static final String DEFAULT_HOST = "localhost";
    //TODO this should probably be a constant somewhere else as a default port.
    public static final Integer DEFAULT_PORT = Integer.valueOf(1024);
    public static final PROTOCOL DEFAULT_PROTOCOL = PROTOCOL.tcp;
    public static final String REFERENCE_ADDRESS_KEY = "referenceAddress";
    public static final Integer DEFAULT_REFERENCE_ADDRESS = Integer.valueOf(0);
    public static final String CHANGES_ONLY_KEY = "changesOnly";

    static {
        CONFIGS.put(DATA_TYPE_KEY, new ParameterConfiguration("dataType", DATA_TYPES.class));
        CONFIGS.put(HOST_NAME_KEY, new ParameterConfiguration("hostName", String.class));
        CONFIGS.put(PROTOCOL_KEY, new ParameterConfiguration("protocol", PROTOCOL.class));
        CONFIGS.put(PORT_KEY, new ParameterConfiguration("port", Integer.TYPE));
        CONFIGS.put("delay", new ParameterConfiguration("delay", Integer.TYPE));
        CONFIGS.put(REFERENCE_ADDRESS_KEY, new ParameterConfiguration("referenceAddress", Integer.TYPE));
        CONFIGS.put("initialDelay", new ParameterConfiguration("initialDelay", Integer.TYPE));
        CONFIGS.put("count", new ParameterConfiguration("count", Integer.TYPE));
        CONFIGS.put(CHANGES_ONLY_KEY, new ParameterConfiguration(CHANGES_ONLY_KEY, Boolean.TYPE));
        CONFIGS.put(JamodEndpoint.SLAVE_ID, new ParameterConfiguration(JamodEndpoint.SLAVE_ID, Integer.TYPE));
    }

    //This is a list of the parameters that are ignored in the uri building
    private static final Set<String> IGNORED_PARAMS = new HashSet<String>();

    static {
        IGNORED_PARAMS.add(DATA_TYPE_KEY);
        IGNORED_PARAMS.add(PORT_KEY);
        IGNORED_PARAMS.add(PROTOCOL_KEY);
        IGNORED_PARAMS.add(REFERENCE_ADDRESS_KEY);
        IGNORED_PARAMS.add(HOST_NAME_KEY);
    }

    public static enum PROTOCOL {

        tcp,
        udp
    }

    public static enum DATA_TYPES {

        discreteInputs,
        coils,
        registers,
        register,
        inputRegisters,
        inputRegister
    }

    public JamodComponentConfiguration(Component component) {
        super(component);
    }

    public SortedMap<String, ParameterConfiguration> getParameterConfigurationMap() {
        return Collections.unmodifiableSortedMap(CONFIGS);
    }

    public Object getEndpointParameter(Endpoint endpoint, String name) throws RuntimeCamelException {
        //TODO implement this
        return null;
    }

    public void setEndpointParameter(Endpoint endpoint, String name, Object value) throws RuntimeCamelException {
        //TODO implement this
    }

    @Override
    public String getBaseUri() {
        return String.format("jamod:%s://%s:%s/%s/%s", getProtocol(), getHostName(), getPort(), getDataType(), getReferenceAddress());
    }

    @Override
    public void setUriString(final String uriString) throws URISyntaxException {
        try {
            String newUriString;
            final String prefix = "jamod:";
            if (uriString.startsWith(prefix)) {
                newUriString = uriString.substring(prefix.length());
            } else {
                newUriString = uriString;
            }
            URI uri = URI.create(newUriString);
            String protocolFromUri = getProtocolFromUri(uri);
            if (protocolFromUri == null) {
                protocolFromUri = DEFAULT_PROTOCOL.toString();
            }
            setParameter(PROTOCOL_KEY, PROTOCOL.valueOf(protocolFromUri));
            setParameter(PORT_KEY, getPortFromUri(uri));
            setParameter(HOST_NAME_KEY, getHostFromUri(uri));
            String dataTypeFromUri = getDataTypeFromUri(uri);
            if (dataTypeFromUri == null) {
                dataTypeFromUri = DEFAULT_DATA_TYPE.toString();
            }
            setParameter(DATA_TYPE_KEY, DATA_TYPES.valueOf(dataTypeFromUri));
            setParameter(REFERENCE_ADDRESS_KEY, getReferenceFromUri(uri));

            int idx = newUriString.indexOf('?');
            Map<String, Object> newParameters = Collections.emptyMap();
            if (idx >= 0) {
                String query = newUriString.substring(idx + 1);
                newParameters = URISupport.parseQuery(query, true);
            }
            for (Map.Entry<String, Object> entry : newParameters.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                if (key.equals(CHANGES_ONLY_KEY)) {
                    setParameter(key, value.toString().equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE);
                } else {

                    setParameter(key, value);
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new URISyntaxException(uriString, ex.getMessage());
        }
    }

    @Override
    public String getUriString() {
        List<String> queryParams = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : getParameters().entrySet()) {
            if (IGNORED_PARAMS.contains(entry.getKey())) {
                //These don't need to go in the output.
                continue;
            }
            String key = entry.getKey();
            Object value = entry.getValue();
            // convert to "param=value" format here, order will be preserved
            if (value instanceof List) {
                for (Object item : (List<?>) value) {
                    queryParams.add(key + "=" + UnsafeUriCharactersEncoder.encode(item.toString()));
                }
            } else {
                queryParams.add(key + "=" + UnsafeUriCharactersEncoder.encode(value.toString()));
            }
        }
        Collections.sort(queryParams);
        StringBuilder builder = new StringBuilder();
        String base = getBaseUri();
        if (base != null) {
            builder.append(base);
        }
        String separator = "?";
        for (String entry : queryParams) {
            builder.append(separator);
            builder.append(entry);
            separator = "&";
        }
        return builder.toString();
    }

    public PROTOCOL getProtocol() {
        PROTOCOL p = (PROTOCOL) getParameter(PROTOCOL_KEY);
        if (p == null) {
            p = DEFAULT_PROTOCOL;
        }
        return p;
    }

    public String getHostName() {
        String retVal = (String) getParameter(HOST_NAME_KEY);
        if (retVal == null) {
            retVal = DEFAULT_HOST;
        }
        return retVal;
    }

    public Integer getPort() {
        Integer retVal = (Integer) getParameter(PORT_KEY);
        if (retVal == null) {

            retVal = DEFAULT_PORT;
        }
        return retVal;
    }

    public DATA_TYPES getDataType() {
        DATA_TYPES retVal = (DATA_TYPES) getParameter(DATA_TYPE_KEY);
        if (retVal == null) {
            retVal = DEFAULT_DATA_TYPE;
        }
        return retVal;
    }

    public Integer getReferenceAddress() {
        Integer retVal = (Integer) getParameter(REFERENCE_ADDRESS_KEY);
        if (retVal == null) {
            return DEFAULT_REFERENCE_ADDRESS;
        }
        return retVal;
    }

}
