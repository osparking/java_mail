
package mail;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * This program shows how to use JavaMail to send mail messages.
 *
 * @author Cay Horstmann
 * @version 1.01 2018-03-17
 */
public class MailTest {
	public static void main(String[] args) {
		var props = new Properties();

		/**
		 * 프로젝트 이름: MailTest
		 * 프로젝트 폴더: MailTest\src\main\java
		 * 작업 디렉토리: src\main\java
		 * 상대주소의 base 디렉토리
		 */
		System.out.println("작업 디렉토리 = " + 
							System.getProperty("user.dir"));
		
		Session mailSession = Session.getDefaultInstance(props);
		/**
		 *  아래 첫 실인자 "mail": 작업 디렉토리 내 상대 경로명 전반부
		 *  "mail.properties": 작업 디렉토리 내 상대 경로명 후반부 
		 */
		try (InputStream in = Files.newInputStream(
								Paths.get("mail", "mail.properties"));
				Transport tr = mailSession.getTransport()) {
			
			props.load(in);
			
			List<String> lines;
			int line = 0;
			lines = Files.readAllLines(Paths.get(args[0]), StandardCharsets.UTF_8);
			String from = lines.get(line++);
			String to1 = lines.get(line++);
			String to2 = lines.get(line++);
			String subject = lines.get(line++);
			
			var builder = new StringBuilder();
			for (int i = line++; i < lines.size(); i++) {
				builder.append(lines.get(i));
				builder.append("\n");
			}
			
			var message = new MimeMessage(mailSession);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(RecipientType.TO, new InternetAddress(to1));
			message.addRecipient(RecipientType.CC, new InternetAddress(to2));
			message.setSubject(subject);
			addBodyParts(message, builder);
			
			Console console = System.console();
			String password = new String(console.readPassword("Password: "));			
			
			tr.connect("jbpark03", password);
			tr.sendMessage(message, message.getAllRecipients());
			System.out.println("메일 전송 성공!");
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private static void addBodyParts(MimeMessage message, 
			StringBuilder builder) 
					throws MessagingException, IOException {
		Multipart multipart = new MimeMultipart();
	
		BodyPart messageBodyPart = new MimeBodyPart(); 
		messageBodyPart.setText(builder.toString());
		multipart.addBodyPart(messageBodyPart);
		
		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.attachFile(new File("letter.txt")); // "C:\\Document1.txt";
		multipart.addBodyPart(attachmentPart);
		message.setContent(multipart);
	}
}
