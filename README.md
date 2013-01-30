RedefineClassProject (試作)
=========================
実行時にクラスを何度でも再定義可能にするライブラリです．

メソッド単位での再定義が可能です．

なお，クラスを再定義すると，再定義前のクラスのインスタンスへの参照は無くなり，
再定義後新たに生成されたクラスのインスタンスが参照されます．

javassistライブラリが必要です．

用途
---
・ユニットテストのスタブ等

制約事項
---
・継承不可能なクラスは再定義出来ません(今後改善)

・コンストラクタに引数をもつものは再定義出来ません(未実装)

・フィールドを持つものは再定義を推奨しません(再定義ごとに値が初期化されるため，今後改善)

・試作版なので，その他予期せぬエラーが発生します．


動作例
---

Main.java
```
public class Main {
	public static void main(String[] args) {
		
		//Stubクラスを変更可能に
		Weave.redefineable("Stub");
		
		//Stubクラスを生成
		Stub fc = new Stub();
		
		//通常の呼び出し
		System.err.println("Called Stub.hoge()");
		System.out.println("return : " + fc.hoge());
		
		try  {
		Thread.currentThread().sleep(100);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		//戻り値の値を変更
		Weave.defineTarget("hoge", "return -1;");
		System.err.println("\nDefine Stub.hoge() {return -1;}");
		System.err.println("Called Stub.hoge()");
		System.out.println("return : " + fc.hoge());

		//例外(NullPointerException)を吐くようにする
		try {
			System.err.println("\nDefine Stub.hoge() {throw new NullPointerException(\"嘘だよ\");}");
			Weave.defineTarget("hoge", "throw new NullPointerException(\"嘘だよ\");");
			System.err.println("Called Stub.hoge()");
			System.out.println("return : " + fc.hoge()+"\n");
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		
		//例外(IllegalArgumentException)を吐くようにする
		try {
			Weave.defineTarget("hoge", "throw new IllegalArgumentException(\"嘘だよ\");");
			System.err.println("\nDefine Stub.hoge() {throw new IllegalArgumentException(\"嘘だよ\");}");
			System.err.println("Called Stub.hoge()");
			System.out.println("return : " + fc.hoge()+"\n");
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

		System.out.println("end");
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

詳細
---
再定義対象とするクラスは，Proxy化し，実体(対象クラスを継承したクラス)への参照を持ちます．

再定義ごとに，実体が生成されます．実体の名前は「実体クラス名_clone」です．

内部でリフレクションを用いている，動的に生成したクラスを使用していることから，本来のクラス構成とはだいぶ異なります．(エラー文には注意)
