package org.subsecondtdd.j2v8;

import com.eclipsesource.v8.NodeJS;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsProxyTest {
    static NodeJS node = NodeJS.createNodeJS();
    static JsProxy v8Util = new JsProxy(node);


    @Test
    public void creates_v8_proxy() {
        Widget widget = v8Util.create("src/test/java/org/subsecondtdd/j2v8/MyWidget.js", Widget.class);
        assertEquals("Widget Foo", widget.description("Foo"));
    }

    @Test
    public void creates_v8_proxy_with_string_ctor_args() {
        Widget widget = v8Util.create("src/test/java/org/subsecondtdd/j2v8/MyWidget.js", Widget.class, "99");
        assertEquals("99", widget.id());
    }

    @Test
    public void creates_v8_proxy_with_object_ctor_args() {
        Wobble wobble = new Wobble();
        Widget widget = v8Util.create("src/test/java/org/subsecondtdd/j2v8/MyWidget.js", Widget.class, "77", wobble);
        assertEquals(88, widget.wobble());
    }

    @Test
    public void passes_java_object_to_function() {
        Wobble wobble = new Wobble();
        Widget widget = v8Util.create("src/test/java/org/subsecondtdd/j2v8/MyWidget.js", Widget.class);
        assertEquals(88, widget.takeWobble(wobble));
    }

    @Test
    public void js_can_call_java_method_with_js_object() {
        Wobble wobble = new Wobble();
        Widget widget = v8Util.create("src/test/java/org/subsecondtdd/j2v8/MyWidget.js", Widget.class, null, wobble);
        widget.setWobbleThing();
        assertEquals(new Thing("My Thing"), wobble.getThing());
    }

}
