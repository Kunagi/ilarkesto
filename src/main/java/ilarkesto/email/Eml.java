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
package ilarkesto.email;

import ilarkesto.Servers;
import ilarkesto.base.Str;
import ilarkesto.base.Sys;
import ilarkesto.base.Utl;
import ilarkesto.base.time.DateAndTime;
import ilarkesto.core.logging.Log;
import ilarkesto.io.IO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

/**
 * Set of static methods for sending and receiving emails
 */
public class Eml {

	public static void main(String[] args) throws Throwable {
		Log.setDebugEnabled(true);
		Sys.setFileEncoding(IO.UTF_8);

		Message msg = createTextMessage(createDummySession(), "aaa" + Str.UE + "aaa", "aaa" + Str.UE + "aaa",
			"wi@koczewski.de", "wi@koczewski.de");
		OutputStream out = new FileOutputStream("g:/inbox/email-test.msg");
		writeMessage(msg, out);
		out.close();

		// Store store = getStore("imaps", "imap.googlemail.com", "witoslaw.koczewski@googlemail.com", "xxx");
		// try {
		// Folder folder = store.getFolder("INBOX");
		// folder.open(Folder.READ_ONLY);
		// LOG.debug("folder:", folder.getName());
		// for (Message message : folder.getMessages()) {
		// LOG.debug("  message:", getSubject(message), "->", getContentAsText(message));
		// }
		// } finally {
		// closeStore(store);
		// }
		System.exit(0);
	}

	private static final Log LOG = Log.get(Eml.class);

	public static final String HEADER_FROM = "From";
	public static final String HEADER_MESSAGE_ID = "Message-ID";
	public static final String HEADER_MESSAGE_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	public static final String HEADER_MESSAGE_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_REPLY_TO = "Reply-To";
	public static final String HEADER_IN_REPLY_TO = "In-Reply-To";
	public static final String HEADER_X_MAILER = "X-Mailer";
	public static final String HEADER_X_PRIORITY = "X-Priority";
	public static final String HEADER_X_CONFIRM_READING_TO = "X-Confirm-Reading-To";

	public static final String PROTOCOL_IMAP = "imap";
	public static final String PROTOCOL_POP3 = "pop3";
	public static final String PROTOCOL_SMTP = "smtp";

	private static final String X_MAILER = "Witoslaw Koczewski Email-Toolbox, http://www.koczewski.de)";

	private static String charset;

	static {
		setCharset(IO.ISO_LATIN_1);
	}

