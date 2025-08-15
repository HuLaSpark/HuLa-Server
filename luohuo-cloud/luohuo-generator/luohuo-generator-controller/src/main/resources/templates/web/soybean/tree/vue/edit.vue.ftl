<script setup lang="ts">
import { useFs } from '@fast-crud/fast-crud';
import { ref, unref } from 'vue';
import { Api } from '@/service/fetch/${table.plusApplicationName}/${table.plusModuleName}/${table.entityName?uncap_first}';
import type { FormRulesExt } from '@/service/fetch';
import { getValidateRulesByFs } from '@/service/fetch';
import { ActionEnum } from '@/enum/common';
import { $t } from '@/locales';
import { createCrudOptions } from '../data/crud';

defineOptions({
  name: '${table.entityName}Edit',
  inheritAttrs: false
});

interface Emits {
  (e: 'success'): void;
}

const emit = defineEmits<Emits>();

const formRef = ref();
const loading = ref(false);
const buttonLoading = ref(false);
const type = ref<ActionEnum.ADD | ActionEnum.EDIT | ActionEnum.COPY | ActionEnum.VIEW>(ActionEnum.ADD);
const { crudBinding, appendCrudOptions } = useFs({ createCrudOptions });

async function formSubmit() {
  buttonLoading.value = true;
  try {
    await formRef.value.submit();

    window.$message?.success($t(`common.tips.${r'${'}type.value}Success`));
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
      const customRules: FormRulesExt = {};
      const validateApi = unref(type) === ActionEnum.EDIT ? Api.Update : Api.Save;
      const formOptions = await getValidateRulesByFs({
        Api: validateApi,
        mode: unref(type),
        customRules,
        trigger: 'change'
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
  <NCard :title="$t(`common.${r'${'}type}`)">
    <FsForm ref="formRef" v-bind="crudBinding[`${r'${'}type}Form`]" />

    <template #action>
      <div v-if="type !== ActionEnum.VIEW">
        <NButton :loading="buttonLoading" @click="formSubmit">{{ $t('common.submitText') }}</NButton>
        <NButton class="ml-2" @click="formReset">{{ $t('common.resetText') }}</NButton>
      </div>
    </template>
  </NCard>
</template>

<style scoped></style>
