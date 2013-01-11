import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;


public class CreateMethod {
	
	public static void main(String args[]) {
		String targetClass = "Stub";
		String targetMethod = "calc";
		Class[] paramType = new Class[]{int[].class};
		Object[] paramObj = new Object[]{new int[]{3,2}};
		Object returnObj;
		
		boolean b = addProxyMethod(targetClass);
		for (Method m : Stub.class.getDeclaredMethods()) {
			System.out.print(""+m.getName()+"()");
			for (Class c : m.getParameterTypes()) {
				System.out.print(" :"+c.getName());
			}
			System.out.println("");
		}
		System.out.println("");
//		try {
//			Stub s = new Stub();
//			System.out.println("Default Method Call Test .. "+targetMethod+"(10).\nDefaultResult : "+s.calc(10)+"\n");
//			System.out.println("Define Method Test..");
//			Class cl = Stub.class;
//			Object obj = cl.newInstance();
//			Method callMethod = cl.getDeclaredMethod(targetMethod+"_Proxy",paramType);
//			Method getterMethod = cl.getDeclaredMethod("getter_"+targetMethod, new Class[0]);
//			callMethod.invoke(obj,paramObj);
//			returnObj = (Integer)getterMethod.invoke(obj, new Object[0]);
//			System.out.println("Result : "+returnObj);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		System.out.println(b);
	}
	
	public static boolean addProxyMethod (String className) {
		try {
			ClassPool cp = ClassPool.getDefault();
			CtClass cl = cp.get(className);
			for (CtMethod m : cl.getDeclaredMethods()) {
				addProxyMethod(cl, m);
			}
			return true;
		}catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	/**
	 * 
	 * @param targetClass
	 * @param targetMethod
	 * @return
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	public static boolean addProxyMethod (CtClass targetClass, CtMethod targetMethod) throws CannotCompileException,NotFoundException{
		/**
		 * staticメソッドの場合
		 */
		String accessP = "";
		
		/**
		 * フィールドの定義
		 * フィールド名 = 
		 */
		StringBuilder methodName = new StringBuilder();
		methodName.append(targetMethod.getName());
		for (CtClass c : targetMethod.getParameterTypes()) {
			methodName.append(("_"+c.getName()).replaceAll("\\[|\\]", "_"));
		}
		String fieldName = methodName+"_Field";
		String field = "private "+accessP+targetMethod.getReturnType().getName()+" "+fieldName+";";
		targetClass.addField(CtField.make(field, targetClass));
		
		/**
		 * 対象メソッドを呼び出す戻り値voidメソッドの定義
		 * int hoge() => void hoge()
		 * 本来の戻り値は上で定義したフィールドに格納
		 */
		StringBuilder paramSB = new StringBuilder();
		StringBuilder argSB = new StringBuilder();
		CtClass[] paramTypes = targetMethod.getParameterTypes();
		for (int i = 0; i<paramTypes.length; ++i) {
			paramSB.append(paramTypes[i].getName()+" arg"+i+" ");
			argSB.append("arg"+i+" ");
		}
		String methodsrc = "public "+accessP+"void "+methodName+"_Proxy("+paramSB+") {"
				+fieldName+" = "+targetMethod.getName()+"("+argSB+");}";
		targetClass.addMethod(CtMethod.make(methodsrc, targetClass));
		
		/**
		 * 対象メソッドの戻り値を返すメソッド (getter)
		 * メソッド名は getter_対象メソッドの名前  ※引数の型は対象メソッドと同一！
		 * hoge(int a) => getter_hoge(int a)
		 */
		String proxy_methodsrc = "public "+accessP+targetMethod.getReturnType().getName()+" getter_"+methodName+"("+paramSB+") {"
				+"return "+fieldName+";}";
		targetClass.addMethod(CtMethod.make(proxy_methodsrc, targetClass));
		
		return true;
	}
	
}
