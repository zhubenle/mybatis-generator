package cn.mybatis.generator.plugin;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.CompilationUnit;

/**
 * Created on
 * @author zhubenle
 */
public class MyGeneratedJavaFile extends GeneratedJavaFile {

	private String targetPackage;
	
	public MyGeneratedJavaFile(CompilationUnit compilationUnit, String targetProject, String targetPackage, String fileEncoding, JavaFormatter javaFormatter) {
		super(compilationUnit, targetProject, fileEncoding, javaFormatter);
		this.targetPackage = targetPackage;
	}
	
	public MyGeneratedJavaFile(CompilationUnit compilationUnit, String targetProject, String targetPackage, JavaFormatter javaFormatter) {
		super(compilationUnit, targetProject, null, javaFormatter);
		this.targetPackage = targetPackage;
	}

	@Override
	public String getTargetPackage() {
		return targetPackage;
	}
}
