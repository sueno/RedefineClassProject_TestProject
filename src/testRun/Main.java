package testRun;
import info.nohoho.weave.Weave;


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
