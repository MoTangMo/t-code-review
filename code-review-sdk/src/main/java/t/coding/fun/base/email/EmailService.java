package t.coding.fun.base.email;

import javax.mail.MessagingException;

public interface EmailService {

    void sendEmail(String toAddress, String subject, String message) throws MessagingException;

    default void sendEmail(String message) throws Exception {

    }

}
