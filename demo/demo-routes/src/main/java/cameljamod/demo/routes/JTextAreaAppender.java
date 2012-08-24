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
package cameljamod.demo.routes;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import javax.swing.JTextArea;

/**
 *
 * @author Steven Swor
 */
public class JTextAreaAppender extends AppenderBase<ILoggingEvent> {

    private JTextArea textArea;

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    protected void append(final ILoggingEvent eventObject) {
        final JTextArea ta = getTextArea();
        if (ta != null && eventObject != null) {
            ta.append(eventObject.getFormattedMessage());
            ta.append("\n");
        }
    }

}
