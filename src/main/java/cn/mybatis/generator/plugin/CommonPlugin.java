package cn.mybatis.generator.plugin;

import cn.mybatis.generator.constant.CommonConst;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <br/>
 * create time 2017年4月13日 上午12:01:33
 *
 * @author t0mpi9
 */
public class CommonPlugin extends PluginAdapter {

    private XmlElement sqlMapSelectByExampleWithoutBLOBsElement;

    private final static String OPTIMIZE_PAGE_SQL_ID = "selectByExampleOptimizePage";

    private MyCommentGenerator commentGenerator;
    private FullyQualifiedJavaType baseMapperType;
    /**
     * 更改原有的Mapper后缀名
     */
    private String newMapperSubName;
    /**
     * 是否创建复杂优化分页sql
     */
    private Boolean createOptimizePageSql = false;
    /**
     * 是否为mapper对象添加spring注解
     */
    private Boolean addBaseMapperRepositoryAnnotation = true;
    /**
     * 是否创建读写分离的mapper
     */
    private Boolean createReadAndEditMapper = false;

    @Override
    public boolean validate(List<String> arg0) {
        boolean valid = true;
        if (!StringUtility.stringHasValue(properties.getProperty(CommonConst.TARGET_PROJECT)) && createReadAndEditMapper) {
            arg0.add(Messages.getString(CommonConst.VALIDATION_ERROR_18, CommonConst.COMMON_PLUGIN, CommonConst.TARGET_PROJECT));
            valid = false;
        }
        if (!StringUtility.stringHasValue(properties.getProperty(CommonConst.TARGET_EDIT_MAPPER_PACKAGE)) && createReadAndEditMapper) {
            arg0.add(Messages.getString(CommonConst.VALIDATION_ERROR_18, CommonConst.COMMON_PLUGIN, CommonConst.TARGET_EDIT_MAPPER_PACKAGE));
            valid = false;
        }
        if (!StringUtility.stringHasValue(properties.getProperty(CommonConst.TARGET_READ_MAPPER_PACKAGE)) && createReadAndEditMapper) {
            arg0.add(Messages.getString(CommonConst.VALIDATION_ERROR_18, CommonConst.COMMON_PLUGIN, CommonConst.TARGET_READ_MAPPER_PACKAGE));
            valid = false;
        }
        return valid;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        newMapperSubName = properties.getProperty(CommonConst.NEW_MAPPER_SUB_NAME);
        createOptimizePageSql = Boolean.parseBoolean(properties.getProperty(CommonConst.CREATE_OPTIMIZE_PAGE_SQL));
        addBaseMapperRepositoryAnnotation = Boolean.parseBoolean(properties.getProperty(CommonConst.ADD_BASE_MAPPER_REPOSITORY_ANNOTATION));
        createReadAndEditMapper = Boolean.parseBoolean(properties.getProperty(CommonConst.CREATE_READ_AND_EDIT_MAPPER));
        commentGenerator = (MyCommentGenerator) context.getCommentGenerator();

        if (!StringUtility.stringHasValue(newMapperSubName)) {
            newMapperSubName = CommonConst.MAPPER;
        }
        introspectedTable.setMyBatis3JavaMapperType(introspectedTable.getMyBatis3JavaMapperType().replaceAll(CommonConst.MAPPER, newMapperSubName));
        super.initialized(introspectedTable);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> answer = new ArrayList<>(1);
        if (createReadAndEditMapper) {
            String targetProject = properties.getProperty(CommonConst.TARGET_PROJECT);
            String targetEditPackage = properties.getProperty(CommonConst.TARGET_EDIT_MAPPER_PACKAGE);
            String targetReadPackage = properties.getProperty(CommonConst.TARGET_READ_MAPPER_PACKAGE);

            String baseMapperStr = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
            String editMapper = baseMapperStr + "Edit" + newMapperSubName;

            Interface interfazeEdit = new Interface(targetEditPackage + "." + editMapper);
            interfazeEdit.addSuperInterface(baseMapperType);
            interfazeEdit.addImportedType(baseMapperType);
            interfazeEdit.setVisibility(JavaVisibility.PUBLIC);
            interfazeEdit.addAnnotation(CommonConst.REPOSITORY_ANNOTATION_NAME);
            interfazeEdit.addImportedType(new FullyQualifiedJavaType(CommonConst.REPOSITORY_FULL_TYPE_NAME));

            MyGeneratedJavaFile myGeneratedJavaFileEdit = new MyGeneratedJavaFile(interfazeEdit, targetProject, targetEditPackage, CommonConst.UTF_8,
                    context.getJavaFormatter());

            String readMapper = baseMapperStr + "Read" + newMapperSubName;
            Interface interfazeRead = new Interface(targetReadPackage + "." + readMapper);
            interfazeRead.addSuperInterface(baseMapperType);
            interfazeRead.addImportedType(baseMapperType);
            interfazeRead.setVisibility(JavaVisibility.PUBLIC);
            interfazeRead.addAnnotation(CommonConst.REPOSITORY_ANNOTATION_NAME);
            interfazeRead.addImportedType(new FullyQualifiedJavaType(CommonConst.REPOSITORY_FULL_TYPE_NAME));

            MyGeneratedJavaFile myGeneratedJavaFileRead = new MyGeneratedJavaFile(interfazeRead, targetProject, targetReadPackage, CommonConst.UTF_8,
                    context.getJavaFormatter());

            answer.add(myGeneratedJavaFileRead);
            answer.add(myGeneratedJavaFileEdit);
        }
        return answer;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        List<GeneratedXmlFile> newResults = introspectedTable.getGeneratedXmlFiles().stream()
                .peek(generatedXmlFile -> generatedXmlFile.setMergeable(false))
                .collect(Collectors.toList());
        introspectedTable.getGeneratedXmlFiles().clear();
        introspectedTable.getGeneratedXmlFiles().addAll(newResults);
        return super.contextGenerateAdditionalXmlFiles(introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        baseMapperType = interfaze.getType();

        //增加类作者和时间注释
        commentGenerator.addClassComment(interfaze, introspectedTable);

        if (createOptimizePageSql) {
            Method method = new Method();
            method.setName(OPTIMIZE_PAGE_SQL_ID);
            for (Method m : interfaze.getMethods()) {
                if ("selectByExample".equals(m.getName())) {
                    method.setReturnType(m.getReturnType());
                    method.addParameter(m.getParameters().get(0));
                }
            }
            commentGenerator.addGeneralMethodComment(method, introspectedTable);
            interfaze.addMethod(method);
        }

        if (addBaseMapperRepositoryAnnotation && !createReadAndEditMapper) {
            interfaze.addImportedType(new FullyQualifiedJavaType(CommonConst.REPOSITORY_FULL_TYPE_NAME));
            interfaze.addAnnotation(CommonConst.REPOSITORY_ANNOTATION_NAME);
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //example对象添加limit相关
        addExampleLimit(topLevelClass, "limitStart");
        addExampleLimit(topLevelClass, "limitEnd");

        //增加类作者和时间注释
        commentGenerator.addClassComment(topLevelClass, introspectedTable);
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        //xml添加limit相关
        addXmlLimit(element);
        return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        sqlMapSelectByExampleWithoutBLOBsElement = new XmlElement("select");
        for (Attribute a : element.getAttributes()) {
            if (!"id".equals(a.getName())) {
                sqlMapSelectByExampleWithoutBLOBsElement.addAttribute(new Attribute(a.getName(), a.getValue()));
            } else {
                sqlMapSelectByExampleWithoutBLOBsElement.addAttribute(new Attribute("id", OPTIMIZE_PAGE_SQL_ID));
            }
        }
        for (Element e : element.getElements()) {
            if (e instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement) e;
                if ("orderByClause != null".equals(xmlElement.getAttributes().get(0).getValue())) {
                    sqlMapSelectByExampleWithoutBLOBsElement.addElement(new TextElement("order by id desc"));
                } else {
                    sqlMapSelectByExampleWithoutBLOBsElement.addElement(new XmlElement(xmlElement));
                }
            } else {
                TextElement textElement = (TextElement) e;
                sqlMapSelectByExampleWithoutBLOBsElement.addElement(new TextElement(textElement.getContent()));
            }
        }
        addXmlLimit(element);
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (createOptimizePageSql) {
            createOptimizeXml(sqlMapSelectByExampleWithoutBLOBsElement, introspectedTable);
            document.getRootElement().addElement(sqlMapSelectByExampleWithoutBLOBsElement);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * 在xml中增加基本分页
     *
     * @param element
     *         {@link XmlElement}
     */
    private void addXmlLimit(XmlElement element) {
        //基本分页sql
        XmlElement ifXmlElement = new XmlElement("if");
        ifXmlElement.addAttribute(new Attribute("test", "limitStart != -1 and limitEnd != -1"));
        ifXmlElement.addElement(new TextElement("limit #{limitStart}, #{limitEnd}"));
        element.addElement(ifXmlElement);
    }

    /**
     * 在xml中增加复杂分页
     *
     * @param element
     *         {@link XmlElement}
     * @param introspectedTable
     *         {@link IntrospectedTable}
     */
    private void createOptimizeXml(XmlElement element, IntrospectedTable introspectedTable) {
        //复杂优化分页sql
        List<Element> elements = element.getElements();
        for (Element e : elements) {
            if (e instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement) e;
                if ("if".equals(xmlElement.getName()) && "_parameter != null".equals(xmlElement.getAttributes().get(0).getValue())) {
                    XmlElement ifXmlElement = new XmlElement("if");
                    ifXmlElement.addAttribute(new Attribute("test", "limitStart != -1"));
                    String sb = "and " + introspectedTable.getFullyQualifiedTable().getAlias() + ".id &lt;= (" +
                            "select " + introspectedTable.getFullyQualifiedTable().getAlias() + ".id " +
                            "from " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
                    ifXmlElement.addElement(new TextElement(sb));

                    XmlElement includeElement = new XmlElement("include");
                    includeElement.addAttribute(new Attribute("refid", introspectedTable.getExampleWhereClauseId()));
                    ifXmlElement.addElement(includeElement);

                    ifXmlElement.addElement(new TextElement("order by id desc"));

                    ifXmlElement.addElement(new TextElement("limit #{limitStart}, 1)"));
                    xmlElement.addElement(ifXmlElement);
                }
            }
        }

        XmlElement ifXmlElement2 = new XmlElement("if");
        ifXmlElement2.addAttribute(new Attribute("test", "limitEnd != -1"));
        ifXmlElement2.addElement(new TextElement("limit #{limitEnd}"));
        element.addElement(ifXmlElement2);
    }

    /**
     * 为example对象添加limit相关
     *
     * @param topLevelClass
     *         {@link TopLevelClass}
     * @param name
     *         {@link String}
     */
    private void addExampleLimit(TopLevelClass topLevelClass, String name) {
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(FullyQualifiedJavaType.getIntInstance());
        field.setName(name);
        field.setInitializationString("-1");
        topLevelClass.addField(field);
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), name));
        method.addBodyLine("this." + name + " = " + name + ";");
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        topLevelClass.addMethod(method);
    }
}
