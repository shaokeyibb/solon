package org.noear.solon.extend.sessionstate.jwt;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Solon.global().enableSessionState() == false) {
            return;
        }

        if (Bridge.sessionStateFactory().priority() >= JwtSessionStateFactory.SESSION_STATE_PRIORITY) {
            return;
        }

        XServerProp.init();

        Bridge.sessionStateFactorySet(JwtSessionStateFactory.getInstance());

        System.out.println("solon:: Jwt session state plugin is loaded");
    }
}
