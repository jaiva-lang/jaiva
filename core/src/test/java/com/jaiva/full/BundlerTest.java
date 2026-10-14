package com.jaiva.full;

import com.jaiva.JBundler;
import com.jaiva.errors.JaivaException;
import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import com.jaiva.interpreter.runtime.IConfig;
import com.jaiva.interpreter.symbol.BaseFunction;
import com.jaiva.interpreter.symbol.BaseVariable;
import com.jaiva.interpreter.symbol.CallProperties;
import com.jaiva.tokenizer.tokens.Token;
import com.jaiva.tokenizer.jdoc.JDoc;
import com.jaiva.tokenizer.tokens.specific.TFunction;
import com.jaiva.tokenizer.tokens.specific.TStringVar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.jaiva.full.Files.BUNDLER_JIV;

@PublicLibrary(path = "customPath")
class CustomLib extends BaseLibrary {

    public CustomLib(IConfig<Object> config) throws JaivaException {
        add(new FEcho());
        add(new VVar());
    }

    static class FEcho extends BaseFunction {
        public FEcho() throws JaivaException {
            super("echo", new TFunction("echo", new String[] {"arg1"}, null, -1, JDoc.from("d")));
            freeze();
        }

        @Override
        public Object call(CallProperties callProperties) throws Exception {
            this.checkParams(callProperties.tFuncCall(), callProperties.scope());
            return callProperties.params().getFirst();
        }
    }

    static class VVar extends BaseVariable {
        public VVar() throws JaivaException {
            super("poop", new TStringVar("poop", "factuality", -1, JDoc.from("The best string")), "facuality");
            freeze();
        }
    }
}

public class BundlerTest {

    @Test
    void test1() {
        ArrayList<Token<?>> tokens = new ArrayList<>();
        try {
            JBundler jb = new JBundler(BUNDLER_JIV.toString(), CustomLib.class);
            jb.run(null);

            // DebugException was not thrown. Test failure.
            Assertions.fail("Expected DebugException, but no exception was thrown.");
        } catch (JaivaException.DebugException e) {
            // check that there is no error
            Assertions.assertNull(e.error, "An error ocurred and was saved into DebugException.error");

            System.out.println(e.components);
        } catch (Exception e) {
            // catch any other exception that we realistically don't want to catch
            Assertions.fail("Exception thrown: " + e.getMessage(), e);
        }
    }
}
