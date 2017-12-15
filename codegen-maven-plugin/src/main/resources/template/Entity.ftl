/*
 * ${className?cap_first}.java Copyright(C) 2017 一般財団法人建設業振興基金 All rights reserved.
 * システム名(CCUSシステム) 業務区分名(データ連携API)
 */
package jp.co.fujifilm.ccs.api.common.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ${classComment}。<br>
 * 
 * @version 1.0 2017/09/26
 * @author NOAH)何
 */
public class ${className?cap_first} implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * コンストラクタ。<br>
	 */
	public ${className?cap_first}() {
		<#list fields as field>
			
			<#if field.javaType == "String">
				${field.javaName} = "";
			<#else>
				${field.javaName} = new ${field.javaType?cap_first}();
			</#if>
			
		</#list>
	}
<#list fields as field>
	/**
	 * ${field.comment}
	 */
	private ${field.javaType?cap_first} ${field.javaName};
</#list>

<#list fields as field>
	/**
	 * ${field.comment}を取得する。</br>
	 * @return ${field.comment}
	 */
	public ${field.javaType?cap_first} get${field.javaName?cap_first}() {
		return ${field.javaName};
	}

	/**
	 * ${field.comment}を設定する。</br>
	 * @param ${field.javaName} ${field.comment}
	 */
	public void set${field.javaName?cap_first}(${field.javaType?cap_first} ${field.javaName}) {
		this.${field.javaName} = ${field.javaName};
	}

</#list>
}
