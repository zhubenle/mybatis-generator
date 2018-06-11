package cn.mybatis.generator;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;

/**
 * <br/>
 * Created on 2018/6/1 21:54.
 *
 * @author zhubenle
 */
public class CustomShellCallback extends DefaultShellCallback {
    /**
     * Instantiates a new default shell callback.
     *
     * @param overwrite
     *         the overwrite
     */
    public CustomShellCallback(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public File getDirectory(String targetProject, String targetPackage) throws ShellException {
        try {
            targetProject = new File("").getCanonicalPath() + targetProject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.getDirectory(targetProject, targetPackage);
    }
}
