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

package sx.swing;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 * @author masc
 */
public class TextAreaLogger {
    private static String NEWLINE = System.getProperty("line.separator");
    /** Buffer holding visible log content */
    private String _buffer = "";
    /** Text area this logger is attached to */
    private JTextArea _textArea;
    /** Maximum amount of log entries */
    private int _maxLines = 100;
    /** Current amount of lines */
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
    public void log(String msg, boolean redraw) {
        if (_lines >= _maxLines) {
            int i = _buffer.indexOf(NEWLINE);
            if (i >= 0) {
                _buffer = _buffer.substring(i + NEWLINE.length());
            }
        }
        _buffer = _buffer + msg + NEWLINE;
        _lines++;

        if (redraw)
            _textArea.setText(_buffer);
    }

    public void log(String msg) {
        this.log(msg, true);
    }

    public void clear() {
        _buffer = "";
        _lines = 0;
        this.redraw();
    }

    public void redraw() {
        _textArea.setText(_buffer);
    }

    public int getMaxLines() {
        return _maxLines;
    }
    public void setMaxLines(int maxLines) { _maxLines = maxLines; }
}
