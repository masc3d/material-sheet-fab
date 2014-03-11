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

package org.masc.diagnostics;

/**
 *
 * @author masc
 */

/**
 * A class to help benchmark code
 * It simulates a real stop watch
 */
public class Stopwatch {

    private long startTime = -1;
    private long stopTime = -1;
    private boolean running = false;

    public Stopwatch start() {
        startTime = System.currentTimeMillis();
        running = true;
        return this;
    }

    public Stopwatch stop() {
        stopTime = System.currentTimeMillis();
        running = false;
        return this;
    }

    /**
     * returns elapsed time in milliseconds
     * if the watch has never been started then
     * return zero
     */
    public long getElapsedTime() {
        if (startTime == -1) {
            return 0;
        }
        if (running) {
            return System.currentTimeMillis() - startTime;
        } else {
            return stopTime - startTime;
        }
    }

    public Stopwatch reset() {
        startTime = -1;
        stopTime = -1;
        running = false;
        return this;
    }
}
