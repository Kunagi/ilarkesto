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
package ilarkesto.tools;

import ilarkesto.base.Str;
import ilarkesto.cli.ACommand;
import ilarkesto.cli.Arguments;
import ilarkesto.cli.BadSyntaxException;
import ilarkesto.cli.CommandExecutionFailedException;
import ilarkesto.cli.ValueParameter;
import ilarkesto.io.IO;

import java.io.File;

public class HexCat extends ACommand<HexCat.MyArguments> {

	private static final int BYTES_PER_LINE = 10;

	public static void main(String[] args) throws Throwable {
		byte b = (byte) 0;
		int i = b;
		System.out.println(i);

		HexCat hc = new HexCat();
		hc.print(("ä").getBytes(IO.UTF_8));
		// ApplicationStarter.executeCommand(HexCat.class, args);
	}

	public HexCat() {
		super("Shows contents of a file.");
	}

	@Override
	public Object execute(MyArguments arguments) throws BadSyntaxException, CommandExecutionFailedException {
		if (!arguments.file.isSet()) throw new BadSyntaxException(this, "Missing argument: file");
		File file = new File(arguments.file.getValue());
		if (!file.exists())
			throw new CommandExecutionFailedException(this, "File does not exist: " + file.getAbsolutePath());
		if (!file.isFile()) throw new CommandExecutionFailedException(this, "Not a file: " + file.getAbsolutePath());
		byte[] data = IO.readToByteArray(file);
		print(data);
		return null;
	}

	private void print(byte[] data) {
		int len = data.length;
		int done = 0;
		while (len > done) {
			int count = BYTES_PER_LINE;
			if (count > len - done) count = len - done;
			byte[] line = new byte[count];
			System.arraycopy(data, done, line, 0, count);
			printLine(line);
			done += count;
		}
	}

	private void printLine(byte[] line) {
		for (byte b : line) {
			System.out.print(Str.toBinaryString(b));
			System.out.print(" ");
		}
		System.out.println();
	}

	@Override
	public void assertPermissions() {}

	@Override
	public MyArguments createArguments() {
		return new MyArguments();
	}

	class MyArguments extends Arguments {

		private ValueParameter file = new ValueParameter("file", "the file to cat");

		public MyArguments() {
			super(HexCat.this);
			add(file);
		}
	}
}
