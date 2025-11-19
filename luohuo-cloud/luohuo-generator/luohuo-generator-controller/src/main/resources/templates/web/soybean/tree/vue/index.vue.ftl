<script setup lang="ts">
import { ref, unref } from 'vue';
import { ActionEnum } from '@/enum/common';
import Edit from './modules/Edit.vue';
import Tree from './modules/Tree.vue';
const collapsedRef = ref<boolean>(true);
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
function handleTreeAdd(parent = {}) {
  getEditRef().setData({ type: ActionEnum.ADD, parent });
}

function handleEditSuccess() {
  getTreeRef().fetch();
}
</script>

<template>
  <NLayout has-sider>
    <NLayoutSider
      collapse-mode="width"
      :native-scrollbar="false"
      :collapsed-width="500"
      :collapsed="collapsedRef"
      :on-update:collapsed="
        (collapsed: boolean) => {
          collapsedRef = collapsed;
        }
      "
      width="50%"
      show-trigger="arrow-circle"
      content-style="padding: 10px;"
      bordered
    >
      <Tree ref="treeRef" @select="handleTreeSelect" @add="handleTreeAdd" @edit="handleTreeEdit" />
    </NLayoutSider>
    <NLayoutContent>
      <Edit ref="editRef" @success="handleEditSuccess" />
    </NLayoutContent>
  </NLayout>
</template>

<style scoped></style>
