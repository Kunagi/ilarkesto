/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.mswindows;

import ilarkesto.base.Proc;

import java.util.StringTokenizer;

public class Registry {

    public static final String HKCU = "HKCU";
    public static final String HKCU_ENVIRONMENT = "HKEY_CURRENT_USER\\Environment";

    public static void main(String[] args) {
        appendToUsersPath("e:\\daten\\a101zi8");
    }

    public static void appendToUsersPath(String additionalPath) {
        additionalPath = additionalPath.replace('/', '\\');
        String path = getString(HKCU_ENVIRONMENT, "PATH");
        if (path != null && path.indexOf(additionalPath) >= 0) return;
        if (!additionalPath.endsWith(";")) additionalPath += ";";
        if (path == null) path = "%PATH%;";
        if (!path.endsWith(";")) path += ";";
        path += additionalPath;
        setString(HKCU_ENVIRONMENT, "PATH", path);
    }

    public static void setString(String key, String string, String value) {
        Proc proc = new Proc("REG");
        proc.addParameter("ADD");
        proc.addParameter(key);
        proc.addParameter("/f");
        proc.addParameter("/v");
        proc.addParameter(string);
        proc.addParameter("/d");
        proc.addParameter(value);
        proc.start();
        int ret = proc.getReturnCode();
        String output = proc.getOutput();
        if (ret != 0) throw new RuntimeException("Command failed: " + output);
    }

    public static String getString(String key, String string) {
        Proc proc = new Proc("REG");
        proc.addParameter("QUERY");
        proc.addParameter(key);
        proc.addParameter("/v");
        proc.addParameter(string);
        proc.start();
        int ret = proc.getReturnCode();
        String output = proc.getOutput();
        if (ret == 1) return null;
        if (ret != 0) throw new RuntimeException("Command failed: " + output);
        StringTokenizer tokenizer = new StringTokenizer(output, "\n\r");
        String valueLine = null;
        while (tokenizer.hasMoreTokens()) {
            String tok = tokenizer.nextToken().trim();
            if (key.equals(tok)) {
                valueLine = tokenizer.nextToken().trim();
                break;
            }
        }
        if (valueLine == null) { throw new RuntimeException("Parsing command output failed: " + output); }
        valueLine = valueLine.substring(string.length() + 1);
        int idx = valueLine.indexOf('\t') + 1;
        valueLine = valueLine.substring(idx);
        return valueLine;
    }

}
