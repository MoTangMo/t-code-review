package t.coding.fun.base.openai;

import t.coding.fun.model.openai.ChatCompletionRequestDTO;
import t.coding.fun.model.openai.ChatCompletionSyncResponseDTO;

import java.io.IOException;
import java.net.MalformedURLException;

public interface IOpenAi {

    /**
    * @Description: 基于OpenAi处理请求
    * @Param:
    * @return:
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    ChatCompletionSyncResponseDTO handle(ChatCompletionRequestDTO dto) throws IOException;

}
