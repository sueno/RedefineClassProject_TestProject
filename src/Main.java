
public class Main {
	public static void main(String[] args) {
		
		//Weaveクラスを変更可能に
		Weave.redefine("Stub");
		
		//Weaveクラスを生成
		Stub fc = new Stub();
		
		//通常の呼び出し
		System.err.println("Called Stub.hoge()");
		System.out.println("return : " + fc.hoge());

		//戻り値の値を変更
		Weave.defineStub("hoge", "return -1;");
		System.err.println("Define Stub.hoge() {return -1;}");
		System.err.println("Called Stub.hoge()");
		System.out.println("return : " + fc.hoge());

		//例外(NullPointerException)を吐くようにする
		try {
			Weave.defineStub("hoge", "throw new NullPointerException(\"嘘だよ\");");
			System.err.println("Define Stub.hoge() {throw new NullPointerException();}");
			System.err.println("Called Stub.hoge()");
			System.out.println("return : " + fc.hoge());
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		
		//例外(IllegalArgumentException)を吐くようにする
		try {
			Weave.defineStub("hoge", "throw new IllegalArgumentException(\"嘘だよ\");");
			System.err.println("Define Stub.hoge() {throw new NullPointerException();}");
			System.err.println("Called Stub.hoge()");
			System.out.println("return : " + fc.hoge());
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

	}
}
