package de.zebrajaeger.krpano;

/*-
 * #%L
 * de.zebrajaeger:equirectangular
 * %%
 * Copyright (C) 2016 - 2018 Lars Brandt
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Lars Brandt on 21.05.2016.
 */
@Deprecated
public class StreamPump implements Runnable {
    private String name;
    private Thread thread;
    private InputStream from;
    private OutputStream to;

    public StreamPump(String name, InputStream from, OutputStream to) {
        this(from, to);
        this.name = name;
    }

    public StreamPump(InputStream from, OutputStream to) {
        this.from = from;
        this.to = to;
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        } else {
            throw new IllegalStateException("thread already running");
        }
    }

    public Thread getThread() {
        return thread;
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return Integer.toString(hashCode());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Start StreamPump '" + getName() + "'");
            process();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Finished StreamPump '" + getName() + "'");
            thread = null;
        }
    }

    private void process() throws IOException, InterruptedException {
        for (; ; ) {
            int av = from.available();
            if (av == -1) {
                break;
            } else if (av == 0) {
                Thread.sleep(1);
            } else {
                int c = from.read();
                //if(c=='\n' || c=='\n' || Character.isAlphabetic(c)) {
                to.write(c);
                //}else{
                //  to.write('\n');
                //}
            }
        }
    }

}
