{
  "table": { "title": "${table.swaggerComment!}" },
<#list allFields as field>
  "${field.javaField}": "${field.swaggerComment!}"<#if field_has_next>,</#if>
</#list>
}
