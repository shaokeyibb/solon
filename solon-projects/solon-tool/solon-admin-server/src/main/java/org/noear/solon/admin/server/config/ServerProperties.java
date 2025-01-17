package org.noear.solon.admin.server.config;

import lombok.Data;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * 配置文件
 *
 * @author shaokeyibb
 * @since 2.3
 */
@Inject(value = "${solon.admin.server}", required = false)
@Configuration
@Data
public class ServerProperties {

    private boolean enabled = true;

    private String mode = "local";

    private long heartbeatInterval = 10 * 1000;

    private long clientMonitorPeriod = 2 * 1000;

    private long connectTimeout = 5 * 1000;

    private long readTimeout = 5 * 1000;
}
