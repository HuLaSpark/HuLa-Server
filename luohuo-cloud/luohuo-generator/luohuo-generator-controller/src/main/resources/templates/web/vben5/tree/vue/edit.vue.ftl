<script setup lang="ts">
import type { Recordable } from '@vben/types';

import { ref, unref } from 'vue';

import { useFs } from '@fast-crud/fast-crud';
import { Button, Card } from 'ant-design-vue';

import { ${table.entityName}Config } from '#/api/${table.plusApplicationName}/${table.plusModuleName}/${table.entityName?uncap_first}';
import { getValidateRulesByFs } from '#/api/common/validateByFs';
import { ActionEnum } from '#/enums/commonEnum';
import { useMessage } from '#/hooks/web/useMessage';
import { $t } from '#/locales';
import { frontRules, createCrudOptions } from '../data/crud';

defineOptions({
  name: '${table.entityName}编辑',
  inheritAttrs: false
});

interface Emits {
  (e: 'success'): void;
}

const emit = defineEmits<Emits>();

const formRef = ref();
const loading = ref(false);
const buttonLoading = ref(false);
const { createMessage } = useMessage();
const type = ref<ActionEnum.ADD | ActionEnum.EDIT | ActionEnum.COPY | ActionEnum.VIEW>(ActionEnum.ADD);
const { crudExpose, crudBinding, appendCrudOptions } = useFs({ createCrudOptions });

async function formSubmit() {
  try {
    buttonLoading.value = true;
    await formRef.value.submit();

    createMessage.success($t(`common.tips.${r'${type.value}'}Success`));
    type.value = ActionEnum.VIEW;
    formRef.value.reset();
    emit('success');
  } finally {
    buttonLoading.value = false;
  }
}

function formReset() {
  formRef.value.reset();
}

async function setData(data: Recordable) {
  try {
    type.value = data?.type;
    loading.value = true;

    const { record = {}, parent = {} } = data;
    record.parentName = parent?.name;
    record.parentId = parent?.id;

    formRef.value.setFormData(record, { mergeForm: false });

    if (unref(type) !== ActionEnum.VIEW) {
      const validateApi = unref(type) === ActionEnum.EDIT ? ${table.entityName}Config.Update : ${table.entityName}Config.Save;
      const formOptions = await getValidateRulesByFs({
        Api: validateApi,
        mode: unref(type),
        customRules: frontRules(crudExpose, unref(type)),
      });
      appendCrudOptions({ ...formOptions });
    }
  } finally {
    loading.value = false;
  }
}

defineExpose({ setData });
</script>

<template>
  <Card :title="$t(`common.title.${r'${type}'}`)" v-loading="loading" class="h-full">
    <FsForm ref="formRef" v-bind="crudBinding[`${r'${type}'}Form`]" />

    <template #extra>
      <div v-if="type !== ActionEnum.VIEW">
        <Button :loading="buttonLoading" @click="formSubmit" type="primary">{{ $t('common.saveText') }}</Button>
        <Button class="ml-2" @click="formReset">{{ $t('common.resetText') }}</Button>
      </div>
    </template>
  </Card>
</template>

<style lang="scss" scoped></style>
