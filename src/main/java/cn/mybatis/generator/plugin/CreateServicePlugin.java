package cn.mybatis.generator.plugin;

import cn.mybatis.generator.constant.CommonConst;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.util.ArrayList;
import java.util.List;

public class CreateServicePlugin extends PluginAdapter {

	private MyCommentGenerator commentGenerator;

	@Override
	public void initialized(IntrospectedTable introspectedTable) {
		commentGenerator = (MyCommentGenerator) context.getCommentGenerator();
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		String domainName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
		String serviceName = domainName + "Service";
		String serviceImplName = domainName + "ServiceImpl";

		Interface service = new Interface(super.properties.getProperty(CommonConst.TARGET_PACKAGE) + "." + serviceName);
		service.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addClassComment(service, introspectedTable);

		TopLevelClass serviceImpl = new TopLevelClass(super.properties.getProperty(CommonConst.TARGET_PACKAGE) + ".impl." + serviceImplName);
		serviceImpl.setVisibility(JavaVisibility.PUBLIC);
		serviceImpl.addAnnotation(CommonConst.SERVICE_ANNOTATION_NAME);
		serviceImpl.addImportedType(service.getType());
		serviceImpl.addImportedType(new FullyQualifiedJavaType(CommonConst.SERVICE_FULL_TYPE_NAME));
		serviceImpl.addSuperInterface(service.getType());
		commentGenerator.addClassComment(serviceImpl, introspectedTable);


		MyGeneratedJavaFile myGeneratedJavaFileService = new MyGeneratedJavaFile(service, properties.getProperty(CommonConst.TARGET_PROJECT),
				properties.getProperty(CommonConst.TARGET_PACKAGE), CommonConst.UTF_8, context.getJavaFormatter());

		MyGeneratedJavaFile myGeneratedJavaFileServiceImpl = new MyGeneratedJavaFile(serviceImpl, properties.getProperty(CommonConst.TARGET_PROJECT),
				properties.getProperty(CommonConst.TARGET_PACKAGE) + ".impl", CommonConst.UTF_8, context.getJavaFormatter());
		
		List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();
		answer.add(myGeneratedJavaFileService);
		answer.add(myGeneratedJavaFileServiceImpl);

		return answer;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		return true;
	}

	@Override
	public boolean validate(List<String> arg0) {
		boolean valid = true;
		if (!StringUtility.stringHasValue(properties.getProperty("targetProject"))) {
			arg0.add(Messages.getString("ValidationError.18", "CreateDaoImplPlugin", "targetProject"));
			valid = false;
		}
		if (!StringUtility.stringHasValue(properties.getProperty("targetPackage"))) {
			arg0.add(Messages.getString("ValidationError.18", "CreateDaoImplPlugin", "targetPackage"));
			valid = false;
		}
		return valid;
	}
}
