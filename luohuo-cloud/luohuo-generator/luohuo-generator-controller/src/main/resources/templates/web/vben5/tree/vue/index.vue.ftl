<script setup lang="ts">
import { reactive, ref, unref } from 'vue';

import { ColPage } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';

import { Button, Tooltip } from 'ant-design-vue';

import { ActionEnum } from '#/enums/commonEnum';

import Edit from './modules/edit.vue';
import Tree from './modules/tree.vue';

const colPageProps = reactive({
  leftCollapsedWidth: 5,
  // 左侧最大宽度百分比
  leftMaxWidth: 50,
  // 左侧最小宽度百分比
  leftMinWidth: 10,
  leftWidth: 30,
  rightWidth: 70,
  // 左侧可折叠
  leftCollapsible: true,
  // 显示拖动手柄
  splitHandle: true,
  // 可拖动调整宽度
  resizable: true,
  // 显示拖动分隔线
  splitLine: true,
});

const editRef = ref<any>(null);
const treeRef = ref<any>(null);

function getEditRef() {
  return unref(editRef);
}
function getTreeRef() {
  return unref(treeRef);
}

// 选中树的节点
function handleTreeSelect(parent = {}, record = {}) {
  getEditRef().setData({ type: ActionEnum.VIEW, parent, record });
}

// 编辑
function handleTreeEdit(parent = {}, record = {}) {
  getEditRef().setData({ type: ActionEnum.EDIT, parent, record });
}

// 点击树的新增按钮
function handleTreeAdd(parent = {}, record = {}) {
  getEditRef().setData({
    type: ActionEnum.ADD,
    parent,
    record: {
      ...record,
    },
  });
}

function handleEditSuccess() {
  getTreeRef().fetch();
}
</script>

<template>
  <ColPage v-bind="colPageProps">
    <template #left="{ isCollapsed, expand }">
      <div v-if="isCollapsed" @click="expand">
        <Tooltip title="点击展开左侧">
          <Button shape="circle" type="primary">
            <template #icon>
              <IconifyIcon class="text-2xl" icon="bi:arrow-right" />
            </template>
          </Button>
        </Tooltip>
      </div>
      <div
        v-else
        class="border-border bg-card mr-2 h-full rounded-[var(--radius)] border p-2"
      >
        <Tree
          ref="treeRef"
          @select="handleTreeSelect"
          @add="handleTreeAdd"
          @edit="handleTreeEdit"
        />
      </div>
    </template>
    <Edit ref="editRef" @success="handleEditSuccess" />
  </ColPage>
</template>

<style lang="scss" scoped></style>
