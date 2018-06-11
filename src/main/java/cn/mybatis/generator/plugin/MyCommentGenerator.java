package cn.mybatis.generator.plugin;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * <br/>
 * Created on 2017/11/6 16:54.
 *
 * @author zhubenle
 */
public class MyCommentGenerator implements CommentGenerator {

    private boolean suppressAllComments;
    private boolean suppressFileComments;
    private SimpleDateFormat simpleDateFormat;

    public MyCommentGenerator() {
        super();
        suppressAllComments = false;
        suppressFileComments = true;
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm.");
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {

    }

    @Override
    public void addComment(XmlElement xmlElement) {

    }

    @Override
    public void addRootComment(XmlElement rootElement) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        field.addJavaDocLine("/**");
        field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
        field.addJavaDocLine(" */");
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        method.addJavaDocLine(" * " + method.getName());
        method.addJavaDocLine(" * ");
        List<Parameter> parameters = method.getParameters();
        if (parameters != null) {
            parameters.forEach(parameter -> {
                method.addJavaDocLine(" * @param " + parameter.getName());
                method.addJavaDocLine(" *         {@link " + parameter.getType().getShortName() + " } ");
            });
        }
        method.addJavaDocLine(" * ");
        FullyQualifiedJavaType returnType = method.getReturnType();
        if (returnType != null) {
            method.addJavaDocLine(" * @return {@link " + returnType.getShortName() + " } ");
        }
        method.addJavaDocLine(" */");
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        if (suppressFileComments) {
            return;
        }
        compilationUnit.addFileCommentLine("/**");
        compilationUnit.addFileCommentLine(" * ");
        compilationUnit.addFileCommentLine(" */");
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addJavaDocLine("/**");
        String remarks = introspectedTable.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            topLevelClass.addJavaDocLine(" * 实体对象对应数据表注释: <br/>");
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" * " + remarkLine);
            }
        }
        topLevelClass.addJavaDocLine(" * Created on " + simpleDateFormat.format(new Date()));
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * @author zhubenle");
        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        innerClass.addJavaDocLine("/**");
        innerClass.addJavaDocLine(" * <br/>");
        innerClass.addJavaDocLine(" * Created on " + simpleDateFormat.format(new Date()));
        innerClass.addJavaDocLine(" * ");
        innerClass.addJavaDocLine(" * @author zhubenle");
        innerClass.addJavaDocLine(" */");
    }


    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        innerClass.addJavaDocLine("/**");
        innerClass.addJavaDocLine(" * <br/>");
        innerClass.addJavaDocLine(" * Created on " + simpleDateFormat.format(new Date()));
        innerClass.addJavaDocLine(" * ");
        innerClass.addJavaDocLine(" * @author zhubenle");
        innerClass.addJavaDocLine(" */");
    }

    /**
     * 添加类的作者和时间注释
     *
     * @param javaElement
     *         {@link JavaElement}
     * @param introspectedTable
     *         {@link IntrospectedTable}
     */
    public void addClassComment(JavaElement javaElement, IntrospectedTable introspectedTable) {
        javaElement.addJavaDocLine("/**");
        javaElement.addJavaDocLine(" * <br/>");
        javaElement.addJavaDocLine(" * Created on " + simpleDateFormat.format(new Date()));
        javaElement.addJavaDocLine(" * ");
        javaElement.addJavaDocLine(" * @author zhubenle");
        javaElement.addJavaDocLine(" */");
    }
}
