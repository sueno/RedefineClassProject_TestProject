
public class Main {
	public static void main(String[] args) {
		Weave.redefine("Stub");
		Stub fc = new Stub();
		System.err.println("Called Stub.hoge()");
		System.out.println("return : " + fc.hoge());

		Weave.defineStub("hoge", "return -1;");
		System.err.println("Define Stub.hoge() {return -1;}");
		System.err.println("Called Stub.hoge()");
		System.out.println("return : " + fc.hoge());

		try {
			Weave.defineStub("hoge", "throw new NullPointerException();");
			System.err.println("Define Stub.hoge() {throw new NullPointerException();}");
			System.err.println("Called Stub.hoge()");
			System.out.println("return : " + fc.hoge());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
