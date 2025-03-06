package t.coding.fun.base.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


/**
 * @description 发送邮件服务
 * @param 
 * @return 
 * @author T
 * @date 2025/3/6
 * @version 1.0
 */
public abstract class BaseEmailService implements EmailService{

    private String host;
    private String port;
    private String username;
    private String password;

    public BaseEmailService(String host, String port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void sendEmail(String toAddress, String subject, String message) throws MessagingException {
        // 设置邮件服务器属性
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // 创建会话
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        // 创建邮件消息
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new java.util.Date());
        msg.setText(message);
        // 发送邮件
        Transport.send(msg);
    }

}
