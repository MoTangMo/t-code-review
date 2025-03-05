package t.coding.fun.base.git;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GitCommand {

    private final Logger logger = LoggerFactory.getLogger(GitCommand.class);

    private final String githubReviewLogUri;

    private final String githubToken;

    private final String project;

    private final String branch;

    private final String author;

    private final String message;

    public GitCommand(String githubReviewLogUri, String githubToken, String project, String branch, String author, String message) {
        this.githubReviewLogUri = githubReviewLogUri;
        this.githubToken = githubToken;
        this.project = project;
        this.branch = branch;
        this.author = author;
        this.message = message;
    }

    /**
    * @Description: 获取提交的代码差异
    * @Param:
    * @return:
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    public String diff() throws  InterruptedException, IOException {
        ProcessBuilder logProcessBuilder = new ProcessBuilder("git", "log", "-1", "--pretty=format:%H");
        logProcessBuilder.directory(new File("."));
        Process logProcess = logProcessBuilder.start();

        BufferedReader logReader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        String latestCommitHash = logReader.readLine();
        logReader.close();
        logProcess.waitFor();

        ProcessBuilder diffProcessBuilder = new ProcessBuilder("git", "diff", latestCommitHash + "^", latestCommitHash);
        diffProcessBuilder.directory(new File("."));
        Process diffProcess = diffProcessBuilder.start();

        StringBuilder diffCode = new StringBuilder();
        BufferedReader diffReader = new BufferedReader(new InputStreamReader(diffProcess.getInputStream()));
        String line;
        while ((line = diffReader.readLine()) != null) {
            diffCode.append(line).append("\n");
        }
        diffReader.close();

        int exitCode = diffProcess.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to get diff, exit code:" + exitCode);
        }
        return diffCode.toString();
    }

    /**
    * @Description: 控制 git 提交和推送评审日志
    * @Param:
    * @return:
    * @Author: tangzhaofeng
    * @Date: 3/5/2025
    */
    public String commitAndPush(String recommend) throws Exception {
        logger.info("记录评审结果 -> {}", recommend);
        logger.info("clone git repo -> {}", githubReviewLogUri);
        Git git = Git.cloneRepository()
                .setURI(githubReviewLogUri)
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                .call();
        // 创建分支
        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File("repo/" + dateFolderName);
        if (!dateFolder.exists()) {
            dateFolder.mkdirs();
        }
        //配置评审文件日志名
        String fileName = project + "-" + branch + "-" + author  + System.currentTimeMillis() + ".md";
        File newFile = new File(dateFolder, fileName);
        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(recommend);
        }
        // 提交内容
        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
        git.commit().setMessage("add code review new file" + fileName).call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, "")).call();
        logger.info("openai-code-review git commit and push done! {}", fileName);
        return githubReviewLogUri + "/blob/master/" + dateFolderName + "/" + fileName;
    }

    public String getProject() {
        return project;
    }

    public String getBranch() {
        return branch;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }
}
