import static org.junit.Assert.*;

import org.junit.Test;


public class TestDriver {

	static {
		Weave.redefineable("Stub");
	}
	
	@Test
	public void test_default() {
		Stub fc = new Stub();
		assertEquals(1,fc.hoge());
	}
	
	@Test
	public void test_changeReturn() {
		Stub fc = new Stub();
		Weave.defineTarget("hoge", "return -1;");
		assertEquals(-1,fc.hoge());

	}
	
	@Test
	public void test_addThrow() {
		try {
			Stub fc = new Stub();
			Weave.defineTarget("hoge", "throw new NullPointerException();");
			fail("expected:<NullPointerException> but was <"+fc.hoge()+">");
		} catch (NullPointerException expected) {
		} catch (Throwable th) {
			fail("expected:<NullPointerException> but was <"+th.getClass().getName()+"");
		}
	}

}
