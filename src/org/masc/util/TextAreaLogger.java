/*
 *  Copyright (C) 2010 masc
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.masc.util;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

/**
 * @author masc
 */
public class TextAreaLogger {
    private static String NEWLINE = System.getProperty("line.separator");
    private JTextArea _textArea;
    /**
     * Maximum amount of log entries
     */
    private int _maxLines = 100;
    /**
     * Current amount of lines
     */
    private int _lines = 0;

    public TextAreaLogger(JTextArea textArea) {
        _textArea = textArea;

        // Configure caret for text area to always scroll to bottom
        DefaultCaret caret = (DefaultCaret) _textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    /**
     * log
     *
     * @param msg message
     */
    public void log(String msg) {
        String text = _textArea.getText();
        if (_lines >= _maxLines) {
            int i = text.indexOf(NEWLINE);
            if (i >= 0) {
                text = text.substring(i + NEWLINE.length());
            }
        }
        _textArea.setText(text + msg + NEWLINE);
        _lines++;
    }

    public void clear() {
        _textArea.setText("");
        _lines = 0;
    }

    public int getMaxLines() {
        return _maxLines;
    }
}
