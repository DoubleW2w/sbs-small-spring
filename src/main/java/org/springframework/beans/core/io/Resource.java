package org.springframework.beans.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源处理接口：负责获取配置文件的输入流
 *
 * @author: DoubleW2w
 * @date: 2024/10/3
 * @project: sbs-small-spring
 */
public interface Resource {
  InputStream getInputStream() throws IOException;
}
