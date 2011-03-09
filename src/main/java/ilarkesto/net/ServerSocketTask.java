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
package ilarkesto.net;

import ilarkesto.concurrent.ATask;
import ilarkesto.concurrent.TaskManager;
import ilarkesto.core.logging.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketTask extends ATask {

	private static final Log LOG = Log.get(ServerSocketTask.class);

	private ClientHandler clientHandler;
	private int port;
	private ServerSocket serverSocket;
	private TaskManager clientHandlerTaskManager;

	public ServerSocketTask(ClientHandler clientHandler, int port, TaskManager clientHandlerTaskManager) {
		this.clientHandler = clientHandler;
		this.port = port;
		this.clientHandlerTaskManager = clientHandlerTaskManager;
	}

	@Override
	protected void perform() {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(1000);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		while (!isAbortRequested()) {
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
				LOG.debug("Client connected:", clientSocket.getInetAddress().getHostAddress() + ":"
						+ clientSocket.getPort());
			} catch (SocketTimeoutException ex) {
				// nop
				continue;
			} catch (IOException ex) {
				clientHandler.onIOException(ex);
				continue;
			}
			ClientHandlerTask clientHandlerTask = new ClientHandlerTask(clientSocket);
			clientHandlerTaskManager.start(clientHandlerTask);
		}
	}

	@Override
	public String toString() {
		return "ServerSocket:" + port;
	}

	class ClientHandlerTask extends ATask {

		private Socket clientSocket;

		public ClientHandlerTask(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		protected void perform() {
			clientHandler.handleClient(clientSocket);
		}

	}

}
