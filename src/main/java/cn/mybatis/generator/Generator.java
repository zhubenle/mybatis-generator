package cn.mybatis.generator;

import org.mybatis.generator.config.xml.ConfigurationParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 * create time 2017年4月13日 上午12:01:33
 *
 * @author zhubenle
 */
public class Generator {

	public static void main(String[] args) {
		try {
			List<String> warnings = new ArrayList<>();
			File configFile = new File(Generator.class.getClassLoader().getResource("generator.xml").getFile());
			CustomShellCallback customShellCallback = new CustomShellCallback(true);
			CustomMybatisGenerator customMybatisGenerator = new CustomMybatisGenerator(new ConfigurationParser(warnings).parseConfiguration(configFile),
					customShellCallback, warnings);
			customMybatisGenerator.generate(null);

			warnings.forEach(System.err::println);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
