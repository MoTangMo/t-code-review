package t.coding.fun.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import t.coding.fun.base.git.GitCommand;
import t.coding.fun.base.openai.IOpenAi;

import javax.mail.MessagingException;
import java.io.IOException;


/**
* @Description: 代码审查模板
* @Param:
* @return:
* @Author: tangzhaofeng
* @Date: 3/5/2025
*/
public abstract class BaseCodeReviewHandler implements CodeReviewHandler{

    private final Logger logger = LoggerFactory.getLogger(BaseCodeReviewHandler.class);

    protected final GitCommand gitCommand;
    protected final IOpenAi openAI;

    public BaseCodeReviewHandler(GitCommand gitCommand, IOpenAi openAI) {
        this.gitCommand = gitCommand;
        this.openAI = openAI;
    }


    /**
    * @Description: 获取git提交的代码差异
    * @Param:
    * @return:
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    abstract String getDiff() throws IOException, InterruptedException;

    /** 
    * @Description: 对代码进行审查
    * @Param: codeContent 代码内容
    * @return: 
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    abstract String reviewCode(String codeContent) throws IOException;



    /** 
    * @Description: 记录评审结果，并返回评审结果Url
    * @Param:
    * @return:
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    abstract String recordReview(String reviewResult) throws Exception;



    /**
    * @Description: 推送评审结果，邮件方式
    * @Param:
    * @return:
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    abstract void pushResultUrl(String reviewUrl) throws Exception;


    /**
    * @Description: 执行模板
    * @Param:
    * @return:
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    @Override
    public void doReview() {
        try{
            logger.info("开始代码审查 -> project : {} , branch : {} , author : {} , message : {}", gitCommand.getProject(), gitCommand.getBranch(), gitCommand.getAuthor(), gitCommand.getMessage());
            String diff = getDiff();
            logger.info("获取git提交差异 -> {}", diff);
            if (diff == null || diff.isEmpty()){
                throw new RuntimeException("获取git提交差异为空");
            }
            String reviewResult = reviewCode(diff);
            String reviewUrl = recordReview(reviewResult);
            logger.info("推送评审结果 -> {}", reviewUrl);
            pushResultUrl(reviewUrl);
        }catch (Exception e){
            logger.info("代码审查失败 -> msg : {}" , e);
        }

    }
}
