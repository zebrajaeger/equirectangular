package de.zebrajaeger.equirectangular.core.imgremove;

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

/**
 * @author Lars Brandt on 05.05.2016.
 */
public class FileNameNotMatchException extends IllegalArgumentException {
    public FileNameNotMatchException() {
    }

    public FileNameNotMatchException(String s) {
        super(s);
    }

    public FileNameNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNameNotMatchException(Throwable cause) {
        super(cause);
    }
}
