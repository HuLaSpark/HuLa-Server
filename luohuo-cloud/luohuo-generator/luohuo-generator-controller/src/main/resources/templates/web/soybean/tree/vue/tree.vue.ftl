<script setup lang="ts">
import { h, onMounted, ref, unref } from 'vue';
import type { DropdownOption } from 'naive-ui/es/dropdown';
import type { Key, TreeOption } from 'naive-ui/es/tree/src/interface';
import { NButton } from 'naive-ui';
import type { TreeActionItem, TreeActionType, TreeItem } from '@/components/${projectPrefix}/tree';
import { findNodeByKey } from '@/utils/helper/treeHelper';
import { BasicTree } from '@/components/${projectPrefix}/tree';
import { $t } from '@/locales';
import SvgIcon from '@/components/custom/svg-icon.vue';
import { usePermission } from '@/hooks/web/usePermission';
import { useMessage } from '@/hooks/web/useMessage';
import { <#if table.deleteShow>remove, </#if>tree } from '@/service/fetch/${table.plusApplicationName}/${table.plusModuleName}/${table.entityName?uncap_first}';

defineOptions({
  name: '${table.entityName}Tree',
  inheritAttrs: false
});

interface Emits {
  (e: 'select', parent: TreeItem, node: TreeItem): void;
  (e: 'add', node: TreeItem): void;
  (e: 'edit', parent: TreeItem, current: TreeItem): void;
}

const emit = defineEmits<Emits>();
const treeRef = ref<Nullable<TreeActionType>>(null);
const treeData = ref<TreeItem[]>([]);
const { isPermission } = usePermission();
const { warningDialog, successMessage, warningMessage } = useMessage();

function getTree() {
  const treeR = unref(treeRef);
  if (!treeR) {
    throw new Error('树结构加载失败,请刷新页面');
  }
  return treeR;
}

onMounted(() => {
  fetch();
});

// 加载数据
async function fetch() {
  treeData.value = (await tree()) as unknown as TreeItem[];
  setTimeout(() => {
    getTree().filterByLevel(2);
  }, 0);
}

// 选择节点
function handleSelect(keys: Key[]) {
  if (keys[0]) {
    const node = findNodeByKey(keys[0], treeData.value);
    const parent = findNodeByKey(node?.parentId, treeData.value);

    emit('select', parent, node);
  }
}

// 悬停图标
const actionList: TreeActionItem[] = [
  <#if table.addShow>
  {
    <#if table.addAuth?? && table.addAuth != ''>
    show: isPermission('${table.addAuth}'),
    </#if>
    render: node => {
      return h(
        NButton,
        {
          text: true,
          onClick: (e: Event) => {
            e?.stopPropagation();
            e?.preventDefault();
            emit('add', findNodeByKey(node.id as Key, treeData.value));
          }
        },
        {
          default: () => $t('common.add')
        }
      );
    }
  },
  </#if>
  <#if table.editShow>
  {
    <#if table.editAuth?? && table.editAuth != ''>
    show: isPermission('${table.editAuth}'),
    </#if>
    render: node => {
      return h(
        NButton,
        {
          text: true,
          onClick: (e: Event) => {
            e?.stopPropagation();
            e?.preventDefault();
            const current = findNodeByKey(node?.id as Key, treeData.value);
            const parent = findNodeByKey(node?.parentId as Key, treeData.value);
            emit('edit', parent, current);
          }
        },
        {
          default: () => $t('common.edit')
        }
      );
    }
  },
  </#if>
  <#if table.deleteShow>
  {
    <#if table.deleteAuth?? && table.deleteAuth != ''>
    show: isPermission('${table.deleteAuth}'),
    </#if>
    render: node => {
      return h(
        NButton,
        {
          text: true,
          onClick: (e: Event) => {
            e?.stopPropagation();
            e?.preventDefault();
            batchDelete([node.id as string]);
          }
        },
        {
          default: () => $t('common.delete')
        }
      );
    }
  }
  </#if>
];

// 右键菜单
const getRightMenuList = (node: TreeOption): DropdownOption[] => {
  return [
    <#if table.addShow>
    {
      label: $t('common.addChildren'),
      <#if table.addAuth?? && table.addAuth != ''>
      show: isPermission('${table.addAuth}'),
      </#if>
      props: {
        onClick: () => {
          emit('add', findNodeByKey(node.id as Key, treeData.value));
        }
      },
      icon: () => {
        return h(SvgIcon, { icon: 'ant-design:plus-outlined' });
      }
    },
    </#if>
    <#if table.editShow>
    {
      label: $t('common.edit'),
      <#if table.editAuth?? && table.editAuth != ''>
      show: isPermission('${table.editAuth}'),
      </#if>
      props: {
        onClick: () => {
          const current = findNodeByKey(node?.id as Key, treeData.value);
          const parent = findNodeByKey(node?.parentId as Key, treeData.value);
          emit('edit', parent, current);
        }
      },
      icon: () => {
        return h(SvgIcon, { icon: 'ant-design:edit-outlined' });
      }
    },
    </#if>
    <#if table.deleteShow>
    {
      label: $t('common.delete'),
      <#if table.deleteAuth?? && table.deleteAuth != ''>
      show: isPermission('${table.deleteAuth}'),
      </#if>
      props: {
        onClick: () => {
          batchDelete([node?.id as string]);
        }
      },
      icon: () => {
        return h(SvgIcon, { icon: 'ant-design:delete-outlined' });
      }
    }
    </#if>
  ];
};

<#if table.addShow || table.copyShow>
// 点击树外面的 新增
function handleAdd() {
  emit('add', findNodeByKey('0', treeData.value));
}
</#if>

<#if table.deleteShow>
// 执行批量删除
async function batchDelete(ids: string[]) {
  warningDialog({
    content: '选中节点及其子结点将被永久删除, 是否确定删除？',
    onPositiveClick: async () => {
      try {
        await remove(ids);
        successMessage($t('common.tips.deleteSuccess'));
        fetch();
      } catch (e) {}
    }
  });
}

// 点击树外面的 批量删除
function handleBatchDelete() {
  const checked = getTree().getCheckedKeys();
  if (!checked || checked.length <= 0) {
    warningMessage($t('common.tips.pleaseSelectTheData'));
    return;
  }
  batchDelete(checked as string[]);
}
</#if>

defineExpose({ fetch });
</script>

<template>
  <div class="m-4 mr-2 overflow-hidden">
    <div class="m-4">
      <#if table.addShow || table.copyShow>
      <NButton
        <#if table.addAuth?? && table.addAuth != ''>
        v-hasAnyPermission="['${table.addAuth}']"
        </#if>
        class="mr-2"
        pre-icon="ant-design:plus-outlined"
        @click="handleAdd()"
      >
        {{ $t('common.addRoot') }}
      </NButton>
      </#if>
      <#if table.deleteShow>
      <NButton
        <#if table.deleteAuth?? && table.deleteAuth != ''>
        v-hasAnyPermission="['${table.deleteAuth}']"
        </#if>
        class="mr-2"
        pre-icon="ant-design:delete-outlined"
        @click="handleBatchDelete()"
      >
        {{ $t('common.delete') }}
      </NButton>
      </#if>
    </div>
    <BasicTree
      ref="treeRef"
      key-field="id"
      <#if table.treeName?? && table.treeName != ''>
      label-field="${table.treeName}"
      </#if>
      :action-list="actionList"
      :before-right-click="getRightMenuList"
      checkable
      :title="$t('${table.plusApplicationName}.${table.plusModuleName}.${table.entityName?uncap_first}.table.title')"
      :data="treeData"
      search
      toolbar
      block-node
      @on-update:selected-keys="handleSelect"
    >
    </BasicTree>
  </div>
</template>

<style scoped></style>
