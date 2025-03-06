package t.coding.fun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import t.coding.fun.base.email.GLMReviewCodeBaseEmailService;
import t.coding.fun.base.git.GitCommand;
import t.coding.fun.base.openai.IOpenAi;
import t.coding.fun.base.openai.Impl.ChatGLMHandler;
import t.coding.fun.core.GLMCodeReviewHandler;
import static t.coding.fun.utils.EnvUtils.getEnv;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    //定义标题
    private String subject = "T Code Review Result";





    public static void main(String[] args) {
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );
        IOpenAi openAI = new ChatGLMHandler(getEnv("CHATGLM_APIHOST"), getEnv("CHATGLM_APIKEYSECRET"));

        //实例化GLM邮件服务
        GLMReviewCodeBaseEmailService emailService = new GLMReviewCodeBaseEmailService(
                getEnv("EMAIL_HOST"),
                getEnv("EMAIL_PORT"),
                getEnv("EMAIL_USERNAME"),
                getEnv("EMAIL_PASSWORD"),
                getEnv("EMAIL_TOADDRESS")
        );

        //实例化GLM 代码审查器
        GLMCodeReviewHandler openAiCodeReviewService = new GLMCodeReviewHandler(gitCommand, openAI , emailService);
        //执行审查
        openAiCodeReviewService.doReview();
        logger.info("代码审查结束  ----> End");
    }


}