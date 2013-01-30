RedefineClassProject (試作)
=========================

動作例
---

Main.java
```
public class Main {
	public static void main(String[] args) {
		
		//Weaveクラスを変更可能に
		Weave.redefineable("Stub");
		
		//Stubクラスを生成
		Stub fc = new Stub();
		
		//通常の呼び出し
		System.err.println("Called Stub.hoge()");
		System.out.println("return : " + fc.hoge());
		
		//戻り値の値を変更
		Weave.defineStub("hoge", "return -1;");
		System.err.println("\nDefine Stub.hoge() {return -1;}");
		System.err.println("Called Stub.hoge()");
		System.out.println("return : " + fc.hoge());

		//例外(NullPointerException)を吐くようにする
		try {
			System.err.println("\nDefine Stub.hoge() {throw new NullPointerException(\"嘘だよ\");}");
			Weave.defineStub("hoge", "throw new NullPointerException(\"嘘だよ\");");
			System.err.println("Called Stub.hoge()");
			System.out.println("return : " + fc.hoge()+"\n");
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		
		//例外(IllegalArgumentException)を吐くようにする
		try {
			Weave.defineStub("hoge", "throw new IllegalArgumentException(\"嘘だよ\");");
			System.err.println("\nDefine Stub.hoge() {throw new IllegalArgumentException(\"嘘だよ\");}");
			System.err.println("Called Stub.hoge()");
			System.out.println("return : " + fc.hoge()+"\n");
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

	}
}
```

Stub.java
```
public class Stub {
	public int hoge () {
		return 1;
	}
}
```

実行結果
```
Called Stub.hoge()
return : 1

Define Stub.hoge() {return -1;}
Called Stub.hoge()
return : -1

Define Stub.hoge() {throw new NullPointerException("嘘だよ");}
Called Stub.hoge()
java.lang.NullPointerException: 嘘だよ
	at Stub_clone.hoge(Stub.java)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at Stub.hoge(Stub.java)
	at Main.main(Main.java:32)

Define Stub.hoge() {throw new IllegalArgumentException("嘘だよ");}
Called Stub.hoge()
Exception in thread "main" java.lang.IllegalArgumentException: 嘘だよ
	at Stub_clone.hoge(Stub.java)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
	at java.lang.reflect.Method.invoke(Method.java:597)
	at Stub.hoge(Stub.java)
	at Main.main(Main.java:42)
```