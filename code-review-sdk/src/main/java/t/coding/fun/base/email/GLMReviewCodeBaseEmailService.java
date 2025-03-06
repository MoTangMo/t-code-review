package t.coding.fun.base.email;

import t.coding.fun.utils.EnvUtils;

import javax.mail.MessagingException;

/**
 * @author T
 * @description
 * @date 2025年03月06日 20:33
 */
public class GLMReviewCodeBaseEmailService extends BaseEmailService {

    /**
     * @description 定义消息模板
     * @version 1.0
     */
    private String MSG_TEMPLATE = "😊Hi, %s, \n\n" +
            "Your code review has been completed. Please find the results below:\n\n" +
            "%s\n\n" +
            "Thank you for using our service.\n\n" +
            "Best regards,\n" +
            "GLM Code Review✅";

    private final String subject = "😊 T Code Review -> Your code review results";


    private final String toAddress = EnvUtils.getEnv("EMAIL_TOADDRESS");


    public GLMReviewCodeBaseEmailService(String host, String port, String username, String password, String toAddress) {
        super(host, port, username, password);
    }



    @Override
    public void sendEmail(String message) throws Exception {
        message = String.format(MSG_TEMPLATE, toAddress, message);
        super.sendEmail(toAddress, subject, message);
    }

}
