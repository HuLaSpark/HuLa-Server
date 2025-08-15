{
  "table": { "title": "${table.entityName!}" },
<#list allFields as field>
  "${field.javaField}": "${field.javaField?cap_first}"<#if field_has_next>,</#if>
</#list>
}
