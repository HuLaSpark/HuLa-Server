package com.luohuo.flex.model.vo.save;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.basic.utils.ArgumentAssert;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 实体类
 * 业务附件
 * </p>
 *
 * @author tangyh
 * @date 2021-06-30
 * @create [2021-06-30] [tangyh] [初始创建]
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@Schema(description = "业务附件")
public class AppendixSaveVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务id
     */
    @Schema(description = "业务id")
    private Long bizId;
    @Schema(description = "类型和附件id")
    private List<TypeFile> typeFiles;

    @Getter
    @Setter
    @ToString
    public static class TypeFile {
        @Schema(description = "业务类型")
        private String bizType;
        @Schema(description = "多个附件id")
        private List<Long> fileIdList;

        public static TypeFile build(String bizType, Long fileId) {
            if (StrUtil.isEmpty(bizType) || fileId == null) {
                return null;
            }
            return new TypeFile().setBizType(bizType).setFileIdList(Collections.singletonList(fileId));
        }

        public static TypeFile build(String bizType, List<Long> fileIds) {
            if (StrUtil.isEmpty(bizType) || CollUtil.isEmpty(fileIds)) {
                return null;
            }
            return new TypeFile().setBizType(bizType).setFileIdList(fileIds);
        }

    }


    public static AppendixSaveVO buildDelete(Long bizId) {
        ArgumentAssert.notNull(bizId, "请填写业务id");
        return new AppendixSaveVO().setBizId(bizId);
    }

    public AppendixSaveVO setTypeFiles(List<TypeFile> typeFiles) {
        this.typeFiles = typeFiles == null ? Collections.emptyList() : typeFiles;
        return this;
    }

    public AppendixSaveVO setTypeFiles(TypeFile typeFile) {
        if (typeFile != null) {
            this.setTypeFiles(Collections.singletonList(typeFile));
        }
        return this;
    }

    public AppendixSaveVO setTypeFiles(TypeFile... typeFiles) {
        if (typeFiles.length > 0) {
            this.setTypeFiles(Arrays.stream(typeFiles).toList());
        }
        return this;
    }

    public static AppendixSaveVO build(Long bizId, String bizType, Long fileId) {
        ArgumentAssert.notNull(bizId, "请填写业务id");
        ArgumentAssert.notEmpty(bizType, "请填写业务类型");
        return new AppendixSaveVO().setBizId(bizId).setTypeFiles(TypeFile.build(bizType, fileId));
    }

    public static AppendixSaveVO build(Long bizId, String bizType, List<Long> fileIds) {
        ArgumentAssert.notNull(bizId, "请填写业务id");
        ArgumentAssert.notEmpty(bizType, "请填写业务类型");
        return new AppendixSaveVO().setBizId(bizId).setTypeFiles(TypeFile.build(bizType, fileIds));
    }

    public static AppendixSaveVO build(Long bizId, TypeFile... typeFiles) {
        ArgumentAssert.notNull(bizId, "请填写业务id");
        return new AppendixSaveVO().setBizId(bizId).setTypeFiles(typeFiles);
    }

    public static AppendixSaveVO build(Long bizId, List<TypeFile> typeFiles) {
        ArgumentAssert.notNull(bizId, "请填写业务id");
        return new AppendixSaveVO().setBizId(bizId).setTypeFiles(typeFiles);
    }

}