	public static void writeMessage(Message message, OutputStream out) {
		try {
			Enumeration<Header> enu = message.getAllHeaders();
			while (enu.hasMoreElements()) {
				Header header = enu.nextElement();
				IO.writeText(out, header.getName() + ": " + header.getValue() + "\n", charset);
			}
			IO.writeText(out, "\n", charset);
			IO.copyData(message.getInputStream(), out);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Message getMessageById(String id, Folder folder) {
		try {
			for (Message message : folder.getMessages()) {
				if (id.equals(getMessageId(message))) return message;
			}
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public static Set<String> getAttachmentFilenames(Part part) {
		try {
			Set<String> result = new HashSet<String>();
			if (part.getContentType().toLowerCase().startsWith("multipart")) {
				MimeMultipart multipart;
				try {
					multipart = (MimeMultipart) part.getContent();
					int count = multipart.getCount();
					for (int i = 0; i < count; i++) {
						result.addAll(getAttachmentFilenames(multipart.getBodyPart(i)));
					}
				} catch (NullPointerException ex) {
					// part.getContent() throws NullPointerException
					LOG.info(ex);
				}
			} else {
				String filename = part.getFileName();
				if (filename != null) result.add(Str.decodeQuotedPrintable(filename));
			}
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static InputStream getAttachment(Part part, String filename) {
		try {
			if (filename.equals(part.getFileName())) return part.getInputStream();
			if (part.getContentType().toLowerCase().startsWith("multipart")) {
				MimeMultipart multipart;
				multipart = (MimeMultipart) part.getContent();
				int count = multipart.getCount();
				for (int i = 0; i < count; i++) {
					InputStream in = getAttachment(multipart.getBodyPart(i), filename);
					if (in != null) return in;
				}
			}
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public static String getContentAsText(Part part) {
		String result = getPlainTextContent(part);
		if (result == null) result = Str.html2text(getHtmlTextContent(part));
		return result;
	}

	public static String getHtmlTextContent(Part part) {
		return getTextContent(part, "html");
	}

	public static String getPlainTextContent(Part part) {
		String text = getTextContent(part, "plain");
		if (text == null) {
			text = getTextContent(part, "calendar");
		}
		return text;
	}

	public static String getTextContent(Part part, String type) {
		if (part == null) return null;
		try {
			String contentType;
			try {
				contentType = part.getContentType();
			} catch (Throwable t) {
				contentType = "unknown";
			}
			if (contentType.toLowerCase().startsWith("text/" + type)) {
				// ContentType ct = new ContentType(contentType);
				// String charset = ct.getParameter("charset");
				try {
					Object content = part.getContent();
					if (content == null) return null;
					if (content instanceof String) return (String) content;
					if (content instanceof InputStream) {
						String encoding = charset;
						if (contentType.toLowerCase().contains("UTF")) encoding = IO.UTF_8;
						if (contentType.toLowerCase().contains("ISO")) encoding = IO.ISO_LATIN_1;
						return IO.readToString((InputStream) content, encoding);
					}
					return Utl.toStringWithType(content);
				} catch (UnsupportedEncodingException ex) {
					LOG.warn(ex);
					return null;
				} catch (IOException e) {
					String message = e.getMessage();
					if (message != null) {
						if ("No content".equals(message)) { return null; }
						if (message.toLowerCase().startsWith("unknown encoding")) {
							LOG.warn(e);
							return null;
						}
					}
					throw e;
				} catch (Throwable t) {
					LOG.warn(t);
					return Str.getStackTrace(t);
				}
			}
			if (contentType.toLowerCase().startsWith("multipart")) {
				MimeMultipart multipart;
				try {
					multipart = (MimeMultipart) part.getContent();
				} catch (NullPointerException ex) {
					LOG.warn(ex);
					return null;
				}
				int count = multipart.getCount();
				for (int i = 0; i < count; i++) {
					BodyPart subPart = multipart.getBodyPart(i);
					String filename = subPart.getFileName();
					if (filename != null) continue;
					String text = getTextContent(subPart, type);
					if (text != null) return text.trim();
				}
				return null;
			}
			return null;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static MimeMessage loadMessage(File file) throws MessagingException, IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		MimeMessage message = loadMessage(createDummySession(), in);
		in.close();
		return message;
	}

	public static MimeMessage loadMessage(Session session, InputStream in) throws MessagingException, IOException {
		MimeMessage message = new MimeMessage(session, in);
		message.getContent();
		return message;
	}

	public static void moveMessage(Message message, Folder destination) {
		copyMessage(message, destination);
		try {
			message.setFlag(Flags.Flag.DELETED, true);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		LOG.debug("Message", toString(message), "moved to ", destination.getName());
	}

	public static void copyMessage(Message message, Folder destination) {
		boolean sourceOpened = false;
		boolean destinationOpened = false;
		Folder source = message.getFolder();
		try {
			if (!source.isOpen()) {
				source.open(Folder.READ_ONLY);
				sourceOpened = true;
			}
			if (!destination.isOpen()) {
				destination.open(Folder.READ_WRITE);
				destinationOpened = true;
			}
			try {
				source.copyMessages(new Message[] { message }, destination);
			} catch (MessagingException e) {
				destination.appendMessages(new Message[] { message });
			}
		} catch (MessagingException ex) {
			throw new RuntimeException("Copying message " + toString(message) + " from " + source.getName() + " to "
					+ destination.getName() + " failed.", ex);
		} finally {
			if (sourceOpened) closeFolder(source, false);
			if (destinationOpened) closeFolder(destination, false);
		}
	}

	public static String toString(Message message) {
		StringBuilder sb = new StringBuilder();
		sb.append(getFromFormated(message));
		sb.append(":");
		try {
			sb.append(message.getSubject());
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		return sb.toString();
	}

	public static String getToFormated(Message msg) throws MessagingException {
		StringBuffer sb = new StringBuffer();
		Address[] aa = msg.getRecipients(Message.RecipientType.TO);
		for (int i = 0; i < aa.length; i++) {
			sb.append(aa[i].toString());
			if (i < aa.length - 1) sb.append(", ");
		}
		return sb.toString();
	}

	public static String getReplyTo(Message msg) {
		Address[] aa;
		try {
			aa = msg.getReplyTo();
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		if (aa == null) return null;
		if (aa.length > 0) { return Str.decodeQuotedPrintable(aa[0].toString()); }
		return null;
	}

	public static String getFrom(Message msg) {
		return getHeaderFieldValue(msg, HEADER_FROM);
	}

	public static String getFromFormated(Message msg) {
		StringBuffer sb = new StringBuffer();
		Address[] aa;
		try {
			aa = msg.getFrom();
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		if (aa == null) {
			sb.append("<Kein Absender>");
		} else {
			for (int i = 0; i < aa.length; i++) {
				sb.append(Str.decodeQuotedPrintable(aa[i].toString()));
				if (i < aa.length - 1) sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static Set<String> getTosFormated(Message msg) {
		return getRecipientsFormated(msg, javax.mail.Message.RecipientType.TO);
	}

	public static Set<String> getCcsFormated(Message msg) {
		return getRecipientsFormated(msg, javax.mail.Message.RecipientType.CC);
	}

	public static Set<String> getRecipientsFormated(Message msg, javax.mail.Message.RecipientType type) {
		Address[] aa;
		try {
			aa = msg.getRecipients(type);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		Set<String> result = new HashSet<String>();
		if (aa != null) {
			for (Address a : aa) {
				result.add(Str.decodeQuotedPrintable(a.toString()));
			}
		}
		return result;
	}

	public static DateAndTime getSentTime(Message msg) {
		Date date;
		try {
			date = msg.getSentDate();
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		if (date == null) return null;
		DateAndTime result = new DateAndTime(date);
		if (result.isFuture()) result = DateAndTime.now();
		return result;
	}

	public static String getSubject(Message msg) {
		try {
			return Str.decodeQuotedPrintable(msg.getSubject());
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static MimeMessage createTextMessage(Session session, String subject, String text, String from, String to) {
		try {
			return createTextMessage(session, subject, text, InternetAddress.parse(from)[0],
				InternetAddress.parse(to.replace(';', ',')));
		} catch (AddressException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static MimeMessage createTextMessage(Session session, String subject, String text, Address from, Address[] to) {
		MimeMessage msg = createEmptyMimeMessage(session);
		try {
			msg.setSubject(subject, charset);
			msg.setText(text, charset);
			msg.setFrom(from);
			msg.setRecipients(Message.RecipientType.TO, to);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		return msg;
	}

	public static Address[] parseAddresses(Collection<String> addresses) {
		Address[] ret = new Address[addresses.size()];
		int i = 0;
		for (String address : addresses) {
			try {
				ret[i] = InternetAddress.parse(address)[0];
			} catch (AddressException ex) {
				throw new RuntimeException("Parsing email address failed: " + address, ex);
			}
			i++;
		}
		return ret;
	}

	public static MimeMessage createTextMessageWithAttachments(Session session, String subject, String text,
			String from, Collection<String> tos, Attachment... attachments) {
		try {
			return createTextMessageWithAttachments(session, subject, text, InternetAddress.parse(from)[0],
				parseAddresses(tos), attachments);
		} catch (AddressException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static MimeMessage createTextMessageWithAttachments(Session session, String subject, String text,
			Address from, Address[] to, Attachment... attachments) {
		MimeMessage msg = createEmptyMimeMessage(session);
		try {
			msg.setSubject(subject, charset);
			msg.setFrom(from);
			msg.setRecipients(Message.RecipientType.TO, to);

			Multipart multipart = new MimeMultipart();

			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(text, charset);
			multipart.addBodyPart(textBodyPart);

			if (attachments != null) {
				for (Attachment attachment : attachments) {
					appendAttachment(multipart, attachment);
				}
			}

			msg.setContent(multipart);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		return msg;
	}

	private static void appendAttachment(Multipart multipart, Attachment attachment) throws MessagingException {
		BodyPart fileBodyPart = new MimeBodyPart();
		fileBodyPart.setDataHandler(new DataHandler(attachment.getDataSource()));
		fileBodyPart.setFileName(attachment.getFileName());
		multipart.addBodyPart(fileBodyPart);
	}

	public static MimeMessage createEmptyMimeMessage(Session session) {
		MimeMessage msg = new MimeMessage(session);
		setHeaderFieldValue(msg, HEADER_MESSAGE_ID, UUID.randomUUID() + "@" + Servers.SERVISTO);
		setHeaderFieldValue(msg, HEADER_MESSAGE_CONTENT_TRANSFER_ENCODING, "8bit");
		return msg;
	}

	public static Session createDummySession() {
		Properties p = new Properties();
		p.setProperty("mail.smtp.host", "localhost");
		p.setProperty("mail.smtp.auth", "true");
		Session session = Session.getInstance(p);
		return session;
	}

	public static void sendSmtpMessage(Session session, Message message) {
		try {
			sendSmtpMessage(session, message, message.getAllRecipients());
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Session createSmtpSession(String host, Integer port, boolean tls, String user, String password) {
		if (Str.isBlank(host)) throw new IllegalArgumentException("host ist blank");

		if (Str.isBlank(user)) user = null;
		if (Str.isBlank(password)) password = null;

		Properties p = new Properties();
		p.setProperty("mail.mime.charset", charset);
		p.setProperty("mail.transport.protocol", "smtp");
		p.setProperty("mail.smtp.host", host);
		if (port != null) p.put("mail.smtp.port", port);
		p.put("mail.smtp.starttls.enable", String.valueOf(tls));

		boolean auth = user != null && password != null;
		p.setProperty("mail.smtp.auth", String.valueOf(auth));
		if (user != null) p.setProperty("mail.smtp.auth.user", user);
		if (password != null) p.setProperty("mail.smtp.auth.password", password);

		Session session = Session.getInstance(p);

		if (auth) {
			session.setPasswordAuthentication(new URLName("local"), new PasswordAuthentication(user, password));
		}

		return session;
	}

	public static void sendSmtpMessage(String host, Integer port, boolean tls, String user, String password,
			Message message, Address[] recipients) throws MessagingException {
		sendSmtpMessage(createSmtpSession(host, port, tls, user, password), message, recipients);
	}

	public static void sendSmtpMessage(Session session, Message message, Address[] recipients)
			throws MessagingException {

		// message = cloneMimeMessage(message);
		message.setSentDate(new Date());
		setHeaderFieldValue(message, HEADER_X_MAILER, X_MAILER);
		message.saveChanges();

		LOG.info("Sending message '" + message.getSubject() + "' from '" + Str.format(message.getFrom()) + "' to '"
				+ Str.format(recipients) + "'.");

		Transport trans = session.getTransport("smtp");
		Properties properties = session.getProperties();
		String host = properties.getProperty("mail.smtp.host");
		String port = properties.getProperty("mail.smtp.port");
		if (port == null) port = "25";
		String user = properties.getProperty("mail.smtp.auth.user");
		String password = properties.getProperty("mail.smtp.auth.password");
		trans.connect(host, Integer.parseInt(port), user, password);
		trans.sendMessage(message, recipients);
		trans.close();
	}

	public static MimeMessage cloneMimeMessage(Message msg) throws MessagingException {
		MimeMessage newmsg = new MimeMessage((MimeMessage) msg);
		return newmsg;
	}

	public static String getMessageId(Message msg) {
		String[] header;
		try {
			header = msg.getHeader(HEADER_MESSAGE_ID);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		if (header == null) {
			LOG.debug("Message has no id.'");
			return null;
		}
		return header[0];
	}

	public static String getHeaderFieldValue(Message msg, String fieldName) {
		String[] header;
		try {
			header = msg.getHeader(fieldName);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		if (header == null) return null;
		return header[0];
	}

	public static void setHeaderFieldValue(Message msg, String fieldName, String value) {
		try {
			msg.setHeader(fieldName, value);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void setReplyTo(Message msg, String email) {
		try {
			msg.setReplyTo(new Address[] { new InternetAddress(email) });
		} catch (AddressException ex) {
			throw new RuntimeException(ex);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void addHeaderField(Message msg, String fieldName, String value) {
		try {
			msg.addHeader(fieldName, value);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void addBCC(Message msg, String addresses) {
		try {
			msg.addRecipients(RecipientType.BCC, InternetAddress.parse(addresses.replace(';', ',')));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void addCC(Message msg, String addresses) {
		try {
			msg.addRecipients(RecipientType.CC, InternetAddress.parse(addresses.replace(';', ',')));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Store getStore(String protocol, String host, String user, String password) {
		Properties properties = new Properties();
		properties.setProperty("mail.user", user);
		properties.setProperty("mail.host", host);
		Session session = Session.getInstance(properties);
		Store store;
		try {
			store = session.getStore(protocol);
			store.connect(host, user, password);
		} catch (NoSuchProviderException ex) {
			throw new RuntimeException(ex);
		} catch (MessagingException ex) {
			throw new RuntimeException(ex);
		}
		return store;
	}

	public static void closeStore(Store store) {
		if (store == null) return;
		try {
			store.close();
		} catch (MessagingException ex) {
			ex.printStackTrace();
			// nop
		}
	}

	public static Folder getFolder(Store store, String name, boolean autoCreate) {
		int sepIdx = name.indexOf('/');
		if (sepIdx > 0) {
			String firstName = name.substring(0, sepIdx);
			String lastName = name.substring(sepIdx + 1);
			Folder parent = getFolder(store, firstName, autoCreate);
			if (parent == null) return null;
			return getFolder(parent, lastName, autoCreate);
		}

		Folder folder;
		try {
			folder = store.getFolder(name);
		} catch (MessagingException ex) {
			throw new RuntimeException("Getting folder failed: " + name, ex);
		}
		boolean folderExists;
		try {
			folderExists = folder.exists();
		} catch (MessagingException ex) {
			throw new RuntimeException("Querying folder for existence failed: " + name, ex);
		}
		if (!folderExists) {
			if (!autoCreate) return null;
			boolean created;
			try {
				created = folder.create(Folder.HOLDS_MESSAGES);
			} catch (MessagingException ex) {
				throw new RuntimeException("Creating folder failed: " + name, ex);
			}
			if (!created) throw new RuntimeException("Creating folder failed: " + name);
			LOG.info("Mailbox folder created:", name);
		}
		return folder;
	}

	public static Folder getFolder(Folder store, String name, boolean autoCreate) {
		int sepIdx = name.indexOf('/');
		if (sepIdx > 0) {
			String firstName = name.substring(0, sepIdx);
			String lastName = name.substring(sepIdx + 1);
			Folder parent = getFolder(store, firstName, autoCreate);
			if (parent == null) return null;
			return getFolder(parent, lastName, autoCreate);
		}

		Folder folder;
		try {
			folder = store.getFolder(name);
		} catch (MessagingException ex) {
			throw new RuntimeException("Getting folder failed: " + name, ex);
		}
		boolean folderExists;
		try {
			folderExists = folder.exists();
		} catch (MessagingException ex) {
			throw new RuntimeException("Querying folder for existence failed: " + name, ex);
		}
		if (!folderExists) {
			if (!autoCreate) return null;
			boolean created;
			try {
				created = folder.create(Folder.HOLDS_MESSAGES);
			} catch (MessagingException ex) {
				throw new RuntimeException("Creating folder failed: " + name, ex);
			}
			if (!created) throw new RuntimeException("Creating folder failed: " + name);
			LOG.info("Mailbox folder created:", name);
		}
		return folder;
	}

	public static void closeFolder(Folder folder, boolean delete) {
		if (folder == null) return;
		if (!folder.isOpen()) return;
		try {
			folder.close(delete);
		} catch (Exception ex) {
			if (delete) throw new RuntimeException(ex);
		}
	}

	public static Store getStore(String email) {
		ResourceBundle bundle = ResourceBundle.getBundle("mailstores");
		return getStore(bundle.getString(email + ".protocol"), bundle.getString(email + ".host"),
			bundle.getString(email + ".user"), bundle.getString(email + ".password"));
	}

	public static Address[] parseAddresses(String s) throws AddressException {
		String[] tokens = Str.tokenize(s, ",;:");
		InternetAddress[] ads = new InternetAddress[tokens.length];
		for (int i = 0; i < ads.length; i++) {
			ads[i] = new InternetAddress(tokens[i]);
		}
		return ads;
	}

	public static String parsePureEmail(String richEmailAddress) {
		InternetAddress a;
		try {
			a = new InternetAddress(richEmailAddress);
		} catch (AddressException ex) {
			return null;
		}
		return a.getAddress();
	}

	public static Address[] toAddressArray(Collection<Address> c) {
		Address[] aa = new Address[c.size()];
		Iterator<Address> it = c.iterator();
		int i = 0;
		while (it.hasNext()) {
			aa[i] = it.next();
			i++;
		}
		return aa;
	}

	public static Attachment[] createAttachmentsFromDirContents(File dir) {
		if (dir == null || !dir.exists()) return null;
		return toAttachments(dir.listFiles());
	}

	public static Attachment[] toAttachments(File... files) {
		if (files == null) return null;
		Attachment[] attachments = new Attachment[files.length];
		for (int i = 0; i < files.length; i++) {
			attachments[i] = new Attachment(files[i]);
		}
		return attachments;
	}

	public static void setCharset(String charset) {
		Eml.charset = charset;
		Sys.setProperty("mail.mime.charset", charset);
	}

	public static class Attachment {

		private String fileName;
		private DataSource dataSource;

		public Attachment(String fileName, DataSource dataSource) {
			this.fileName = fileName;
			this.dataSource = dataSource;
		}

		public Attachment(File file) {
			this(file.getName(), new FileDataSource(file));
		}

		public String getFileName() {
			return fileName;
		}

		public DataSource getDataSource() {
			return dataSource;
		}
	}

}