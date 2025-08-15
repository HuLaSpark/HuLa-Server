<script lang="ts" setup>
import { onMounted } from 'vue';

import { useFs } from '@fast-crud/fast-crud';

import { ${table.entityName}Config } from '#/api/${table.plusApplicationName}/${table.plusModuleName}/${table.entityName?uncap_first}';
import { getValidateRulesByFs } from '#/api/common/validateByFs';
import { ActionEnum } from '#/enums/commonEnum';

import { createCrudOptions, frontRules } from './data/crud';

defineOptions({
  name: '${table.menuName}',
  inheritAttrs: false,
});

const { crudRef, crudBinding, crudExpose, <#if table.addShow || table.copyShow || table.editShow>appendCrudOptions</#if> } = useFs({
  createCrudOptions,
});

// 页面打开后获取列表数据
onMounted(async () => {
  <#if table.addShow || table.copyShow>
  const addFormOptions = await getValidateRulesByFs({
    Api: ${table.entityName}Config.Save,
    mode: ActionEnum.ADD,
    customRules: frontRules(crudExpose, ActionEnum.ADD),
    trigger: ['blur', 'change'],
  });
  </#if>
  <#if table.editShow>
  const editFormOptions = await getValidateRulesByFs({
    Api: ${table.entityName}Config.Update,
    mode: ActionEnum.EDIT,
    customRules: frontRules(crudExpose, ActionEnum.EDIT),
    trigger: ['blur', 'change'],
  });
  </#if>
  <#if table.addShow || table.copyShow || table.editShow>
  appendCrudOptions({ <#if table.addShow || table.copyShow>...addFormOptions, </#if><#if table.editShow>...editFormOptions</#if> });
  </#if>
  crudExpose.doRefresh();
});
</script>

<template>
  <FsPage>
    <FsCrud ref="crudRef" v-bind="crudBinding" />
  </FsPage>
</template>
<style lang="scss" scoped></style>