/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.externaleditor.command;

import static java.lang.String.format;

import org.springframework.core.annotation.Order;

import de.tudarmstadt.ukp.inception.rendering.selection.FocusPosition;

@Order(1000)
public class ScrollToCommand
    implements EditorCommand
{
    private static final long serialVersionUID = 1779280309942407825L;

    private final int offset;
    private final FocusPosition position;

    public ScrollToCommand(int aOffset, FocusPosition aPosition)
    {
        offset = aOffset;
        position = aPosition;
    }

    @Override
    public String command(String aEditorVariable)
    {
        return format("e.scrollTo({ offset: %d, position: '%s'})", offset, position);
    }
}
