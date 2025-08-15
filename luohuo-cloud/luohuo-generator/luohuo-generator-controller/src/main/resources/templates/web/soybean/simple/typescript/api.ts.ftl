import { RequestEnum } from '@vben/http';
import type { AxiosRequestConfig } from 'axios';

import type { AddReq, DelReq, EditReq, InfoReq, UserPageRes } from '@fast-crud/fast-crud';
import { defHttp } from '@/service/http';
import { ServicePrefixEnum } from '@/enum/common';
import type { ${pageQueryName}, ${resultVoName}, ${saveVoName}, ${updateVoName} } from './model/${table.entityName?uncap_first}Model';

<#if table.superClass == SUPER_CLASS_SUPER_POI_CLASS>
import type { UploadFileParams } from '@vben/http';
</#if>

const MODULAR = '${table.entityName?uncap_first}';
// tips: 建议在ServicePrefixEnum中新增：${table.serviceName?upper_case} = '/${table.serviceName}'，并将下方代码改为： const ServicePrefix = ServicePrefixEnum.${table.serviceName?upper_case};
// tips: /${table.serviceName} 需要与 ${projectPrefix}-gateway-server.yml中配置的Path一致，否则无法正常调用接口！！！
// const ServicePrefix = ServicePrefixEnum.${table.serviceName?upper_case};
const ServicePrefix = '/${table.serviceName}';

export const Api = {
  Page: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/page`,
    method: RequestEnum.POST,
  } as AxiosRequestConfig,
  Detail: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/detail`,
    method: RequestEnum.GET,
  } as AxiosRequestConfig,
  Copy: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/copy`,
    method: RequestEnum.POST,
  } as AxiosRequestConfig,
  Get: (id: string) => {
    return {
      url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/${r"${id}"}`,
      method: RequestEnum.GET,
    } as AxiosRequestConfig;
  },
<#if table.tplType == TPL_TREE>
  Tree: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/tree`,
    method: RequestEnum.POST,
  } as AxiosRequestConfig,
</#if>
<#if table.addShow || table.copyShow>
  Save: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}`,
    method: RequestEnum.POST,
  } as AxiosRequestConfig,
</#if>
<#if table.editShow>
  Update: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}`,
    method: RequestEnum.PUT,
  },
</#if>
<#if table.deleteShow>
  Delete: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}`,
    method: RequestEnum.DELETE,
  } as AxiosRequestConfig,
</#if>
  Query: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/query`,
    method: RequestEnum.POST,
  } as AxiosRequestConfig,
  FindByIds: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/findByIds`,
    method: RequestEnum.POST,
  } as AxiosRequestConfig,
<#if table.superClass == SUPER_CLASS_SUPER_POI_CLASS>
  Preview: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/preview`,
    method: RequestEnum.POST,
  } as AxiosRequestConfig,
  Export: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/export`,
    method: RequestEnum.POST,
    responseType: 'blob',
  } as AxiosRequestConfig,
  Import: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}/import`,
    method: RequestEnum.POST,
  } as AxiosRequestConfig,
</#if>
};

<#if table.tplType == TPL_TREE>
export const tree = (params?: ${pageQueryName}) => defHttp.request<${resultVoName}[]>({ ...Api.Tree, params });

</#if>
export const copy = (id: string) => defHttp.request<${resultVoName}>({ ...Api.Copy, params: { id } });

export const page = (params: Fetch.PageParams<${pageQueryName}>) =>
  defHttp.request<Fetch.PageResult<${resultVoName}>>({ ...Api.Page, params });

export const detail = (id: string) =>
  defHttp.request<${resultVoName}>({ ...Api.Detail, params: { id } });

export const query = (params: ${pageQueryName}) => defHttp.request<${resultVoName}[]>({ ...Api.Query, params });

export const findByIds = (ids: string[]) => defHttp.request<${resultVoName}[]>({ ...Api.FindByIds, params: ids });

export const get = (id: string) => defHttp.request<${resultVoName}>({ ...Api.Get(id) });

<#if table.addShow || table.copyShow>
export const save = (params: ${saveVoName}) => defHttp.request<${resultVoName}>({ ...Api.Save, params });

</#if>
<#if table.editShow>
export const update = (params: ${updateVoName}) =>
  defHttp.request<${resultVoName}>({ ...Api.Update, params });

</#if>
<#if table.deleteShow>
export const remove = (params: string[]) => defHttp.request<boolean>({ ...Api.Delete, params });

</#if>
<#if table.superClass == SUPER_CLASS_SUPER_POI_CLASS>

export const exportPreview = (params: Fetch.PageParams<${pageQueryName}>) =>
  defHttp.request<string>({ ...Api.Preview, params });

export const exportFile = (params: Fetch.PageParams<${pageQueryName}>) =>
  defHttp.request<any>({ ...Api.Export, params }, { isReturnNativeResponse: true });

export const importFile = (params: UploadFileParams) =>
  defHttp.uploadFile<boolean>({ ...Api.Import }, params);
</#if>


export const pageRequest = async (pageQuery: Fetch.PageParams<${pageQueryName}>): Promise<UserPageRes> => {
  return await page(pageQuery);
};

export const infoRequest = async (ctx: InfoReq): Promise<UserPageRes> => {
  const { row } = ctx;

  // 请求后台查询数据
  return row;
};

<#if table.addShow || table.copyShow>
export const addRequest = async (req: AddReq) => {
  const { form } = req;
  return await save(form);
};
</#if>

<#if table.editShow>
export const editRequest = async (ctx: EditReq) => {
  const { form } = ctx;
  return await update(form);
};
</#if>

<#if table.deleteShow>
export const delRequest = async (ctx: DelReq) => {
  const { row } = ctx;
  return await remove([row.id]);
};

export const removeFn = async (ids: string[]) => {
  return await remove(ids);
};
</#if>


