<script lang="ts" setup>
import { useRoute } from 'vue-router';
import { onMounted, ref, unref } from 'vue';
import { useFs } from '@fast-crud/fast-crud';
import { ActionEnum } from '@/enum/common';
import type { FormRulesExt } from '@/service/fetch';
import { getValidateRulesByFs } from '@/service/fetch';
import { Api, get } from '@/service/fetch/${table.plusApplicationName}/${table.plusModuleName}/${table.entityName?uncap_first}';
import { $t } from '@/locales';
import { useTabStore } from '@/store/modules/tab';
import { createCrudOptions } from '../data/crud';

defineOptions({
  name: '${table.menuName}编辑',
  inheritAttrs: false
});
const { crudBinding, appendCrudOptions } = useFs({ createCrudOptions });

const type = ref<ActionEnum.ADD | ActionEnum.EDIT | ActionEnum.COPY | ActionEnum.VIEW>(ActionEnum.ADD);
const formRef = ref();
const buttonLoading = ref(false);
const tabStore = useTabStore();

const route = useRoute();

async function formSubmit() {
  buttonLoading.value = true;
  try {
    await formRef.value.submit();

    window.$message?.success($t(`common.tips.${r'${'}type.value}Success`));
    tabStore.removeActiveTab();
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
};

onMounted(async () => {
  const { params } = route;
  load({ type: params?.type, id: params?.id });
});
</script>

<template>
  <FsPage>
    <template #header>
      <div class="title">{{ $t(`common.${r'${type}'}`) }}</div>
    </template>
    <div class="p-5">
      <FsForm ref="formRef" v-bind="crudBinding[`${r'${type}'}Form`]" />
      <div style="margin-top: 10px">
        <NButton v-if="formRef" :loading="buttonLoading" @click="formSubmit">保存</NButton>
      </div>
    </div>
  </FsPage>
</template>
