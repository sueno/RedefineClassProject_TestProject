import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Target;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Loader;

@SuppressWarnings("all")
public class Weave {

	// TODO TestMethod. This method remove after test.
	public static void main(String[] args) {
		Class c = makeClass();
		redefine(c,"hoge");
		Stub fc = new Stub();
		System.out.println(fc.getClass());
		System.out.println("return : " + fc.hoge());
		
		defineStub("hoge", "return -1;");
		System.out.println(fc.getClass());
		System.out.println("return : " + fc.hoge());
		

	}

	public static void premain(Instrumentation inst) {

	}

	public static void redefine(Class c, String methodName) {
		ClassPool cp = ClassPool.getDefault();

		try {
			CtClass target = cp.get("Stub");
			target.addField(CtField.make("private static Stub_clone cl = new Stub_clone();",
					target));
			CtMethod[] methods = target.getDeclaredMethods();
			for (CtMethod m : methods) {
				m.insertBefore("return cl."+m.getName()+"($$);");
			}
			target.addMethod(CtMethod.make("public static void set_Stub(Object stub) {cl = (Stub_clone)stub;}", target));
			target.toClass(Thread.currentThread().getContextClassLoader());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void defineStub(String methodName, String methodValue) {
		try {
			Object o = define(methodName,methodValue).newInstance();
			Class c = Class.forName("Stub");
			Method[] mm = c.getDeclaredMethods();
			Method m = c.getDeclaredMethod("set_Stub",Object.class);
			m.invoke(null, o);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public static Class define(String methodName, String methodValue) {
		ClassPool cp = ClassPool.getDefault();

		// create Stub_clone
		try {
			CtClass targetC = cp.get("Stub_clone");
			targetC.defrost();
//			targetC.setName("Dummy");
//			targetC.setSuperclass();
			CtMethod targetM = targetC.getDeclaredMethod(methodName);
			targetM.insertBefore(methodValue);
			
			return targetC.toClass(new Loader());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static Class makeClass () {
		ClassPool cp = ClassPool.getDefault();

		// create Stub_clone
		try {
			CtClass targetC = cp.get("Stub");
			targetC.setName("Stub_clone");
			return targetC.toClass(Thread.currentThread().getContextClassLoader());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
