<script lang="ts" setup>
import type { Recordable } from '@vben/types';

import { useRoute } from 'vue-router';
import { onMounted, ref, unref } from 'vue';

import { useFs } from '@fast-crud/fast-crud';
import { Button, Card } from 'ant-design-vue';

import { ${table.entityName}Config } from '#/api/${table.plusApplicationName}/${table.plusModuleName}/${table.entityName?uncap_first}';
import { getValidateRulesByFs } from '#/api/common/validateByFs';
import { ActionEnum } from '#/enums/commonEnum';
import { useMessage } from '#/hooks/web/useMessage';
import { $t } from '#/locales';
import { useTabs } from '@vben/hooks';
import { frontRules, createCrudOptions } from '../data/crud';

defineOptions({
  name: '${table.menuName}编辑',
  inheritAttrs: false
});
const { crudBinding, appendCrudOptions } = useFs({ createCrudOptions });

const type = ref<ActionEnum.ADD | ActionEnum.EDIT | ActionEnum.COPY | ActionEnum.VIEW>(ActionEnum.ADD);
const formRef = ref();
const buttonLoading = ref(false);
const { createMessage } = useMessage();
const { closeCurrentTab } = useTabs();

const route = useRoute();

async function formSubmit() {
  try {
    buttonLoading.value = true;
    await formRef.value.submit();

    createMessage.success($t(`common.tips.${r'${type.value}'}Success`));
    closeCurrentTab();
  } finally {
    buttonLoading.value = false;
  }
}

const load = async (data: Recordable) => {
  type.value = data?.type;

  if (![ActionEnum.ADD].includes(unref(type))) {
    const record = await get(data?.id);
    formRef.value.setFormData(record, { mergeForm: false });
  }

  if (unref(type) !== ActionEnum.VIEW) {
    const validateApi = unref(type) === ActionEnum.EDIT ? ${table.entityName}Config.Update : ${table.entityName}Config.Save;
    const formOptions = await getValidateRulesByFs({
      Api: validateApi,
      mode: unref(type),
      customRules: frontRules(crudExpose, unref(type)),
    });
    appendCrudOptions({ ...formOptions });
  }
};

onMounted(async () => {
  const { params } = route;
  load({ type: params?.type, id: params?.id });
});
</script>

<template>
  <Card :title="$t(`common.title.${r'${type}'}`)" v-loading="loading">
    <FsForm ref="formRef" v-bind="crudBinding[`${r'${type}'}Form`]" />

    <template #extra>
      <div v-if="type !== ActionEnum.VIEW">
        <Button :loading="buttonLoading" @click="formSubmit">{{ $t('common.submitText') }}</Button>
        <Button class="ml-2" @click="formReset">{{ $t('common.resetText') }}</Button>
      </div>
    </template>
  </Card>
</template>

<style scoped></style>
