package de.zebrajaeger.common;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lars Brandt on 22.05.2016.
 */
public class ProcessUtils {
    protected static Pattern PROCESS_LINE_PATTERN = Pattern.compile("(.+\\S)\\s+(\\d+)\\s+(.*\\S)\\s+(\\d+)\\s+([\\d\\.]+)\\s+K");

    public static ProcessManager tasklist() {
        List<ProcessLine> processes = new LinkedList<>();
        try {
            String line;
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                ProcessLine pl = ProcessLine.of(line);
                if (pl != null) {
                    processes.add(pl);
                }
                //System.out.println(line); //<-- Parse data here.
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return new ProcessManager(processes);
    }

    public static class ProcessManager {
        private List<ProcessLine> processes;

        public ProcessManager(List<ProcessLine> processes) {
            this.processes = processes;
        }

        public ProcessLine findByName(String name, boolean exactMatch) {
            List<ProcessLine> allByName = findAllByName(name, exactMatch);
            if (allByName.isEmpty()) {
                return null;
            } else {
                return allByName.get(0);
            }
        }

        public List<ProcessLine> findAllByName(String name, boolean exactMatch) {
            List<ProcessLine> result = new LinkedList<>();
            if (exactMatch) {
                for (ProcessLine pl : processes) {
                    if (pl.getName().equals(name)) {
                        result.add(pl);
                    }
                }
            } else {
                String searchFor = name.toLowerCase();
                for (ProcessLine pl : processes) {
                    String n = pl.getName().toLowerCase();
                    if (n.contains(searchFor)) {
                        result.add(pl);
                    }
                }
            }
            return result;
        }
    }


    public static class ProcessLine {

        private ProcessLine() {

        }

        private String name;
        private int pid;
        private String sessionName;
        private int sessionIndex;
        private int memoryUsage;

        public String getName() {
            return name;
        }

        public int getPid() {
            return pid;
        }

        public String getSessionName() {
            return sessionName;
        }

        public int getSessionIndex() {
            return sessionIndex;
        }

        public int getMemoryUsage() {
            return memoryUsage;
        }

        public static ProcessLine of(String line) {
            Matcher matcher = ProcessUtils.PROCESS_LINE_PATTERN.matcher(line);
            if (matcher.matches()) {
                ProcessLine result = new ProcessLine();
                result.name = matcher.group(1);
                result.pid = Integer.parseInt(matcher.group(2));
                result.sessionName = matcher.group(3);
                result.sessionIndex = Integer.parseInt(matcher.group(4));
                result.memoryUsage = Integer.parseInt(matcher.group(5).replaceAll("\\.", ""));
                return result;
            }
            return null;
        }

        public static void main(String[] args) {
            tasklist();
        }
    }
}
