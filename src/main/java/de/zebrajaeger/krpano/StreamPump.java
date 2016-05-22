package de.zebrajaeger.krpano;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lars on 21.05.2016.
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
