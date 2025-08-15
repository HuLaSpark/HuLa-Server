import type {
  AddReq,
  DelReq,
  EditReq,
  InfoReq,
  UserPageRes,
} from '@fast-crud/fast-crud';

import type { AxiosRequestConfig } from '@vben/request';

import type { ${table.entityName}Model } from './model/${table.entityName?uncap_first}Model';
import type { PageParams, PageResult } from '#/api';

import { requestClient } from '#/api/request';
import { ServicePrefixEnum } from '#/enums/commonEnum';

const MODULAR = '${table.entityName?uncap_first}';

// tips: 建议在ServicePrefixEnum中新增：${table.serviceName?upper_case} = '/${table.serviceName}'，并将下方代码改为： const ServicePrefix = ServicePrefixEnum.${table.serviceName?upper_case};
// tips: /${table.serviceName} 需要与 ${projectPrefix}-gateway-server.yml中配置的Path一致，否则无法正常调用接口！！！
// const ServicePrefix = ServicePrefixEnum.${table.serviceName?upper_case};
const ServicePrefix = '/${table.serviceName}';

export const ${table.entityName}Config = {
  Save: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}`,
    method: 'POST',
  } as AxiosRequestConfig,
  Update: {
    url: `${r"${ServicePrefix}"}/${r"${MODULAR}"}`,
    method: 'PUT',
  } as AxiosRequestConfig,
};

export namespace ${table.entityName}Api {
  /**
   * 根据条件查询分页数据
   */
  export async function page(
    params: PageParams<${table.entityName}Model.${table.entityName}PageQuery>,
  ) {
    return requestClient.post<PageResult<${table.entityName}Model.${resultVoName}>>(
      `${r"${ServicePrefix}"}/${r"${MODULAR}"}/page`,
      params,
    );
  }
<#if table.tplType == TPL_TREE>
  /**
   * 获取树结构数据
   */
  export async function tree(params: ${table.entityName}Model.${table.entityName}PageQuery) {
    return requestClient.post<${table.entityName}Model.${resultVoName}[]>(
      `${r"${ServicePrefix}"}/${r"${MODULAR}"}/tree`,
      params,
    );
  }
</#if>
  /**
   * 根据id查询单条数据
   */
  export async function get(id: string) {
    return requestClient.get<${table.entityName}Model.${resultVoName}>(
      `${r"${ServicePrefix}"}/${r"${MODULAR}"}/${r"${id}"}`,
    );
  }

  /**
   * 保存
   */
  export async function save(params: ${table.entityName}Model.${saveVoName}) {
    return requestClient.post<${table.entityName}Model.${resultVoName}>(
      ${table.entityName}Config.Save.url as string,
      params,
    );
  }

  /**
   * 修改
   */
  export async function update(params: ${table.entityName}Model.${updateVoName}) {
    return requestClient.put<${table.entityName}Model.${resultVoName}>(
      ${table.entityName}Config.Update.url as string,
      params,
    );
  }

  /**
   * 删除
   */
  export async function remove(data: string[]) {
    return requestClient.delete<boolean>(
      `${r"${ServicePrefix}"}/${r"${MODULAR}"}`,
      { data },
    );
  }

  // fast-crud 需要的接口
  export const pageRequest = async (
    pageQuery: PageParams<${table.entityName}Model.${table.entityName}PageQuery>,
  ): Promise<UserPageRes> => {
    return await page(pageQuery);
  };

  export const infoRequest = async (ctx: InfoReq): Promise<UserPageRes> => {
    const { row } = ctx;
    // 请求后台查询数据
    return row;
  };

  export const addRequest = async (req: AddReq) => {
    const { form } = req;
    return await save(form);
  };

  export const editRequest = async (ctx: EditReq) => {
    const { form } = ctx;
    return await update(form);
  };

  export const delRequest = async (ctx: DelReq) => {
    const { row } = ctx;
    return await remove([row.id]);
  };
}
