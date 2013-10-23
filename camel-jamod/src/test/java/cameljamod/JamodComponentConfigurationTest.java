/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cameljamod;

import java.net.URISyntaxException;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.camel.Component;
import org.apache.camel.ComponentConfiguration;
import org.apache.camel.Endpoint;
import org.apache.camel.component.timer.TimerComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.ParameterConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author justin
 */
public class JamodComponentConfigurationTest {

    public JamodComponentConfigurationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getParameterConfigurationMap method, of class
     * JamodComponentConfiguration.
     */
    @Test
    public void testGetParameterConfigurationMap() {
        //Test for current behavior...
        System.out.println("Using \"timer\" as an example.");
        final DefaultCamelContext defaultCamelContext = new DefaultCamelContext();
        Component timerComponent = defaultCamelContext.getComponent("timer");
        ComponentConfiguration timerConfig = timerComponent.createComponentConfiguration();
        for (String s : timerConfig.getParameterConfigurationMap().keySet()) {
            System.out.println(String.format("Timer Config: %s -> %s", s, timerConfig.getParameterConfiguration(s).getParameterType()));
        }
        timerConfig.setParameter("fixedRate", Boolean.TRUE);
        System.out.println(String.format("Time BaseURI: %s", timerConfig.getBaseUri()));
        System.out.println(String.format("Time URI String: %s", timerConfig.getUriString()));
        System.out.println("Done using \"timer\" as an example.");
        //End known behavior comparison
        String uri = "jamod:tcp://localhost:1024/coils/0?count=8&changesOnly=true";
        Component jamodComponent = defaultCamelContext.getComponent("jamod");
        ComponentConfiguration jamodConfig = jamodComponent.createComponentConfiguration();
        try {
            jamodConfig.setUriString(uri);
        } catch (URISyntaxException ex) {
            fail("This uri should be valid: " + uri);
        }
        assertEquals("localhost", jamodConfig.getParameter("hostName"));
        assertEquals(1024, jamodConfig.getParameter("port"));
        //TODO decide how this affects the rest of the url resolving.  This should only matter in the configuration API however,
        //but it would be nice to see it used in the rest of the code (the enum for protocol, and data_type.)
        assertEquals(JamodComponentConfiguration.PROTOCOL.tcp, jamodConfig.getParameter("protocol"));
    }
}
