package cs735_project.operations;

import cs735_project.Operation;

public class PublishStatusOperation extends Operation {
    private String content;

    public PublishStatusOperation(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
