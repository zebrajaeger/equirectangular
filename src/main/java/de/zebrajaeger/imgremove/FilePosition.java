package de.zebrajaeger.imgremove;

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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * wrapper for the tile filename pattern of krpano
 * <p>
 * @author Lars Brandt on 04.05.2016.
 */
public class FilePosition {
    public enum Side {
        FRONT, BACK, LEFT, RIGHT, UP, DOWN
    }

    private int level;
    private Side side;
    private int x;
    private int y;

    public FilePosition(int level, Side side, int x, int y) {
        this.level = level;
        this.side = side;
        this.x = x;
        this.y = y;
    }

    public int getLevel() {
        return level;
    }

    public Side getSide() {
        return side;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
    }

    public static class Builder {
        private static final Pattern FILENAME = Pattern.compile("l(\\d+)_([fblrud])_(\\d+)_(\\d+).jpg");
        private String fileName;

        private Builder() {
        }

        public static Builder of() {
            return new Builder();
        }

        public Builder file(File file) {
            return fileName(file.getName());
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public FilePosition build() {
            if (fileName == null) {
                throw new IllegalArgumentException("You have to put a file(name) before calling build()");
            }

            Matcher matcher = FILENAME.matcher(fileName);
            if (matcher.matches()) {
                int level = Integer.parseInt(matcher.group(1));
                char sideC = matcher.group(2).charAt(0);
                Side side;
                switch (sideC) {
                    case 'f':
                        side = Side.FRONT;
                        break;
                    case 'b':
                        side = Side.BACK;
                        break;
                    case 'l':
                        side = Side.LEFT;
                        break;
                    case 'r':
                        side = Side.RIGHT;
                        break;
                    case 'u':
                        side = Side.UP;
                        break;
                    case 'd':
                        side = Side.DOWN;
                        break;
                    default:
                        side = null;
                }
                int x = Integer.parseInt(matcher.group(4));
                int y = Integer.parseInt(matcher.group(3));

                return new FilePosition(level, side, x, y);
            } else {
                throw new FileNameNotMatchException("the filename '" + fileName + "' does not match the pattern");
            }
        }
    }
}
