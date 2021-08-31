
package mail;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * This program shows how to use JavaMail to send mail messages.
 *
 * @author Cay Horstmann
 * @version 1.01 2018-03-17
 */
public class MailTest {
	public static void main(String[] args) throws MessagingException, IOException {
		var props = new Properties();
		
		/**
		 * MailTest\src\main\java
		 * - MailTest : 프로젝트 폴더
		 * - src\main\java : 작업 디렉토리 // 상대주소의 base 디렉토리
		 */
		// 아래 첫 실인자 값인 "mail"은 작업 디렉토리에 있는 경로명의 전반부
		// "mail.properties"은 작업 디렉토리에 있는 경로명의 후반부
		System.out.println("Working Directory = " + System.getProperty("user.dir"));

		try (InputStream in = Files.newInputStream(Paths.get("mail", "mail.properties"))) {
			props.load(in);
		}
		List<String> lines = Files.readAllLines(Paths.get(args[0]), StandardCharsets.UTF_8);

		String from = lines.get(0);
		String to = lines.get(1);
		String subject = lines.get(2);

		var builder = new StringBuilder();
		for (int i = 3; i < lines.size(); i++) {
			builder.append(lines.get(i));
			builder.append("\n");
		}

		Console console = System.console();
		String password = new String(console.readPassword("Password: "));

		Session mailSession = Session.getDefaultInstance(props);
		// mailSession.setDebug(true);
		var message = new MimeMessage(mailSession);
		message.setFrom(new InternetAddress(from));
		message.addRecipient(RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		message.setText(builder.toString());

		try (Transport tr = mailSession.getTransport()) {
			tr.connect("jong479", password);
			tr.sendMessage(message, message.getAllRecipients());
		}
	}
}
