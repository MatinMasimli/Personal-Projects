package cs735_project.operations;

import cs735_project.Operation;

public class LikeOperation extends Operation {
    private Long userId;
    private Long statusId;

    public LikeOperation(Long userId, Long statusId) {
        this.userId = userId;
        this.statusId = statusId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getStatusId() {
        return statusId;
    }
}
