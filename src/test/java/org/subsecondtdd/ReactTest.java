package org.subsecondtdd;

import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ReactTest {
    @Test
    public void test_react() {
        NodeJS nodeJS = NodeJS.createNodeJS();
        V8Object reactApp = nodeJS.require(new File("src/main/javascript/react-app.js"));
        V8Array args = new V8Array(nodeJS.getRuntime());

        String html = reactApp.executeStringFunction("render", args);
        assertEquals("<div>Hello World</div>", html);

        while (nodeJS.isRunning()) {
            nodeJS.handleMessage();
        }

        args.release();
        reactApp.release();
        nodeJS.release();
    }
}