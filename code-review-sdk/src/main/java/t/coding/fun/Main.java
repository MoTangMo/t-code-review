package t.coding.fun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import t.coding.fun.base.git.GitCommand;
import t.coding.fun.base.openai.IOpenAi;
import t.coding.fun.base.openai.Impl.ChatGLMHandler;
import t.coding.fun.core.GLMCodeReviewHandler;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // Github 配置
    private String github_review_log_uri;
    private String github_token;


    // github 工程配置 - 依据环境变量获取
    private String github_project;
    private String github_branch;
    private String github_author;


    // ChatGLM 配置
    private String chatglm_apiHost = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private String chatglm_apiKeySecret = "";

    /**
    * @Description: 获取环境变量
    * @Param:
    * @return:
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (null == value || value.isEmpty()) {
            throw new RuntimeException("value is null");
        }
        return value;
    }


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
        //实例化GLM 代码审查器
        GLMCodeReviewHandler openAiCodeReviewService = new GLMCodeReviewHandler(gitCommand, openAI);
        //执行审查
        openAiCodeReviewService.doReview();
        logger.info("代码审查结束  ----> End");
    }


}