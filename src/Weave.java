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
		redefine("Stub");
		Stub fc = new Stub();
		System.out.println(fc.getClass());
		System.out.println("return : " + fc.hoge());
		
		defineStub("hoge", "return -1;");
		System.out.println(fc.getClass());
		System.out.println("return : " + fc.hoge());
		

	}

	public static void premain(Instrumentation inst) {

	}

	public static void redefine(String c) {
		Class<?> cl = makeClass(c);
		try {
		System.out.println(cl.newInstance().getClass().getDeclaredMethod("hoge").invoke(cl.newInstance(), new Object[0]));
		}catch (Exception ex) {
			
		}
		
		ClassPool cp = ClassPool.getDefault();

		try {
			CtClass target = cp.get(c);
			target.addField(CtField.make("private static java.lang.Object stub_clone = new "+cl.getName()+"();",target));
			CtMethod[] methods = target.getDeclaredMethods();
			for (CtMethod m : methods) {
				StringBuilder sb = new StringBuilder();
				sb.append("return ($r)stub_clone.getClass().getDeclaredMethod(\""+m.getName()+"\",$sig).invoke(stub_clone, $args);");
				m.setBody(""+sb);
			}
			target.addMethod(CtMethod.make("public static void set_Stub(Object stub) {stub_clone = stub;}", target));
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
	
	public static Class makeClass (String className) {
		ClassPool cp = ClassPool.getDefault();

		// create Stub_clone
		try {
			CtClass targetC = cp.get(className);
			targetC.setName("Stub_clone");
			return targetC.toClass(Thread.currentThread().getContextClassLoader());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
