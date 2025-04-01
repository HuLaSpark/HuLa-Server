package com.hula.ai.common.api;

import java.io.Serializable;
import java.util.List;

/**
 * 整个应用通用的Command
 *
 * @author: 云裂痕
 * @date: 2020/12/8
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class CommonCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作人
     */
    private Long operater;

    /**
     * 操作人Id
     */
    private Long operaterId;

    /**
     * 是否需要操作人
     */
    private boolean needsOperator;

    /**
     * 操作ids
     */
    private List<Long> ids;

    /**
     * 操作id
     */
    private Long id;

    /**
     * 审核数据源id
     */
    private Long auditId;

    /**
     * 更新数据源id
     */
    private Long updateId;

    public Long getOperater() {
        return this.operater;
    }

    public void setOperater(Long operater) {
        this.operater = operater;
        needsOperator = true;
    }

    public void setOperaterId(Long operaterId) {
        this.operaterId = operaterId;
        needsOperator = true;
    }

    public Long getOperaterId() {
        return this.operaterId;
    }

    public boolean isNeedsOperator() {
        return needsOperator;
    }

    public void setNeedsOperator(boolean needsOperator) {
        this.needsOperator = needsOperator;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public Long getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Long updateId) {
        this.updateId = updateId;
    }

}
